package com.checkout.shared.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Currency;

@Component
public class JsonMapper {

    private final ObjectMapper objectMapper;

    public JsonMapper() {
        SimpleModule currencyModule = new SimpleModule("CurrencyModule");
        currencyModule.addSerializer(Currency.class, new JsonSerializer<>() {
            @Override
            public void serialize(Currency value, JsonGenerator gen, SerializerProvider p) throws IOException {
                gen.writeString(value.getCurrencyCode());
            }
        });
        currencyModule.addDeserializer(Currency.class, new JsonDeserializer<>() {
            @Override
            public Currency deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                return Currency.getInstance(p.getValueAsString());
            }
        });

        this.objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .registerModule(currencyModule)
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public String serialize(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            throw new SerializationException("Failed to serialize " + object.getClass().getName(), e);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T deserialize(String json, String className) {
        try {
            Class<?> clazz = Class.forName(className);
            return (T) objectMapper.readValue(json, clazz);
        } catch (ClassNotFoundException e) {
            throw new SerializationException("Unknown event type: " + className, e);
        } catch (Exception e) {
            throw new SerializationException("Failed to deserialize " + className, e);
        }
    }

    public <T> T deserialize(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new SerializationException("Failed to deserialize " + clazz.getName(), e);
        }
    }

    public static class SerializationException extends RuntimeException {
        public SerializationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
