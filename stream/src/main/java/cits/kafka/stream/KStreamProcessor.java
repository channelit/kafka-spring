package cits.kafka.stream;

import air.ClientMessage;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Predicate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KStreamProcessor {

    @Value("${spring.kafka.properties.topic-one}")
    private String outTopic;

    public void process(KStream<String, ClientMessage> stream) {
        stream.filter((key, message) -> {
            System.out.println(message.getClient());
            return message != null && message.getClient() != null;
        }).to(outTopic);
    }
}