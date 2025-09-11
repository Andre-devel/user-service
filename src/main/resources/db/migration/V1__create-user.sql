CREATE TABLE users (
    id UUID PRIMARY KEY,
    email varchar(60) NOT NULL,
    name varchar(20) NOT NULL,
    password varchar(255) NOT NULL,
    CONSTRAINT unique_email UNIQUE (email),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
)