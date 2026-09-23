CREATE DATABASE IF NOT EXISTS theater_db;
USE theater_db;

-- Movie Ticket Booking System
-- Daniel Yozman, Jada Thompson, Samuel Vaughn, and William Byers
-- INFO-C451
-- 27 September 2026

-- User Table --
CREATE TABLE user (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('CUSTOMER', 'ADMIN') NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Movie Table --
CREATE TABLE movie (
    movie_id  INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    duration_minutes INT NOT NULL,
    rating VARCHAR(10),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Auditorium Table --
CREATE TABLE auditorium (
    auditorium_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);

-- Seat Table --
CREATE TABLE seat (
    seat_id INT AUTO_INCREMENT PRIMARY KEY,
    auditorium_id INT NOT NULL,
    row_label VARCHAR(5) NOT NULL,
    seat_number INT NOT NULL,
    UNIQUE (auditorium_id, row_label, seat_number),
    FOREIGN KEY (auditorium_id)
        REFERENCES auditorium(auditorium_id) ON DELETE CASCADE
);

-- Showtime Table --
CREATE TABLE showtime (
    showtime_id INT AUTO_INCREMENT PRIMARY KEY,
    movie_id  INT NOT NULL,
    auditorium_id INT NOT NULL,
    start_time DATETIME NOT NULL,
    status ENUM('ACTIVE', 'CANCELED') NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (movie_id)
        REFERENCES movie(movie_id) ON DELETE CASCADE,
    FOREIGN KEY (auditorium_id)
        REFERENCES auditorium(auditorium_id) ON DELETE CASCADE
);

-- ShowtimeSeat Table --
CREATE TABLE showtime_seat (
    showtime_seat_id INT AUTO_INCREMENT PRIMARY KEY,
    showtime_id INT NOT NULL,
    seat_id INT NOT NULL,
    status ENUM('AVAILABLE', 'RESERVED') NOT NULL DEFAULT 'AVAILABLE',
    UNIQUE (showtime_id, seat_id),
    FOREIGN KEY (showtime_id)
        REFERENCES showtime(showtime_id) ON DELETE CASCADE,
    FOREIGN KEY (seat_id)
        REFERENCES seat(seat_id) ON DELETE CASCADE
);

-- Reservation Table --
CREATE TABLE reservation (
    reservation_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    showtime_id INT NOT NULL,
    reservation_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status  ENUM('ACTIVE', 'CANCELED') NOT NULL DEFAULT 'ACTIVE',
    FOREIGN KEY (user_id)
        REFERENCES user(user_id),
    FOREIGN KEY (showtime_id)
        REFERENCES showtime(showtime_id)
);

-- ReservationSeat Table--
CREATE TABLE reservation_seat (
    reservation_seat_id INT AUTO_INCREMENT PRIMARY KEY,
    reservation_id INT NOT NULL,
    showtime_seat_id INT NOT NULL,
    UNIQUE (showtime_seat_id),
    FOREIGN KEY (reservation_id)
        REFERENCES reservation(reservation_id) ON DELETE CASCADE,
    FOREIGN KEY (showtime_seat_id)
        REFERENCES showtime_seat(showtime_seat_id) ON DELETE CASCADE
);

