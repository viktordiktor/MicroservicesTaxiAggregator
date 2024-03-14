CREATE TABLE IF NOT EXISTS rating_passengers (
    id BIGSERIAL PRIMARY KEY,
    passenger_id uuid,
    rating INT NOT NULL,
    comment VARCHAR(255),
    FOREIGN KEY (passenger_id) REFERENCES passengers(id)
)