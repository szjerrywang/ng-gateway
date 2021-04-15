package top.didasoft.ibmmq.cli.commands;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

import java.io.IOException;

public class JsonSerializer implements Serializer<Object> {

    private ObjectMapper objectMapper;
    private ObjectWriter writer;
    public JsonSerializer() {
        objectMapper = JacksonUtils.enhancedObjectMapper();
        writer = objectMapper.writerFor((JavaType) null);
    }

    @Override
    public byte[] serialize(String topic, Object data) {
        if (data == null) {
            return null;
        }
        try {
            return this.writer.writeValueAsBytes(data);
        }
        catch (IOException ex) {
            throw new SerializationException("Can't serialize data [" + data + "] for topic [" + topic + "]", ex);
        }
    }
}
