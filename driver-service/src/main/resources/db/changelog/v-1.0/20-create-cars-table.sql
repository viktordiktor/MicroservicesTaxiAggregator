CREATE TABLE cars (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    driver_id BIGINT,
    number VARCHAR(255) UNIQUE NOT NULL,
    model VARCHAR(255),
    color VARCHAR(255),
    FOREIGN KEY (driver_id) REFERENCES drivers(id)
) engine=InnoDB;