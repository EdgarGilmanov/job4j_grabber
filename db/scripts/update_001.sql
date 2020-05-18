CREATE TABLE post
(id SERIAL PRIMARY KEY,
name VARCHAR(256) NOT NULL,
description TEXT NOT NULL,
link TEXT NOT NULL,
created VARCHAR(256) NOT NULL);

INSERT INTO post
(id, name, description, link, created)
VALUES (33, 'first', 'first', 'first', 'first');
INSERT INTO post
(id, name, description, link, created)
VALUES (34, 'second', 'second', 'second', 'second');