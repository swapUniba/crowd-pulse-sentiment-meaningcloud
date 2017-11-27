crowd-pulse-sentiment-meaningcloud
============================

MeaningCloud based Crowd Pulse message sentiment analysis plugin.

----------------------------

The `sentiment-meaningcloud` plugin needs a `meaningcloud.properties` file in the class loader accessible 
resources directory, with the `meaningcloud.keys` value holding the SentIt API keys (comma-separated).

You have to specify the configuration option "calculate" with one of the following values:
- all: to calculate the sentiment of all messages coming from the stream;
- new: to calculate the sentiment of the messages with no sentiment (property is null);
- neuter: to calculate the sentiment of the messages with sentiment equals to 0.

Example of usage:

```json
{
  "process": {
    "name": "sentiment-tester",
    "logs": "/opt/crowd-pulse/logs"
  },
  "nodes": {
    "message-fetcher": {
      "plugin": "message-fetch",
      "config": {
        "db": "test-sentiment"
      }
    },
    "sentiment-analyzer": {
      "plugin": "sentiment-meaningcloud",
      "config": {
        "calculate": "new"
      }
    },
    "message-persister": {
      "plugin": "message-persist",
      "config": {
        "db": "test-sentiment"
      }
    }
  },
  "edges": {
    "message-fetcher": [
      "sentiment-analyzer"
    ],
    "sentiment-analyzer": [
      "message-persister"
    ]
  }
}
```