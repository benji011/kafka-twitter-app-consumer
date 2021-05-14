## About

A WIP proof of concept app that uses ElasticSearch as the consumer to get tweets as messages from the producer to then eventually be added onto an ES cluster. 

## Usage

Set up your environment variables by renaming `.env.example` to `.env`

Create an account with [ElasticSearch](https://www.elastic.co/) and obtain your consumer + access keys/secrets from [Twitter Developers](https://developer.twitter.com/)

```bash
CONSUMER_API_KEY=
CONSUMER_API_SECRET=
CONSUMER_BEARER_TOKEN=
ACCESS_TOKEN=
ACCESS_TOKEN_SECRET=

ELASTIC_SEARCH_ACCESS_KEY=
ELASTIC_SEARCH_ACCESS_SECRET=
ELASTIC_SEARCH_HOSTNAME=
ELASTIC_SEARCH_USERNAME=
ELASTIC_SEARCH_PASSWORD=
```

Run [Consumer.java](./src/main/java/consumers/Consumer.java)

### Output
```bash
May 13, 2021 11:04:56 AM org.elasticsearch.client.RestClient logResponse
WARNING: request [POST <hostname>.bonsaisearch.net:443/twitter/tweets?timeout=1m] returned 1 warnings: [299 Elasticsearch-7.10.2-747e1cc71def077253878a59143c1f785afa92b9 "[types removal] Specifying types in document index requests is deprecated, use the typeless endpoints instead (/{index}/_doc/{id}, /{index}/_doc, or /{index}/_create/{id})."]
[main] INFO consumers.Consumer - tweets_5_0_1392314710893150222
```

Check response from ElasticSearch API using GET

```json
# GET /twitter/tweets/tweets_5_0_1392314710893150222

{
  "_index": "twitter",
  "_type": "tweets",
  "_id": "tweets_5_0_1392314710893150222",
  "_version": 1,
  "_seq_no": 0,
  "_primary_term": 1,
  "found": true,
  "_source": {
    "hogehoge": "foobar"
  }
}
```

### Cleaning up

To remove all committed messages from ES, just send a `POST` request and delete by match all.

```json
# POST /twitter/tweets/_delete_by_query?conflicts=proceed
{
  "query": {
    "match_all": {}
  }
}
```

Sample response:
```json
{
  "took": 415,
  "timed_out": false,
  "total": 23,
  "deleted": 23,
  "batches": 1,
  "version_conflicts": 0,
  "noops": 0,
  "retries": {
    "bulk": 0,
    "search": 0
  },
  "throttled_millis": 0,
  "requests_per_second": -1,
  "throttled_until_millis": 0,
  "failures": []
}
```