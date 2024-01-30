CREATE TABLE IF NOT EXISTS rating_drivers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    driver_id BIGINT NOT NULL,
    rating INT NOT NULL,
    comment VARCHAR(255),
    FOREIGN KEY (driver_id) REFERENCES drivers(id)
) engine=InnoDB;