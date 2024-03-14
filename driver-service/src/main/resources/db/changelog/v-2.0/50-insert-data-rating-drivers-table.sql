INSERT INTO rating_drivers (id, driver_id, rating, comment)
VALUES (1, '11111111-1111-1111-1111-111111111111', 1, null),
       (2, '11111111-1111-1111-1111-111111111111', 5, 'OK!'),
       (3, '22222222-2222-2222-2222-222222222222', 3, 'Super');

ALTER SEQUENCE rating_drivers_id_seq RESTART WITH 4;