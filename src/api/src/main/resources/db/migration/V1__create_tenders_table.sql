CREATE TABLE tenders (
    id UUID PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    buyer VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL
);

INSERT INTO tenders (id, title, buyer, status, created_at)
VALUES
    ('00000000-0000-0000-0000-000000000001', 'Hospital imaging system tender', 'Budapest Clinical Center', 'OPEN', CURRENT_TIMESTAMP),
    ('00000000-0000-0000-0000-000000000002', 'Cloud procurement platform extension', 'Central Procurement Office', 'OPEN', CURRENT_TIMESTAMP);
