INSERT INTO cars (id, number, model, color)
VALUES (1, '1234AA1', 'Toyota Corolla', 'Red'),
       (2, '9876BB2', 'Citroen Xsantia', 'Blue'),
       (3, '3948BB5', 'Ford Mondeo', 'Silver');

INSERT INTO drivers (id, username, phone, car_id, available)
VALUES (1, 'VODILA_S_CHIZOVKI', '+375292547788', 2, true),
       (2, 'TAXIST2001', '+375222222222', 3, true),
       (3, 'FANAT_UBER', '+375123456789', 1, true);

INSERT INTO rating_drivers (id, driver_id, rating, comment)
VALUES (1, 1, 1, null),
       (2, 1, 5, 'OK!'),
       (3, 2, 3, 'Super');