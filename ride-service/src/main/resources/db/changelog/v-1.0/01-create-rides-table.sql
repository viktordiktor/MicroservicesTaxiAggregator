CREATE TABLE rides
(
    id            BIGINT NOT NULL AUTO_INCREMENT,
    driver_id     BIGINT,
    passenger_id  BIGINT,
    start_address VARCHAR(255),
    end_address   VARCHAR(255),
    start_date    DATETIME,
    end_date      DATETIME,
    price         DOUBLE,
    distance      DOUBLE,
    is_active     BOOLEAN,
    PRIMARY KEY (id)
) engine = InnoDB;