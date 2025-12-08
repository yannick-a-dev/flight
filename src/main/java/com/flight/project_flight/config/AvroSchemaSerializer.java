package com.flight.project_flight.config;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.apache.avro.Schema;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.io.IOException;


public class AvroSchemaSerializer extends JsonSerializer<Schema> {
    public void serialize(Schema value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        // Logique de sérialisation du schéma Avro en JSON
        gen.writeStartObject();
        gen.writeStringField("type", value.getType().toString());
        // Ajoutez ici d'autres champs nécessaires pour la sérialisation du schéma
        gen.writeEndObject();
    }
}

