# get amount of data in topic for each partition:
kafka-log-dirs.sh --bootstrap-server ${BOOTSTRAP_SERVER:-localhost:9092} --describe --topic-list ${TOPIC_NAME:-console.tools.topic}

# sum of data in topic partitions:
kafka-log-dirs.sh --bootstrap-server ${BOOTSTRAP_SERVER:-localhost:9092} --describe \
--topic-list ${TOPIC_NAME:-console.tools.topic} | grep -oP '(?<=size":)\d+' | awk '{ sum += $1 } END { print sum }'
