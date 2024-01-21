# generate partitions reassignment plan:
kafka-reassign-partitions.sh --bootstrap-server ${BOOTSTRAP_SERVER:-localhost:9092} --generate \
--topics-to-move-json-file /opt/bitnami/topics-to-move.json \
--broker-list 1,2
# example output:
# Current partition replica assignment
# {"version":1,"partitions":[{"topic":"console.tools.topic","partition":0,"replicas":[2],"log_dirs":["any"]}]}
# Proposed partition reassignment configuration
# {"version":1,"partitions":[{"topic":"console.tools.topic","partition":0,"replicas":[1],"log_dirs":["any"]}]}

# execute the reassignment:
kafka-reassign-partitions.sh --bootstrap-server ${BOOTSTRAP_SERVER:-localhost:9092} --execute \
--reassignment-json-file /opt/bitnami/reassignment-plan.json

# verify the reassignment completed:
kafka-reassign-partitions.sh --bootstrap-server ${BOOTSTRAP_SERVER:-localhost:9092} --verify \
--reassignment-json-file /opt/bitnami/reassignment-plan.json
