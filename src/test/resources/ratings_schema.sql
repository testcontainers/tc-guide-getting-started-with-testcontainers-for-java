CREATE TABLE IF NOT EXISTS ratings (
    ticketId INT,
    comment VARCHAR(255),
    stars INT
);

INSERT
INTO ratings (ticketId, comment, stars)
VALUES (1,'testcontainers-integration-testing', 5)
ON CONFLICT do nothing;

INSERT
INTO ratings (ticketId, comment, stars)
VALUES (1,'flight-of-the-flux', 5)
ON CONFLICT do nothing;