CREATE TABLE IF NOT EXISTS rating_passengers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    passenger_id BIGINT NOT NULL,
    rating INT NOT NULL,
    comment VARCHAR(255),
    FOREIGN KEY (passenger_id) REFERENCES passengers(id)
) engine=InnoDB;