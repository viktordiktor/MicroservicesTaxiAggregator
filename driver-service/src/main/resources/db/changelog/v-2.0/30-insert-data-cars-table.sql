INSERT INTO cars (id, number, model, color)
VALUES (1, '1234AA1', 'Toyota Corolla', 'Red'),
       (2, '9876BB2', 'Citroen Xsantia', 'Blue'),
       (3, '3948BB5', 'Ford Mondeo', 'Silver');

ALTER SEQUENCE cars_id_seq RESTART WITH 4;