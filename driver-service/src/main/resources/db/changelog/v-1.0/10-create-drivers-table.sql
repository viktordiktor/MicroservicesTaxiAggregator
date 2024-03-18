CREATE TABLE drivers
(
    id        uuid    default gen_random_uuid() PRIMARY KEY,
    username  VARCHAR(255) NOT NULL UNIQUE,
    phone     VARCHAR(20)  NOT NULL UNIQUE,
    car_id    BIGINT UNIQUE,
    available BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (car_id) REFERENCES cars (id)
);