INSERT INTO rating_passengers (id, passenger_id, rating, comment) VALUES
    (1, '11111111-1111-1111-1111-111111111111', 1, null),
    (2, '11111111-1111-1111-1111-111111111111', 5, 'Cool!'),
    (3, '22222222-2222-2222-2222-222222222222', 3, 'Not bad');

ALTER SEQUENCE rating_passengers_id_seq RESTART WITH 4;