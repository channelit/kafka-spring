# kafka-spring
```
sudo yum install java-21-amazon-corretto-devel
wget https://downloads.apache.org/kafka/3.6.1/kafka_2.13-3.6.1.tgz
tar -xzf kafka_2.13-3.6.1.tgz
cd kafka_2.13-3.6.1
bin/kafka-topics.sh --bootstrap-server $BOOTSTRAP_SERVERS --topic air --create --partitions 3 --replication-factor 1 --command-config composer.properties
bin/kafka-topics.sh --bootstrap-server $BOOTSTRAP_SERVERS --topic air.retry --create --command-config composer.properties
bin/kafka-topics.sh --bootstrap-server $BOOTSTRAP_SERVERS --topic air.dlq --create --command-config composer.properties
bin/kafka-topics.sh --bootstrap-server $BOOTSTRAP_SERVERS --list --command-config composer.properties
bin/kafka-acls.sh --bootstrap-server $BOOTSTRAP_SERVERS --add --allow-principal "User:scott" --operation ALL --group “*” --topic air-topic
```

### composer.properties
```shell
security.protocol=SASL_SSL
sasl.mechanism=SCRAM-SHA-512
sasl.jaas.config=org.apache.kafka.common.security.scram.ScramLoginModule required username="scott" password="tiger";
```

### Local Kafka
#### Download
```shell
curl https://downloads.apache.org/kafka/3.6.1/kafka_2.13-3.6.1.tgz -o kafka.tgz
tar -xzf kafka.tgz
ln -s kafka_2.13-3.6.1 kafka
```

#### Start Kafka with Raft
```shell
cd kafka
KAFKA_CLUSTER_ID="$(bin/kafka-storage.sh random-uuid)"
bin/kafka-storage.sh format -t $KAFKA_CLUSTER_ID -c config/kraft/server.properties
bin/kafka-server-start.sh config/kraft/server.properties
```

### Schema Registry from Confluent Community
```shell
curl -O https://packages.confluent.io/archive/7.5/confluent-community-7.5.2.tar.gz
tar xzf confluent-7.5.2.tar.gz
cd confluent-7.5.2
./bin/schema-registry-start schema-registry.properties
```

#### schema-registry.properties
```shell
listeners=http://0.0.0.0:8081
host.name=localhost
kafkastore.bootstrap.servers=localhost:9092
# kafkastore.connection.url=zoo1:2181,zoo2:2181,zoo3:2181
kafkastore.topic=_schemas
kafkastore.topic.replication.factor=3
debug=false
```

#### start schema-registry locally
```shell
./bin/schema-registry-start schema-registry.properties
```

### Kafdrop UI http://localhost:9000/
```shell
curl https://github.com/obsidiandynamics/kafdrop/releases/download/4.0.1/kafdrop-4.0.1.jar -o kafdrop.jar
java --add-opens=java.base/sun.nio.ch=ALL-UNNAMED \
    -jar kafdrop.jar \
    --kafka.brokerConnect=localhost:9092 \
    --schemaregistry.connect=http://localhost:8081 \
    --message.format=AVRO \
    --message.keyFormat=DEFAULT
```