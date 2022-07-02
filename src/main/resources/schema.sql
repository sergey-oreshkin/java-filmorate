DROP TABLE IF EXISTS film_genre, likes, friendship, film, genre, rating, directors, users, reviews;



CREATE TABLE IF NOT EXISTS users
(
    id       BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    email    VARCHAR(50),
    login    VARCHAR(50),
    name     VARCHAR(50),
    birthday DATE
);

CREATE TABLE IF NOT EXISTS genre
(
    id    INT PRIMARY KEY,
    genre VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS rating
(
    id     INT PRIMARY KEY,
    rating VARCHAR(10)
);

CREATE TABLE IF NOT EXISTS directors
(
    id BIGINT PRIMARY KEY,
    name VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS film
(
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name         VARCHAR(50),
    description  VARCHAR(200),
    release_date DATE,
    duration     INT,
    rating_id    INT,
    director_id  INT,
    CONSTRAINT fk_rating
        FOREIGN KEY (rating_id) REFERENCES rating (id) ON DELETE CASCADE,
    CONSTRAINT fk_directors
        FOREIGN KEY (director_id) REFERENCES directors (id)
);

CREATE TABLE IF NOT EXISTS friendship
(
    user_id   BIGINT,
    friend_id BIGINT,
    CONSTRAINT friendship_pk
        PRIMARY KEY (user_id, friend_id),
    CONSTRAINT users
        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT friend
        FOREIGN KEY (friend_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS likes
(
    film_id BIGINT,
    user_id BIGINT,
    CONSTRAINT likes_pk
        PRIMARY KEY (film_id, user_id),
    CONSTRAINT fk_film
        FOREIGN KEY (film_id) REFERENCES film (id) ON DELETE CASCADE,
    CONSTRAINT fk_users
        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS film_genre
(
    film_id  BIGINT,
    genre_id INT,
    CONSTRAINT film_genre_pk
        PRIMARY KEY (film_id, genre_id),
    CONSTRAINT film_genre_fk_film
        FOREIGN KEY (film_id) REFERENCES film (id) ON DELETE CASCADE,
    CONSTRAINT film_genre_fk_genre
        FOREIGN KEY (genre_id) REFERENCES genre (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS reviews
(
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    content      VARCHAR(3000),
    isPositive   BOOLEAN,
    userId       BIGINT,
    filmId       BIGINT,
    useful       INTEGER,
    CONSTRAINT reviews_fk_users
        FOREIGN KEY (userId) REFERENCES users (id),
    CONSTRAINT reviews_fk_films
        FOREIGN KEY (filmId) REFERENCES film (id)
);

