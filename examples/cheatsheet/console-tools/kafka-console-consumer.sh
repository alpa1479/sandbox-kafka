# wont work in our example for case when we started only 1 broker and KAFKA_CFG_OFFSETS_TOPIC_REPLICATION_FACTOR=2
# that's why we need to start 2 brokers, or change config KAFKA_CFG_OFFSETS_TOPIC_REPLICATION_FACTOR to 1

# read messages from the beginning:
kafka-console-consumer.sh --bootstrap-server ${BOOTSTRAP_SERVER:-localhost:9092} --topic ${TOPIC_NAME:-console.tools.topic} \
--from-beginning

# read messages with timeout from the beginning [if specified consumer group doesn't have committed offset]:
kafka-console-consumer.sh --bootstrap-server ${BOOTSTRAP_SERVER:-localhost:9092} --topic ${TOPIC_NAME:-console.tools.topic} \
--from-beginning \
--group ${CONSUMER_GROUP:-kafka-console-consumer-group} \
--property "print.timestamp=true" \
--property "print.offset=true" \
--property "print.partition=true" \
--property "print.key=true" \
--property "print.value=true" \
--property "print.headers=true" \
--timeout-ms 5000

# read one message from specific offset and partition:
kafka-console-consumer.sh --bootstrap-server ${BOOTSTRAP_SERVER:-localhost:9092} --topic ${TOPIC_NAME:-console.tools.topic} \
--offset 1 \
--partition 0 \
--max-messages 1 \
--timeout-ms 2000
