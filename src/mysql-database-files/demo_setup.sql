USE theater_db;

-- Demo setup data for development and testing.
-- Admin login: AdminTest | Admin123!

INSERT INTO auditorium (auditorium_id, name)
VALUES (1, 'Auditorium 1')
ON DUPLICATE KEY UPDATE name = VALUES(name);

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
