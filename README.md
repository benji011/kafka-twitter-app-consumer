## About

A WIP proof of concept app that uses ElasticSearch as the consumer to get tweets as messages from the producer to then eventually be added onto an ES cluster. 

## Usage

Run [Consumer.java](./src/main/java/consumers/Consumer.java)

### Output
```bash
May 13, 2021 11:04:56 AM org.elasticsearch.client.RestClient logResponse
WARNING: request [POST <hostname>.bonsaisearch.net:443/twitter/tweets?timeout=1m] returned 1 warnings: [299 Elasticsearch-7.10.2-747e1cc71def077253878a59143c1f785afa92b9 "[types removal] Specifying types in document index requests is deprecated, use the typeless endpoints instead (/{index}/_doc/{id}, /{index}/_doc, or /{index}/_create/{id})."]
[main] INFO consumers.Consumer - <response-index-id>
```

Check response from ElasticSearch API using GET

```json
# GET /twitter/tweets/<response-index-id>

{
  "_index": "twitter",
  "_type": "tweets",
  "_id": "KKt4Y3kB1sAMrQyDS-Aa",
  "_version": 1,
  "_seq_no": 0,
  "_primary_term": 1,
  "found": true,
  "_source": {
    "hogehoge": "foobar"
  }
}
```