-- Сначала удаляем старые таблицы (в правильном порядке из-за связей)
DROP TABLE IF EXISTS servicemen;
DROP TABLE IF EXISTS addresses;
DROP TABLE IF EXISTS ranks;

CREATE TABLE IF NOT EXISTS ranks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS addresses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    post_index VARCHAR(255),
    country VARCHAR(255),
    region VARCHAR(255),
    district VARCHAR(255),
    city VARCHAR(255),
    street VARCHAR(255),
    house VARCHAR(255),
    apartment VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS servicemen (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    surname VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    patronymic VARCHAR(255),
    nationality VARCHAR(255),
    birth_date DATE NOT NULL,
    position VARCHAR(255) NOT NULL,
    rank_id BIGINT NOT NULL,
    address_id BIGINT NOT NULL,
    FOREIGN KEY (rank_id) REFERENCES ranks(id),
    FOREIGN KEY (address_id) REFERENCES addresses(id)
);