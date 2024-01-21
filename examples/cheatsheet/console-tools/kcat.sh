# getting the last five messages of a topic:
kafkacat -C -b ${BOOTSTRAP_SERVER:-localhost:9092} -t ${TOPIC_NAME:-console.tools.topic} -p 0 -o -5 -e

# scenario to requeue messages from one topic to another:
kafkacat -C -b ${BOOTSTRAP_SERVER:-localhost:9092} -o beginning -e -t ${TOPIC_NAME:-console.tools.topic} -K , | \
kafkacat -P -b ${BOOTSTRAP_SERVER:-localhost:9092} -t ${MIGRATION_TOPIC_NAME:-messages-migration-test.topic} -K ,
