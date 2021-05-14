package consumers;

import com.google.gson.JsonParser;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

import static constants.Constants.*;
import static utils.Utils.*;

public class Consumer {

  /**
   * Create ElasticSearch client to use ES
   *
   * @return RestHighLevelClient instance
   */
  public static RestHighLevelClient createClient() {

    final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
    credentialsProvider.setCredentials(
        AuthScope.ANY,
        new UsernamePasswordCredentials(ELASTIC_SEARCH_USERNAME, ELASTIC_SEARCH_PASSWORD));

    RestClientBuilder builder =
        RestClient.builder(new HttpHost(ELASTIC_SEARCH_HOSTNAME, 443, "https"))
            .setHttpClientConfigCallback(
                httpClientBuilder ->
                    httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider));
    return new RestHighLevelClient(builder);
  }

  /**
   * Creates a Kafka Consumer.
   *
   * <p>Receives tweets from the producer as messages to be fed into ElasticSearch.
   *
   * @return a Kafka consumer instance that is subscribed to the topic "tweets"
   */
  public static KafkaConsumer<String, String> createConsumer(String topic) {
    Properties properties = new Properties();
    properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
    properties.setProperty(
        ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
    properties.setProperty(
        ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
    properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
    properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    properties.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
    properties.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "5");

    KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(properties);
    consumer.subscribe(Collections.singletonList(topic));
    return consumer;
  }

  public static void main(String[] args) throws IOException {
    String twitterTopic = "tweets";

    Logger logger = LoggerFactory.getLogger(Consumer.class.getName());
    RestHighLevelClient client = createClient();

    KafkaConsumer<String, String> consumer = createConsumer(twitterTopic);
    // Poll in for new data
    // TODO: Refactor later. Still just a WIP.
    while (true) {
      try {
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
        logger.info("Received " + records.count() + " records.");
        for (ConsumerRecord<String, String> record : records) {
          // Extract twitter messages from each record value.
          String jsonStringPayload = record.value();
          String id = generateId(record) + extractIdStrFromTweet(jsonStringPayload);
          IndexRequest request =
              new IndexRequest("twitter", twitterTopic, id)
                  .source(jsonStringPayload, XContentType.JSON);
          client.index(request, RequestOptions.DEFAULT);
          logger.info(id);
          sleepInMilliseconds(10);
        }
        logger.info("Committing offsets ...");
        consumer.commitSync();
        logger.info("Offsets have now been committed.");
        sleepInMilliseconds(1500);
      } catch (Exception e) {
        logger.error("An exception occurred: ", e);
      }
    }
    // TODO: uncomment later. Removed to avoid error so the app can run (temporary thing)
    //    client.close();
  }
}
