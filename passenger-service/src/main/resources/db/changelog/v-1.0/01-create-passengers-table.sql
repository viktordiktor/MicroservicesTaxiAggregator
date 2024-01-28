CREATE TABLE passengers (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            username VARCHAR(255) NOT NULL UNIQUE,
                            phone VARCHAR(20) NOT NULL UNIQUE
) engine=InnoDB;