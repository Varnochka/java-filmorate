package ru.yandex.practicum.filmorate.storage.imp.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class FilmUsersRepository {

    private final JdbcTemplate jdbcTemplate;

    public void save(Integer filmId, Integer userId) {
        jdbcTemplate.update("INSERT INTO FILMS_USERS VALUES (?,?)", filmId, userId);
    }

    public void delete(Integer filmId, Integer userId) {
        jdbcTemplate.update("DELETE FROM FILMS_USERS WHERE FILM_ID = ? AND USER_ID = ?", filmId, userId);

    }

    public int selectCountLikeByFilmId(Integer filmId) {
        return jdbcTemplate.query("SELECT count(USER_ID) FROM FILMS_USERS WHERE FILM_ID = ?",
                        new BeanPropertyRowMapper<>(Integer.class),
                        filmId)
                .stream().findAny()
                .orElse(0);

    }
}
