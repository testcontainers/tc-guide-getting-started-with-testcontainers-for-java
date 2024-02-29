CREATE TABLE IF NOT EXISTS ratings (
    ticketId INT,
    comment VARCHAR(255),
    value INT
);

INSERT
INTO ratings (ticketId, comment, value)
VALUES (1,'testcontainers-integration-testing', 5)
ON CONFLICT do nothing;

INSERT
INTO ratings (ticketId, comment, value)
VALUES (1,'flight-of-the-flux', 5)
ON CONFLICT do nothing;