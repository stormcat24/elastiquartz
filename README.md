elastiquartz
==========

[![Circle CI](https://circleci.com/gh/stormcat24/elastiquartz.svg?style=shield&circle-token=d6f3ed9b32da3b47773715100fe6e66e72636426)](https://circleci.com/gh/stormcat24/elastiquartz)
[![License: MIT](http://img.shields.io/badge/license-MIT-orange.svg)](LICENSE)

elastiquartz is cron job schedular on Amazon Web Services(AWS).

### Required Environment Variables

elastiquartz requires several environment variables. This can be easily operated as Docker container.

|name|description|example|
|:---|:---|:---|
|CRON_LOCATION_TYPE|s3|storage system to place cron definition file|
|CRON_LOCATION|your_bucket_name|if s3, specify bucket name|
|CRON_TARGET|cron_file|cron definition yml file path. if s3://your_bucket_name/cron_file.yml, you must specify cron_file|
|EVENT_TARGET_TYPE|sqs|Destination of message|

### Cron Definition File(YAML)

In elastiquartz, definiton of cron and message to be sent described in yaml format.

```Ruby
sqs001:
  -
    cron: 0 0/5 * * * ?
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

Notation of cron expression is the same as [Quartz](http://quartz-scheduler.org/documentation/quartz-2.x/tutorials/tutorial-lesson-06).


License
===
See [LICENSE](LICENSE).

Copyright © Akinori Yamada([@stormcat24](https://twitter.com/stormcat24)). All Rights Reserved.
