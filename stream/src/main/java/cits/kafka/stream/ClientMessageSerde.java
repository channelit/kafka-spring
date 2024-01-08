package cits.kafka.stream;

import air.ClientMessage;
import ch.qos.logback.core.net.server.Client;
import com.amazonaws.services.schemaregistry.serializers.avro.AvroSerializer;
import com.fasterxml.jackson.databind.ser.std.StringSerializer;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;

public class ClientMessageSerde extends Serdes.WrapperSerde<ClientMessage> {
    public ClientMessageSerde(Serializer<ClientMessage> serializer, Deserializer<ClientMessage> deserializer) {
        super(serializer, deserializer);
    }
}
