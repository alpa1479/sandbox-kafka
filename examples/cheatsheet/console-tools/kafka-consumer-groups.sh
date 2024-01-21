# get information about each consumer group:
kafka-consumer-groups.sh --bootstrap-server ${BOOTSTRAP_SERVER:-localhost:9092} --describe --all-groups

# get information about consumer group by name:
kafka-consumer-groups.sh --bootstrap-server ${BOOTSTRAP_SERVER:-localhost:9092} --describe --group ${CONSUMER_GROUP:-kafka-console-consumer-group}

# dry-run of resetting consumer group offset to earliest for specific topic:
kafka-consumer-groups.sh --bootstrap-server ${BOOTSTRAP_SERVER:-localhost:9092} --topic ${TOPIC_NAME:-console.tools.topic} --dry-run \
--group ${CONSUMER_GROUP:-kafka-console-consumer-group} \
--reset-offsets \
--to-earliest

# execution of resetting consumer group offset to earliest for specific topic:
kafka-consumer-groups.sh --bootstrap-server ${BOOTSTRAP_SERVER:-localhost:9092} --topic ${TOPIC_NAME:-console.tools.topic} --execute \
--group ${CONSUMER_GROUP:-kafka-console-consumer-group} \
--reset-offsets \
--to-earliest

# execution of resetting consumer group offset to specific offset in specific partition for specific topic:
kafka-consumer-groups.sh --bootstrap-server ${BOOTSTRAP_SERVER:-localhost:9092} --topic ${TOPIC_NAME:-console.tools.topic}:0 --execute \
--group ${CONSUMER_GROUP:-kafka-console-consumer-group} \
--reset-offsets \
--to-offset 1
