package cits.consumer;

import air.ClientMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.annotation.Backoff;

@Configuration
public class KafkaConsumer {
    private final Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);

    final MessageProcessor messageProcessor;

    public KafkaConsumer(MessageProcessor messageProcessor) {
        this.messageProcessor = messageProcessor;
    }

    @RetryableTopic(
            numPartitions = "5",
            attempts = "5",
            backoff = @Backoff(delay = 2_000, maxDelay = 10_000, multiplier = 2),
            include = {RetryableMessageException.class},
            timeout = "10000",
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE)
    @KafkaListener(topics = "${spring.kafka.properties.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void topicListener(ClientMessage clientMessage) {
        System.out.println("Received: " + clientMessage.toString());
        try {
            messageProcessor.processMessage(clientMessage);
        } catch (RetryableMessageException e) {
            System.out.println("Will Retry: " + clientMessage.toString());
            throw e;
        } catch (Exception e) {
            System.out.println("Error : " + e.getMessage());
        }
    }

    @DltHandler
    public void dltListener(String in, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic, @Header(KafkaHeaders.OFFSET) long offset) {
        this.logger.info("DLT Received: {} from {} @ {}", in, topic, offset);
    }

//
//    @Bean
//    public KafkaTemplate<String, String> kafkaTemplate() {
//        return new KafkaTemplate<>(producerFactory());
//    }
//
//    @Bean
//    public ProducerFactory<String, String> producerFactory() {
//        return new DefaultKafkaProducerFactory<>(
//                Map.of(BOOTSTRAP_SERVERS_CONFIG, "localhost:9092",
//                        RETRIES_CONFIG, 0,
//                        BUFFER_MEMORY_CONFIG, 33554432,
//                        KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
//                        VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class
//                ));
//    }

}
