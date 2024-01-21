# delete records until specified offset according to the configuration [need copy configuration to kafka-cli container]:
# if offset specified as 2, then it will remove messages with offset 0,1
kafka-delete-records.sh --bootstrap-server ${BOOTSTRAP_SERVER:-localhost:9092} \
--offset-json-file /opt/bitnami/kafka-delete-records.json
