-- =========================
-- ADD AIRLINE RELATION TO FLIGHT
-- =========================

ALTER TABLE flight
ADD COLUMN airline_id BIGINT;

ALTER TABLE flight
ADD CONSTRAINT fk_flight_airline
FOREIGN KEY (airline_id) REFERENCES airlines(id);