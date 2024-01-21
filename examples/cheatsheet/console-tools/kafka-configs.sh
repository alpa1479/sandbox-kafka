# get broker configuration:
kafka-configs.sh --bootstrap-server ${BOOTSTRAP_SERVER:-localhost:9092} --describe --broker ${BROKER_ID:-1} --all

# get topic configuration:
kafka-configs.sh --bootstrap-server ${BOOTSTRAP_SERVER:-localhost:9092} --topic ${TOPIC_NAME:-console.tools.topic} --describe --all

# add or update configuration property for topic:
kafka-configs.sh --bootstrap-server ${BOOTSTRAP_SERVER:-localhost:9092} --topic ${TOPIC_NAME:-console.tools.topic} --alter \
--add-config segment.ms=1245

# remove or reset to default [604800000 in case segment.ms] configuration property for topic:
kafka-configs.sh --bootstrap-server ${BOOTSTRAP_SERVER:-localhost:9092} --topic ${TOPIC_NAME:-console.tools.topic} --alter \
--delete-config segment.ms
