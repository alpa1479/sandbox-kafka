# send message to kafka topic with custom format:
# message example: header1:value1,header2:value2|demo-key|{"name": "demo-name", "value": "demo-value"}
kafka-console-producer.sh --bootstrap-server ${BOOTSTRAP_SERVER:-localhost:9092} --topic ${TOPIC_NAME:-console.tools.topic} \
--property "parse.key=true" \
--property "key.separator=|" \
--property "parse.headers=true" \
--property "headers.delimiter=|" \
--property "ignore.error=true" \
--producer-property acks=all

# scenario to create new topic and consume messages from existing topic to the new one:
kafka-topics.sh --bootstrap-server ${BOOTSTRAP_SERVER:-localhost:9092} --topic ${MIGRATION_TOPIC_NAME:-messages-migration-test.topic} --create \
--if-not-exists \
--partitions 1 \
--replication-factor 1 \
--config retention.ms=604800000
kafka-console-consumer.sh --bootstrap-server ${BOOTSTRAP_SERVER:-localhost:9092} --topic ${TOPIC_NAME:-console.tools.topic} \
--from-beginning \
--property "print.key=true" \
--property "key.separator=|" \
--property "print.value=true" \
--property "print.headers=true" \
--property "headers.separator=," | kafka-console-producer.sh --bootstrap-server ${BOOTSTRAP_SERVER:-localhost:9092} --topic ${MIGRATION_TOPIC_NAME:-messages-migration-test.topic} \
                                   --property "parse.key=true" \
                                   --property "key.separator=|" \
                                   --property "parse.headers=true" \
                                   --property "headers.delimiter=|" \
                                   --property "ignore.error=true" \
                                   --producer-property acks=all
