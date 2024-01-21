# read messages:
kafka-consumer-perf-test.sh --bootstrap-server ${BOOTSTRAP_SERVER:-localhost:9092} --topic ${TOPIC_NAME:-console.tools.topic} \
--messages 50000000
