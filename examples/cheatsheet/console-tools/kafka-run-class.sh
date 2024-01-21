# get the earliest offset still in a topic:
kafka-run-class.sh kafka.tools.GetOffsetShell --bootstrap-server ${BOOTSTRAP_SERVER:-localhost:9092} --topic ${TOPIC_NAME:-console.tools.topic} --time -2

# get the latest offset still in a topic:
kafka-run-class.sh kafka.tools.GetOffsetShell --bootstrap-server ${BOOTSTRAP_SERVER:-localhost:9092} --topic ${TOPIC_NAME:-console.tools.topic} --time -1

# get partition log dump [need to execute from kafka broker container]:
kafka-run-class.sh kafka.tools.DumpLogSegments --deep-iteration --files /bitnami/kafka/data/console.tools.topic-0/00000000000000000000.log
