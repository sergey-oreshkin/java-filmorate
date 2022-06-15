MERGE INTO RATING KEY (ID, RATING) VALUES (1, 'G');
MERGE INTO RATING KEY (ID, RATING) VALUES (2, 'PG');
MERGE INTO RATING KEY (ID, RATING) VALUES (3, 'PG-13');
MERGE INTO RATING KEY (ID, RATING) VALUES (4, 'R');
MERGE INTO RATING KEY (ID, RATING) VALUES (5, 'NC-17');

MERGE INTO GENRE KEY (ID, GENRE) VALUES (1, 'Комедия');
MERGE INTO GENRE KEY (ID, GENRE) VALUES (2, 'Драма');
MERGE INTO GENRE KEY (ID, GENRE) VALUES (3, 'Мультфильм');
MERGE INTO GENRE KEY (ID, GENRE) VALUES (4, 'Фантастика');
MERGE INTO GENRE KEY (ID, GENRE) VALUES (5, 'Ужасы');
MERGE INTO GENRE KEY (ID, GENRE) VALUES (6, 'Мелодрама');

INSERT INTO FILM(name, description, rating_id, release_date, duration)
    VALUES ( 'test_name', 'test_description', 1, '2005-05-05', 100 );

INSERT INTO USERS(email, login, name, birthday)
    VALUES ( 'test@mail.ru', 'test_login', 'test_name', '1980-05-05' );

INSERT INTO USERS(email, login, name, birthday)
    VALUES ( 'test_1@mail.ru', 'test_1_login', 'test_1_name', '1980-05-05' );

INSERT INTO USERS(email, login, name, birthday)
    VALUES ( 'test_1@mail.ru', 'test_1_login', 'test_1_name', '1980-05-05' );

INSERT INTO FILM_GENRE(film_id, genre_id) VALUES ( 1, 1 );

INSERT INTO LIKES(FILM_ID, USER_ID) VALUES ( 1 , 1 );

INSERT INTO LIKES(FILM_ID, USER_ID) VALUES ( 1 , 2 );
