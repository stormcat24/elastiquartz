elastiquartz
==========

[![Circle CI](https://circleci.com/gh/stormcat24/elastiquartz.svg?style=shield&circle-token=d6f3ed9b32da3b47773715100fe6e66e72636426)](https://circleci.com/gh/stormcat24/elastiquartz)
[![License: MIT](http://img.shields.io/badge/license-MIT-orange.svg)](LICENSE)

elastiquartz is cron job schedular on Amazon Web Services(AWS).
 
 
elastiquartz use Quartz as 
 
```Ruby
sqs001:
  - 
    # cron expression
    cron: 0 0/5 * * * ?
    # message to SQS
    message:
      job: hoge
      
```