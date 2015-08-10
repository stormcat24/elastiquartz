elastiquartz
==========

[![Docker Hub](http://dockeri.co/image/stormcat24/elastiquartz)](https://hub.docker.com/r/stormcat24/elastiquartz/)

[![Circle CI](https://circleci.com/gh/stormcat24/elastiquartz.svg?style=shield&circle-token=d6f3ed9b32da3b47773715100fe6e66e72636426)](https://circleci.com/gh/stormcat24/elastiquartz)
[![License: MIT](http://img.shields.io/badge/license-MIT-orange.svg)](LICENSE)

elastiquartz is cron job schedular on Amazon Web Services(AWS).

### Disclaimer

elastiquartz support only aws currently. Work in progress.

### Required Environment Variables

elastiquartz requires several environment variables. This can be easily operated as Docker container.

|name|example|description|
|:---|:---|:---|
|CRON_LOCATION_TYPE|s3|storage system to place cron definition file|
|CRON_LOCATION|your_bucket_name|if s3, specify bucket name|
|CRON_TARGET|cron_file|cron definition yml file path. if s3://your_bucket_name/cron_file.yml, you must specify cron_file|
|EVENT_TARGET_TYPE|sqs|Destination of message|
|AWS_REGION|us-west-2|your AWS region|
|AWS_ACCESS_KEY|your_access_key|AWS access key|
|AWS_SECRET_KEY|your_secret_key|AWS secret key|

### Cron Definition File(YAML)

In elastiquartz, definiton of cron and message to be sent described in yaml format.

```Ruby
sqs001:
  -
    cron: 0 0-5 14 * * ?
    # message to sqs
    message:
      job: job1
      params:
        param1: value1
        param2: value2
  -
    cron: 0 0/5 * * * ?
    # message to SQS
    message:
      job: job2
      params:
        param1: value1
        param2: value2

```

Top level element is destination of message. You may specify Amazon SQS queue name.

Then below the top level should be array. Each elements should have `cron` and `message` element. Notation of cron expression is the same as [Quartz](http://quartz-scheduler.org/documentation/quartz-2.x/tutorials/tutorial-lesson-06).
`message` element is you free data. This data you have described here is delivered is converted to JSON.

If written as follows,

```Ruby
message:
  job: job2
  params:
    param1: value1
    param2: value2
```

It will be distributed to SQS as follows.

```JSON
{
  "job": "job2",
  "params": {
    "param1": "value1",
    "param2": "value2"
  }
}
```

### example of docker-compose.yml

As follows.

```Ruby
batch:
  image: stormcat24/elastiquartz:latest
  environment:
    CRON_LOCATION_TYPE: s3
    CRON_LOCATION: your_bucket_name
    CRON_TARGET: cron_file_name
    EVENT_TARGET_TYPE: sqs
    AWS_REGION: us-west-2
    AWS_ACCESS_KEY: your_access_key
    AWS_SECRET_KEY: your_secret_key
  volumes:
    # output /logs/elastiquartz.log in container.
    - /var/log/elastiquartz:/logs
```

License
===
See [LICENSE](LICENSE).

Copyright © Akinori Yamada([@stormcat24](https://twitter.com/stormcat24)). All Rights Reserved.
