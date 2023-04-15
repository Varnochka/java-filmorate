package ru.yandex.practicum.filmorate.storage.imp.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NoSuchFilmException;
import ru.yandex.practicum.filmorate.exception.NoSuchGenreException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Primary
@Repository
@RequiredArgsConstructor
public class FilmRepository implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    private final GenreRepository genreRepository;

    @Override
    public Optional<Film> save(Film film) {
        long filmId = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("id")
                .executeAndReturnKey(film.toMap()).longValue();

        film.setId(filmId);
        List<Genre> genres = film.getGenres();
        if (genres != null && !genres.isEmpty()) {
            for (Genre genre : genres) {
                jdbcTemplate.update("INSERT INTO FILMS_GENRES (FILM_ID, GENRE_ID) VALUES(?, ?)",
                        film.getId(), genre.getId());
            }
        }
        return Optional.of(film);
    }

    @Override
    public List<Film> findAll() {
        List<Film> films = jdbcTemplate.query("SELECT f.ID, f.NAME, f.DESCRIPTION, f.DURATION, " +
                "f.RELEASE_DATE, mpa.ID AS mpa_id, mpa.NAME AS mpa_name " +
                "FROM FILMS AS f " +
                "JOIN MPA AS mpa ON f.MPA_ID = mpa.ID", new FilmMapper());

        for (Film film : films) {
            List<Genre> genres = genreRepository.findAllByFilmId(film.getId());
            if (!genres.isEmpty()) {
                Collections.reverse(genres);
                film.setGenres(genres);
            } else {
                film.setGenres(new ArrayList<>());
            }
        }

        return films;
    }

    @Override
    public Optional<Film> update(Film film) {
        jdbcTemplate.update(
                "UPDATE FILMS SET NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ?, MPA_ID = ? WHERE ID = ?",
                film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId(), film.getId());
        setGenresToFilm(film);
        film.setGenres(genreRepository.findAllByFilmId(film.getId()));
        film.setMpa(getMpaToFilm(film));
        return Optional.of(film);
    }

    private Rating getMpaToFilm(Film film) {
        return jdbcTemplate.queryForObject(
                "SELECT f.MPA_ID as id, mpa.NAME AS name FROM FILMS f RIGHT JOIN MPA mpa ON f.MPA_ID = mpa.ID WHERE f.ID = ?",
                (rs, rowNum) -> Rating.builder()
                        .id(rs.getInt("id"))
                        .name(rs.getString("name"))
                        .build(), film.getId());
    }

    @Override
    public Optional<Film> findById(Long id) {
        Optional<Film> film = getAndValidationFilmByFilmId(id);
        if (film.isPresent()) {
            film.get().setGenres(genreRepository.findAllByFilmId(id));
            film.get().getUserLikes().addAll(new HashSet<>(getLikesByFilmId(id)));
        }
        return film;
    }

    @Override
    public List<Film> findPopularFilms(Integer count) {
        List<Film> films = jdbcTemplate.query("SELECT f.ID, f.NAME, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, " +
                        "m.ID AS mpa_id, m.NAME AS mpa_name " +
                        "FROM FILMS AS f " +
                        "JOIN MPA AS m ON f.MPA_ID = m.ID " +
                        "LEFT JOIN FILMS_USERS AS fu ON f.ID = fu.FILM_ID " +
                        "GROUP BY f.ID " +
                        "ORDER BY COUNT(fu.USER_ID) DESC " +
                        "LIMIT ?",
                new FilmMapper(), count);

        for (Film film : films) {
            List<Genre> genres = genreRepository.findAllByFilmId(film.getId());
            if (!genres.isEmpty()) {
                Collections.reverse(genres);
                film.setGenres(genres);
            }
        }
        return films;
    }

    private List<Long> getLikesByFilmId(long filmId) {
        return jdbcTemplate.queryForList("SELECT USER_ID FROM FILMS_USERS WHERE FILM_ID = ?", Long.class, filmId);
    }

    private Optional<Film> getAndValidationFilmByFilmId(Long id) {
        try {
            return Optional.ofNullable(jdbcTemplate
                    .queryForObject("SELECT f.ID, f.NAME, f.DESCRIPTION, f.DURATION, f.RELEASE_DATE, r.ID AS mpa_id, r.NAME AS mpa_name " +
                                    "FROM FILMS AS f " +
                                    "JOIN MPA AS r ON f.MPA_ID = r.ID " +
                                    "WHERE f.ID = ?",
                            new FilmMapper(), id));
        } catch (DataAccessException e) {
            throw new NoSuchFilmException("Film with id='" + id + "' not found");
        }
    }

    private void setGenresToFilm(Film film) {
        try {
            List<Genre> genresFromDbByFilm = genreRepository.findGenresIdByFilmId(film.getId());
            if (film.getGenres() != null && !film.getGenres().isEmpty()) {
                List<Genre> genresFromUI = new ArrayList<>(film.getGenres());
                if (genresFromDbByFilm.isEmpty()) {
                    updateFilmGenres(genresFromUI, film);
                } else {
                    List<Genre> matchedGenres = getGenresMatch(genresFromUI, genresFromDbByFilm);
                    genresFromUI.removeAll(matchedGenres);
                    genresFromDbByFilm.removeAll(matchedGenres);
                    deleteFilmGenresFromDb(genresFromDbByFilm);
                    updateFilmGenres(genresFromUI, film);
                }
            } else {
                deleteFilmGenresFromDb(genresFromDbByFilm);
            }
        } catch (DataAccessException e) {
            throw new NoSuchGenreException("Genre not found");
        }
    }

    private List<Genre> getGenresMatch(List<Genre> genres1, List<Genre> genres2) {
        return genres1.stream()
                .filter(front -> genres2.stream().anyMatch(db -> Objects.equals(db.getId(), front.getId())))
                .collect(Collectors.toList());
    }

    private void deleteFilmGenresFromDb(List<Genre> genres) {
        if (!genres.isEmpty()) {
            for (Genre genre : genres) {
                jdbcTemplate.update("DELETE FROM FILMS_GENRES WHERE GENRE_ID = ?", genre.getId());
            }
        }
    }

    @Override
    public void deleteById(Long id) {
        try {
            jdbcTemplate.update("DELETE FROM FILMS WHERE ID = ?", id);
        } catch (DataAccessException e) {
            throw new NoSuchGenreException("Genre not found");
        }
    }

    private void updateFilmGenres(List<Genre> genresFromFrontEnd, Film film) {
        if (!genresFromFrontEnd.isEmpty()) {
            HashSet<Genre> genres = new HashSet<>(genresFromFrontEnd);
            jdbcTemplate.batchUpdate("INSERT INTO FILMS_GENRES(FILM_ID, GENRE_ID) VALUES(?, ?)",
                    new BatchPreparedStatementSetter() {
                        @Override
                        public void setValues(PreparedStatement ps, int i) throws SQLException {
                            Genre genre = genresFromFrontEnd.get(i);
                            ps.setLong(1, film.getId());
                            ps.setLong(2, genre.getId());
                        }

                        @Override
                        public int getBatchSize() {
                            return genres.size();
                        }
                    });
        }
    }

    @Override
    public Optional<Film> isExistFilm(Film film) {
        List<Film> films = jdbcTemplate.query(
                "SELECT * FROM FILMS WHERE name = ? AND description = ? AND release_date = ? AND duration = ?",
                new BeanPropertyRowMapper<>(Film.class),
                film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration());

        return films.stream()
                .findAny();
    }

    @Override
    public void addLike(long filmId, long userId) {
        if (!isLikeExistsInFilm(filmId, userId)) {
            jdbcTemplate.update("INSERT INTO FILMS_USERS(FILM_ID, USER_ID) " +
                    "VALUES(?, ?)", filmId, userId);
        }
    }

    @Override
    public void deleteLike(long filmId, long userId) {
        jdbcTemplate.update("DELETE FROM FILMS_USERS WHERE FILM_ID = ? AND USER_ID = ?", filmId, userId);
    }

    private boolean isLikeExistsInFilm(long filmId, long userId) {
        return jdbcTemplate.queryForList("SELECT USER_ID FROM FILMS_USERS WHERE FILM_ID = ?", Long.class, filmId)
                .contains(userId);
    }

}