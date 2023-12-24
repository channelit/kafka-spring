package cits.consumer;

import air.ClientMessage;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;

@Configuration
public class KafkaConsumer {

    @KafkaListener(topics = "${spring.kafka.properties.topic}")
    void topicListener(ClientMessage clientMessage) {
        System.out.println("Received: " + clientMessage.toString());
    }

}
