CREATE TABLE passenger (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    phone VARCHAR(50),
    passport_number VARCHAR(255) NOT NULL,
    dob DATETIME,
    password VARCHAR(255) NOT NULL,
    enabled BOOLEAN NOT NULL
);

CREATE TABLE roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE airport (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    location VARCHAR(255) NOT NULL,
    code VARCHAR(10) NOT NULL UNIQUE,
    capacity INT NOT NULL DEFAULT 0,
    city VARCHAR(255) NOT NULL DEFAULT 'Unknown',
    country VARCHAR(255) NOT NULL DEFAULT 'Unknown',
    international BOOLEAN NOT NULL DEFAULT FALSE,
    is_active BOOLEAN NOT NULL DEFAULT FALSE,
    terminal_info VARCHAR(255) NOT NULL DEFAULT 'No information available',
    timezone VARCHAR(100) NOT NULL DEFAULT 'Unknown'
);

CREATE TABLE airlines (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    code VARCHAR(5) UNIQUE
);

CREATE TABLE airline_airports (
    airline_id BIGINT NOT NULL,
    airport_id BIGINT NOT NULL,

    PRIMARY KEY (airline_id, airport_id),

    CONSTRAINT fk_airline_airports_airline
        FOREIGN KEY (airline_id) REFERENCES airlines(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_airline_airports_airport
        FOREIGN KEY (airport_id) REFERENCES airport(id)
        ON DELETE CASCADE
);

CREATE TABLE flight (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    flight_number VARCHAR(50) UNIQUE,
    departure_time DATETIME,
    arrival_time DATETIME,
    departure_airport_id BIGINT,
    arrival_airport_id BIGINT,
    status VARCHAR(30),

    CONSTRAINT fk_flight_departure_airport
        FOREIGN KEY (departure_airport_id) REFERENCES airport(id),

    CONSTRAINT fk_flight_arrival_airport
        FOREIGN KEY (arrival_airport_id) REFERENCES airport(id)
);

CREATE TABLE ticket (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ticket_number VARCHAR(255) UNIQUE,
    issue_date DATETIME NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    passenger_id BIGINT NOT NULL,
    flight_id BIGINT NOT NULL,

    CONSTRAINT fk_ticket_passenger
        FOREIGN KEY (passenger_id) REFERENCES passenger(id),

    CONSTRAINT fk_ticket_flight
        FOREIGN KEY (flight_id) REFERENCES flight(id)
);

CREATE TABLE reservation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    reservation_date DATETIME,
    seat_number VARCHAR(50),
    price DECIMAL(10,2),

    passenger_id BIGINT NOT NULL,
    flight_id BIGINT NOT NULL,

    CONSTRAINT fk_reservation_passenger
        FOREIGN KEY (passenger_id) REFERENCES passenger(id),

    CONSTRAINT fk_reservation_flight
        FOREIGN KEY (flight_id) REFERENCES flight(id)
);

CREATE TABLE alert (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    message VARCHAR(255) NOT NULL,
    alert_date DATETIME,
    severity VARCHAR(50),

    passenger_id BIGINT,
    flight_id BIGINT,
    ticket_id BIGINT,

    CONSTRAINT fk_alert_passenger
        FOREIGN KEY (passenger_id) REFERENCES passenger(id),

    CONSTRAINT fk_alert_flight
        FOREIGN KEY (flight_id) REFERENCES flight(id),

    CONSTRAINT fk_alert_ticket
        FOREIGN KEY (ticket_id) REFERENCES ticket(id)
);

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,

    PRIMARY KEY (user_id, role_id),

    CONSTRAINT fk_user_roles_user
        FOREIGN KEY (user_id) REFERENCES passenger(id),

    CONSTRAINT fk_user_roles_role
        FOREIGN KEY (role_id) REFERENCES roles(id)
);