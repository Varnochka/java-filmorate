package ru.yandex.practicum.filmorate.storage.imp.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class GenreRepository {

    private final JdbcTemplate jdbcTemplate;

    public List<Genre> findAll() {
        return jdbcTemplate.query("SELECT * FROM GENRES",
                new GenreMapper());
    }

    public Optional<Genre> findById(Integer id) {
        Genre genre = jdbcTemplate.queryForObject("SELECT * FROM GENRES WHERE id = ?",
                new GenreMapper(), id);

        return Optional.ofNullable(genre);
    }


    public List<Genre> findAllByFilmId(Long id) {
        return jdbcTemplate.query("SELECT g.ID, g.NAME " +
                "FROM FILMS_GENRES AS fg " +
                "LEFT JOIN GENRES AS g ON fg.genre_id=g.id " +
                "WHERE FILM_ID = ?",
                new GenreMapper(), id);

    }

    public List<Genre> findGenresIdByFilmId(Long id) {
        return jdbcTemplate.query("SELECT GENRE_ID FROM FILMS_GENRES WHERE FILM_ID = ?",
                (rs, rowNum) -> Genre.builder()
                        .id(rs.getInt("genre_id"))
                        .build(), id);
    }
}
