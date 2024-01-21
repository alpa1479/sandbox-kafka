# send messages:
kafka-producer-perf-test.sh \
  --topic ${TOPIC_NAME:-console.tools.topic} \
  --num-records 5000000 \
  --record-size 100 \
  --throughput -1 \
  --print-metrics \
  --producer-props acks=1 \
  bootstrap.servers=${BOOTSTRAP_SERVER:-localhost:9092} \
  buffer.memory=67108864 \
  batch.size=8196

# send messages with different size:
for i in 10 100 1000 10000 100000; do
  echo ""
  echo $i
  bin/kafka-producer-perf-test.sh \
    --topic test \
    --num-records $((1000*1024*1024/$i)) \
    --record-size $i \
    --throughput -1 \
    --producer-props acks=1 \
    bootstrap.servers=kafka.example.com:9092 \
    buffer.memory=67108864 \
    batch.size=128000
done;
