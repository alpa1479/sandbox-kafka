#### Quick start

1. Start Bitnami [Zookeeper + 2 Brokers + UI] run configuration
2. Run ```docker run -ti --name kafka-cli --rm --network host --entrypoint=bash bitnami/kafka:3.6.1```

#### Useful links

* https://docs.confluent.io/kafka/operations-tools/kafka-tools.html

#### Useful commands

* ps -ef | grep kafka
* docker rm -f $(docker ps -a -q)
* docker cp ./examples/cheatsheet/console-tools/configs/topics-to-move.json kafka-cli:/opt/bitnami/topics-to-move.json
* docker cp ./examples/cheatsheet/console-tools/configs/reassignment-plan.json kafka-cli:/opt/bitnami/reassignment-plan.json
* docker cp ./examples/cheatsheet/console-tools/configs/kafka-delete-records.json kafka-cli:/opt/bitnami/kafka-delete-records.json