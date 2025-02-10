//package com.flight.project_flight.config;
//
//import org.springframework.kafka.support.serializer.ErrorHandlingSerializer;
//import org.springframework.kafka.core.JsonSerializer;
//import org.apache.kafka.common.serialization.Serializer;
//import org.springframework.kafka.support.serializer.ErrorHandlingSerializer;
//
//public class JsonSerializer<T> extends ErrorHandlingSerializer<T> {
//
//    private final ObjectMapper objectMapper = new ObjectMapper();
//
//    public JsonSerializer() {
//        super(new org.springframework.kafka.support.serializer.ErrorHandlingSerializer<>(objectMapper::writeValueAsBytes));
//    }
//
//    @Override
//    public byte[] serialize(String topic, T data) {
//        try {
//            return objectMapper.writeValueAsBytes(data);
//        } catch (Exception e) {
//            throw new SerializationException("Error serializing object", e);
//        }
//    }
//}

