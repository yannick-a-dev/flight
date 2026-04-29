-- TICKET
CREATE INDEX idx_ticket_passenger ON ticket(passenger_id);
CREATE INDEX idx_ticket_flight ON ticket(flight_id);

-- RESERVATION
CREATE INDEX idx_reservation_passenger ON reservation(passenger_id);
CREATE INDEX idx_reservation_flight ON reservation(flight_id);

-- ALERT
CREATE INDEX idx_alert_passenger ON alert(passenger_id);
CREATE INDEX idx_alert_flight ON alert(flight_id);

-- FLIGHT
CREATE INDEX idx_flight_departure_airport ON flight(departure_airport_id);
CREATE INDEX idx_flight_arrival_airport ON flight(arrival_airport_id);
