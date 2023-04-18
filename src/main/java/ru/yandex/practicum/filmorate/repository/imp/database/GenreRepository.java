package ru.yandex.practicum.filmorate.repository.imp.database;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.imp.database.mapper.GenreMapper;

import java.util.List;
import java.util.NoSuchElementException;

@Repository
@RequiredArgsConstructor
public class GenreRepository {

    private final JdbcTemplate jdbcTemplate;

    public List<Genre> findAll() {
        return jdbcTemplate.query("SELECT * FROM genres",
                new GenreMapper());
    }

    public Genre findById(Integer id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM genres WHERE id = ?",
                    new GenreMapper(), id);
        } catch (DataAccessException exception) {
            throw new NoSuchElementException("Genre with id='" + id + "' not found");
        }
    }
}
