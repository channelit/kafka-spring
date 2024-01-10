# kafka-spring

#### Install Java and Create Topics from EC2
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
#### Increase Num Partitions
```shell
export BOOTSTRAP_SERVERS=localhost:9092
bin/kafka-topics.sh --bootstrap-server $BOOTSTRAP_SERVERS --alter --topic air-fluff --partitions 9
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


### Run apps locally
```shell
java -jar producer/target/producer-0.0.1-SNAPSHOT.jar --server.port=8051
java -jar consumer/target/consumer-0.0.1-SNAPSHOT.jar --server.port=8052
java -jar consumer/target/consumer-0.0.1-SNAPSHOT.jar --server.port=8053
java -jar consumer/target/consumer-0.0.1-SNAPSHOT.jar --server.port=8054
java -jar stream/target/stream-0.0.1-SNAPSHOT.jar --server.port=8055
```


### ksqlDB get unique

```shell
./ksql http://localhost:8088 
SET 'auto.offset.reset' = 'earliest';
```

#### Easy aggregation
```shell
CREATE STREAM air_stream WITH (kafka_topic='air-fluff', value_format='AVRO');
CREATE TABLE air_latest WITH (value_format='AVRO') AS 
  SELECT 
    CLIENT,
    LATEST_BY_OFFSET(unique_id) as latest_id, 
    LATEST_BY_OFFSET(key) as key,
    LATEST_BY_OFFSET(id) as id,
    LATEST_BY_OFFSET(message) as message
  FROM air_stream 
  GROUP BY CLIENT EMIT CHANGES;
SELECT * FROM air_latest EMIT CHANGES;
```

#### Theory Only!
```shell
LIST TOPICS;
SET 'auto.offset.reset' = 'earliest';
CREATE STREAM air_stream WITH (kafka_topic='air-fluff', value_format='AVRO');
CREATE STREAM air_stream_by_unique_id with (value_format='AVRO') AS SELECT * FROM air_stream PARTITION BY unique_id;
#CREATE TABLE air_table_by_unique_id (unique_id varchar primary key) WITH (kafka_topic='AIR_STREAM_BY_UNIQUE_ID', value_format='AVRO');
CREATE TABLE air_table_by_unique_id (latest_id varchar PRIMARY KEY, client varchar) with (value_format='AVRO') AS SELECT LATEST_BY_OFFSET(unique_id) as latest_id, CLIENT FROM AIR_STREAM_BY_UNIQUE_ID GROUP BY CLIENT EMIT CHANGES;
CREATE TABLE air_latest WITH (value_format='AVRO') AS SELECT LATEST_BY_OFFSET(unique_id) as latest_id, CLIENT FROM air_table_by_unique_id GROUP BY CLIENT EMIT CHANGES;
CREATE STREAM air_stream_latest AS SELECT * FROM air_stream_by_unique_id INNER JOIN air_latest ON air_stream_by_unique_id.unique_id=air_latest.latest_id EMIT CHANGES;
```