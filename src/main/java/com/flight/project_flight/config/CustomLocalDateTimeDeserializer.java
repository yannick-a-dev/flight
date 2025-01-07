package com.flight.project_flight.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class CustomLocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

    private static final DateTimeFormatter formatterWithMillis = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
    private static final DateTimeFormatter formatterWithSeconds = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private static final DateTimeFormatter formatterWithoutSeconds = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
    private static final DateTimeFormatter formatterWithoutTime = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String date = p.getText().trim();

        if (date.endsWith("Z")) {
            date = date.substring(0, date.length() - 1);
        }

        try {
            return LocalDateTime.parse(date, formatterWithMillis);
        } catch (DateTimeParseException e1) {
            try {
                return LocalDateTime.parse(date, formatterWithSeconds);
            } catch (DateTimeParseException e2) {
                try {
                    return LocalDateTime.parse(date, formatterWithoutSeconds);
                } catch (DateTimeParseException e3) {
                    return LocalDate.parse(date, formatterWithoutTime).atStartOfDay();
                }
            }
        }
    }
}

