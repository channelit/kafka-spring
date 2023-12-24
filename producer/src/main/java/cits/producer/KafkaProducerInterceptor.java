package cits.producer;

import air.ClientMessage;
import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Map;

public class KafkaProducerInterceptor implements ProducerInterceptor<String, ClientMessage> {


    @Override
    public ProducerRecord<String, ClientMessage> onSend(ProducerRecord<String, ClientMessage> producerRecord) {
        producerRecord.value().setClient(producerRecord.value().getClient() + "client");
        return producerRecord;
    }

    @Override
    public void onAcknowledgement(RecordMetadata recordMetadata, Exception e) {

    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Map<String, ?> map) {

    }
}
