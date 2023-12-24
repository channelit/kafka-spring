package cits.producer;

import air.ClientMessage;
import com.google.common.util.concurrent.ListenableFuture;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

@RestController
public class ProducerController {

    @Autowired
    private KafkaTemplate<String, ClientMessage> template;

    @Value("${spring.kafka.properties.topic}")
    private String topicName;

    @GetMapping(path = "/send")
    public void send(@RequestParam int num) {
        MsgGenerator.getMessages(num).forEach(message -> {
            String[] vals = message.getValue().split(",");
            ClientMessage clientMessage = ClientMessage.newBuilder()
                    .setId(Integer.parseInt(vals[1]))
                    .setKey(message.getKey())
                    .setClient(vals[0])
                    .setMessage(message.getValue())
                    .setGeneratedAt(Instant.now().toEpochMilli())
                    .build();
            CompletableFuture<SendResult<String, ClientMessage>> future = template.send(topicName, clientMessage.getKey().toString(), clientMessage);
            future.whenComplete((sr, ex) -> {
                if (sr != null) {
                    System.out.println("success");
                } else if (ex != null) {
                    System.out.println("error");
                }
            });

        });

    }

}
