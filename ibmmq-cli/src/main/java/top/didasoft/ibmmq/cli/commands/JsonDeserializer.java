package top.didasoft.ibmmq.cli.commands;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Deserializer;


import java.io.IOException;
import java.util.Arrays;

public class JsonDeserializer implements Deserializer<Object> {

    protected final ObjectMapper objectMapper; // NOSONAR

    protected JavaType targetType; // NOSONAR

    protected Jackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper(); // NOSONAR

    private ObjectReader reader;

    public JsonDeserializer() {
        objectMapper = JacksonUtils.enhancedObjectMapper();
        this.reader = this.objectMapper.readerFor((JavaType) null);
        this.typeMapper.setTypePrecedence(Jackson2JavaTypeMapper.TypePrecedence.TYPE_ID);
        this.typeMapper.addTrustedPackages("top.didasoft.ibmmq.cli.model");

    }


    @Override
    public Object deserialize(String topic, byte[] data) {
        if (data == null) {
            return null;
        }
        ObjectReader localReader = this.reader;
        try {
            return localReader.readValue(data);
        }
        catch (IOException e) {
            throw new SerializationException("Can't deserialize data [" + Arrays.toString(data) +
                    "] from topic [" + topic + "]", e);
        }

    }

    @Override
    public Object deserialize(String topic, Headers headers, byte[] data) {
        if (data == null) {
            return null;
        }
        ObjectReader deserReader = null;
        JavaType javaType = this.typeMapper.toJavaType(headers);

        if (javaType != null) {
            deserReader = this.objectMapper.readerFor(javaType);
        }
        if (deserReader == null) {
            deserReader = this.reader;
        }
        try {
            return deserReader.readValue(data);
        }
        catch (IOException e) {
            throw new SerializationException("Can't deserialize data [" + Arrays.toString(data) +
                    "] from topic [" + topic + "]", e);
        }
    }
}
