package utils;

import com.google.gson.JsonParser;
import org.apache.kafka.clients.consumer.ConsumerRecord;

/** Generic utility package to extract & manipulate stuff from the consumer. */
public class Utils {
  private Utils() {}

  private static final JsonParser jsonParser = new JsonParser();

  /**
   * Extracts the "id_str" from a tweet using Gson
   *
   * @param tweetJSON A JSON record of a tweet
   * @return String typed id
   */
  public static String extractIdStrFromTweet(String tweetJSON) {
    return jsonParser.parse(tweetJSON).getAsJsonObject().get("id_str").getAsString();
  }

  /**
   * Generates a unique Id from a consumer record
   *
   * @param record A consumer record instance
   * @return String value for the unique ID to be concatenated with a Twitter id_str
   */
  public static String generateId(ConsumerRecord<String, String> record) {
    return record.topic() + "_" + record.partition() + "_" + record.offset() + "_";
  }
}
