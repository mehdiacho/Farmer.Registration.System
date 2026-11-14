-- FarmersBW schema, version 1. Applied once when user_version is 0.
CREATE TABLE users (
    id            INTEGER PRIMARY KEY,
    email         TEXT    NOT NULL UNIQUE COLLATE NOCASE,
    display_name  TEXT    NOT NULL,
    password_hash TEXT    NOT NULL,
    is_admin      INTEGER NOT NULL DEFAULT 0 CHECK (is_admin IN (0, 1)),
    created_at    TEXT    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE farmers (
    id_number      TEXT    PRIMARY KEY CHECK (length(id_number) = 9),
    user_id        INTEGER UNIQUE REFERENCES users (id) ON DELETE CASCADE,
    first_name     TEXT    NOT NULL,
    last_name      TEXT    NOT NULL,
    contact        TEXT    NOT NULL UNIQUE,
    date_of_birth  TEXT    NOT NULL,
    gender         TEXT    NOT NULL CHECK (gender IN ('MALE', 'FEMALE')),
    postal_address TEXT    NOT NULL,
    district       TEXT    NOT NULL,
    sub_district   TEXT    NOT NULL,
    email          TEXT    NOT NULL UNIQUE COLLATE NOCASE,
    farm_size      REAL    NOT NULL CHECK (farm_size > 0),
    farming_area   REAL    NOT NULL CHECK (farming_area >= 0 AND farming_area <= farm_size),
    status         TEXT    NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'APPROVED', 'DECLINED')),
    registered_at  TEXT    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TEXT    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX farmers_district ON farmers (district, sub_district);
CREATE INDEX farmers_status ON farmers (status);
