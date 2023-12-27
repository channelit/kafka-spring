package cits.consumer;

import air.ClientMessage;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.retrytopic.DeadLetterPublishingRecovererFactory;
import org.springframework.kafka.retrytopic.RetryTopicConfigurationSupport;
import org.springframework.kafka.support.serializer.DelegatingByTypeSerializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Map;
import java.util.function.Consumer;


//@EnableKafka
//@Configuration
//@EnableScheduling
//public class RetryConfig  extends RetryTopicConfigurationSupport {
//    @Override
//    protected Consumer<DeadLetterPublishingRecovererFactory> configureDeadLetterPublishingContainerFactory() {
//        return dlprf -> dlprf.setPartitionResolver((cr, nextTopic) -> null);
//    }
//
//    @Bean
//    public ProducerFactory<String, Object> producerFactory() {
//        return new DefaultKafkaProducerFactory<>(producerConfiguration(), new StringSerializer(),
//                new DelegatingByTypeSerializer(Map.of(byte[].class, new ByteArraySerializer(),
//                        ClientMessage.class, new JsonSerializer<Object>())));
//    }
//
//    @Bean
//    public KafkaTemplate<String, Object> kafkaTemplate() {
////        return new KafkaTemplate<>(producerFactory());
////    }
//
//}
