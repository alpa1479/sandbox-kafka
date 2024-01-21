# get kafka version:
kafka-topics.sh --version

# get list of topics:
kafka-topics.sh --bootstrap-server ${BOOTSTRAP_SERVER:-localhost:9092} --list

# get list of topics that are not in-sync with all replicas:
kafka-topics.sh --bootstrap-server ${BOOTSTRAP_SERVER:-localhost:9092} --describe \
--under-replicated-partitions

# get list of topics without a leader replica:
kafka-topics.sh --bootstrap-server ${BOOTSTRAP_SERVER:-localhost:9092} --describe \
--unavailable-partitions

# get list of topics whose isr count is less that the configured minimum:
kafka-topics.sh --bootstrap-server ${BOOTSTRAP_SERVER:-localhost:9092} --describe \
--under-min-isr-partitions

# get topic info by name:
kafka-topics.sh --bootstrap-server ${BOOTSTRAP_SERVER:-localhost:9092} --topic ${TOPIC_NAME:-console.tools.topic} --describe \
--if-exists

# create topic:
kafka-topics.sh --bootstrap-server ${BOOTSTRAP_SERVER:-localhost:9092} --topic ${TOPIC_NAME:-console.tools.topic} --create \
--if-not-exists \
--partitions 1 \
--replication-factor 1 \
--config retention.ms=604800000

# increase number of partitions for topic:
kafka-topics.sh --bootstrap-server ${BOOTSTRAP_SERVER:-localhost:9092} --topic ${TOPIC_NAME:-console.tools.topic} --alter \
--if-exists \
--partitions 9

# delete topic by name:
kafka-topics.sh --bootstrap-server ${BOOTSTRAP_SERVER:-localhost:9092} --topic ${TOPIC_NAME:-console.tools.topic} --delete \
--if-exists

# delete messages from topic:
kafka-topics.sh --bootstrap-server ${BOOTSTRAP_SERVER:-localhost:9092} --topic ${TOPIC_NAME:-console.tools.topic} --alter \
--config retention.ms=1000
#... wait a minute ...
kafka-topics.sh --bootstrap-server ${BOOTSTRAP_SERVER:-localhost:9092} --topic ${TOPIC_NAME:-console.tools.topic} --alter \
--delete-config retention.ms
