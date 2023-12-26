package cits.consumer;

import air.ClientMessage;
import ch.qos.logback.core.util.FixedDelay;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.annotation.Backoff;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class KafkaConsumer {
    private final Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);

    @RetryableTopic(
            attempts = "5",
            backoff = @Backoff(delay = 2_000, maxDelay = 10_000, multiplier = 2),
            include = {RetryableMessageException.class},
            timeout = "10000",
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE)
    @KafkaListener(topics = "${spring.kafka.properties.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void topicListener(ClientMessage clientMessage) {

        System.out.println("Received: " + clientMessage.toString());
    }

    @DltHandler
    public void dltListener(String in, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic, @Header(KafkaHeaders.OFFSET) long offset) {
        this.logger.info("DLT Received: {} from {} @ {}", in, topic, offset);
    }

}
