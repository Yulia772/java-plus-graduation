-- Создание таблицы хитов
CREATE SCHEMA IF NOT EXISTS public;
CREATE TABLE IF NOT EXISTS endpoint_hits
(
    id        BIGSERIAL PRIMARY KEY,
    app       VARCHAR(50)                 NOT NULL,
    uri       VARCHAR(2000)               NOT NULL,
    ip        VARCHAR(45)                 NOT NULL,
    timestamp TIMESTAMP WITHOUT TIME ZONE NOT NULL
);
