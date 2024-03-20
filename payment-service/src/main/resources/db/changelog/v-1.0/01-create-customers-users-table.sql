CREATE TABLE customers_users (
    id           BIGSERIAL PRIMARY KEY,
    customer_id  VARCHAR(255),
    passenger_id uuid
);