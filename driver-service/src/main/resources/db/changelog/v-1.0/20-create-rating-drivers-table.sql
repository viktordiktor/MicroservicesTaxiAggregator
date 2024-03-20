CREATE TABLE IF NOT EXISTS rating_drivers
(
    id        BIGSERIAL PRIMARY KEY,
    driver_id uuid,
    rating    INT NOT NULL,
    comment   VARCHAR(255),
    FOREIGN KEY (driver_id) REFERENCES drivers (id)
);