INSERT INTO cars (id, number, model, color)
VALUES (1, '1234AA1', 'Toyota Corolla', 'Red'),
       (2, '9876BB2', 'Citroen Xsantia', 'Blue'),
       (3, '3948BB5', 'Ford Mondeo', 'Silver');

INSERT INTO drivers (id, username, phone, car_id, available)
VALUES ('11111111-1111-1111-1111-111111111111', 'VODILA_S_CHIZOVKI', '+375292547788', 2, true),
       ('22222222-2222-2222-2222-222222222222', 'TAXIST2001', '+375222222222', 3, true),
       ('33333333-3333-3333-3333-333333333333', 'FANAT_UBER', '+375123456789', 1, true),
       ('44444444-4444-4444-4444-444444444444', 'DIKTOR2003', '+375123456781', null, true);

INSERT INTO rating_drivers (id, driver_id, rating, comment)
VALUES (1, '11111111-1111-1111-1111-111111111111', 1, null),
       (2, '11111111-1111-1111-1111-111111111111', 5, 'OK!'),
       (3, '22222222-2222-2222-2222-222222222222', 3, 'Super');
