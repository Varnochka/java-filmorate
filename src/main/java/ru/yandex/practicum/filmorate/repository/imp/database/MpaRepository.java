package ru.yandex.practicum.filmorate.repository.imp.database;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.NoSuchElementException;

@Repository
@RequiredArgsConstructor
public class MpaRepository {

    private final JdbcTemplate jdbcTemplate;

    public List<Mpa> findAll() {
        return jdbcTemplate.query("SELECT * FROM MPA", new BeanPropertyRowMapper<>(Mpa.class));
    }

    public Mpa findById(Integer id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM MPA WHERE ID = ?",
                    new BeanPropertyRowMapper<>(Mpa.class), id);
        } catch (DataAccessException exception) {
            throw new NoSuchElementException("Mpa rating with id='" + id + "' not found");
        }
    }

}
