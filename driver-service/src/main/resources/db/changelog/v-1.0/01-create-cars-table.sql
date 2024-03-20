CREATE TABLE cars
(
    id     BIGSERIAL PRIMARY KEY,
    number VARCHAR(255) UNIQUE NOT NULL,
    model  VARCHAR(255),
    color  VARCHAR(255)
);