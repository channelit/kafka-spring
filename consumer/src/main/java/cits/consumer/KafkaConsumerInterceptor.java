package cits.consumer;

import air.ClientMessage;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.RecordInterceptor;

import java.util.Map;

@Configuration
public class KafkaConsumerInterceptor implements ConsumerInterceptor<String, ClientMessage> {

    @Override
    public ConsumerRecords<String, ClientMessage> onConsume(ConsumerRecords<String, ClientMessage> consumerRecords) {
        return consumerRecords;
    }

    @Override
    public void onCommit(Map<TopicPartition, OffsetAndMetadata> map) {

    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Map<String, ?> map) {

    }
}
