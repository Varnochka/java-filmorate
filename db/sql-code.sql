CREATE TABLE "film"(
    "film_id" BIGINT NOT NULL,
    "name" VARCHAR(50) NOT NULL,
    "description" VARCHAR(200) NOT NULL,
    "release_date" DATE NOT NULL,
    "duration" BIGINT NOT NULL,
    "rating_id" BIGINT NOT NULL
);
ALTER TABLE
    "film" ADD PRIMARY KEY("film_id");
CREATE TABLE "films_users"(
    "film_id" BIGINT NOT NULL,
    "user_id" BIGINT NOT NULL
);
CREATE TABLE "genres_films"(
    "film_id" BIGINT NOT NULL,
    "genre_id" BIGINT NOT NULL
);
CREATE TABLE "friendship"(
    "status_id" BIGINT NOT NULL,
    "user_id" BIGINT NOT NULL,
    "friend_id" BIGINT NOT NULL,
    "approved" BOOLEAN NOT NULL
);
ALTER TABLE
    "friendship" ADD PRIMARY KEY("status_id");
CREATE TABLE "mpa_rating"(
    "rating_id" BIGINT NOT NULL,
    "name" VARCHAR(10) NOT NULL
);
ALTER TABLE
    "mpa_rating" ADD PRIMARY KEY("rating_id");
CREATE TABLE "genre"(
    "genre_id" BIGINT NOT NULL,
    "name" VARCHAR(50) NOT NULL
);
ALTER TABLE
    "genre" ADD PRIMARY KEY("genre_id");
CREATE TABLE "user"(
    "user_id" BIGINT NOT NULL,
    "email" VARCHAR(30) NOT NULL,
    "login" VARCHAR(20) NOT NULL,
    "birthday" DATE NOT NULL
);
ALTER TABLE
    "user" ADD PRIMARY KEY("user_id");
ALTER TABLE
    "films_users" ADD CONSTRAINT "films_users_film_id_foreign" FOREIGN KEY("film_id") REFERENCES "film"("film_id");
ALTER TABLE
    "friendship" ADD CONSTRAINT "friendship_friend_id_foreign" FOREIGN KEY("friend_id") REFERENCES "user"("user_id");
ALTER TABLE
    "friendship" ADD CONSTRAINT "friendship_user_id_foreign" FOREIGN KEY("user_id") REFERENCES "user"("user_id");
ALTER TABLE
    "film" ADD CONSTRAINT "film_rating_id_foreign" FOREIGN KEY("rating_id") REFERENCES "mpa_rating"("rating_id");
ALTER TABLE
    "genres_films" ADD CONSTRAINT "genres_films_genre_id_foreign" FOREIGN KEY("genre_id") REFERENCES "genre"("genre_id");
ALTER TABLE
    "films_users" ADD CONSTRAINT "films_users_user_id_foreign" FOREIGN KEY("user_id") REFERENCES "user"("user_id");
ALTER TABLE
    "genres_films" ADD CONSTRAINT "genres_films_film_id_foreign" FOREIGN KEY("film_id") REFERENCES "film"("film_id");