USE theater_db;

-- Demo setup data for development and testing.
-- Admin login: AdminTest | Admin123!

INSERT INTO auditorium (auditorium_id, name)
VALUES (1, 'Auditorium 1')
ON DUPLICATE KEY UPDATE name = VALUES(name);

INSERT IGNORE INTO seat (auditorium_id, row_label, seat_number)
WITH RECURSIVE seat_numbers AS (
    SELECT 1 AS seat_number
    UNION ALL
    SELECT seat_number + 1
    FROM seat_numbers
    WHERE seat_number < 9
),
seat_rows AS (
    SELECT 'A' AS row_label
    UNION ALL SELECT 'B'
    UNION ALL SELECT 'C'
)
SELECT 1, r.row_label, n.seat_number
FROM seat_rows r
CROSS JOIN seat_numbers n;

INSERT INTO user (username, email, password_hash, role)
VALUES (
    'AdminTest',
    'admintest@example.com',
    '$2a$10$n2ZsYOpFUJu0ScpPi4RJqu5YAlDjJFElb8iRQiBTWthkJs/JL8Lpi',
    'ADMIN'
)
ON DUPLICATE KEY UPDATE
    email = VALUES(email),
    password_hash = VALUES(password_hash),
    role = VALUES(role);
