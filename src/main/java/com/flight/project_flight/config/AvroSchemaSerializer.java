package com.flight.project_flight.config;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.apache.avro.Schema;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.io.IOException;


public class AvroSchemaSerializer extends JsonSerializer<Schema> {
    public void serialize(Schema value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("type", value.getType().toString());
        gen.writeEndObject();
    }
}

