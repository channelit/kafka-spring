package cits.kafka.stream;

import air.ClientMessage;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Predicate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KStreamProcessor {

    @Value("${spring.kafka.properties.topic-one}")
    private String texassalestopic;

    public void process(KStream<String, ClientMessage> stream) {
        stream.filter(new Predicate<>() {
            @Override
            public boolean test(String key, ClientMessage object) {
                return object != null && object.getClient() != null;
            }
        }).to(texassalestopic);

    }
}