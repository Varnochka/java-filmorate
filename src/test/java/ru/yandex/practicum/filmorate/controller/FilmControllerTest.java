package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ExistFilmException;
import ru.yandex.practicum.filmorate.exception.NoSuchFilmException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.imp.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.imp.InMemoryUserStorage;

import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerTest {

    private FilmController filmController;
    private Film film;
    private static Validator validator;

    @BeforeAll
    static void beforeAll() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @BeforeEach
    void init() {
        film = new Film(null, "Title film", "Description film", LocalDate.now(), 60);
        FilmStorage filmStorage = new InMemoryFilmStorage();
        UserStorage userStorage = new InMemoryUserStorage();
        UserService userService = new UserService(userStorage);
        FilmService filmService = new FilmService(filmStorage, userService);
        filmController = new FilmController(filmService);
    }

    @Test
    void createFilm_rejectName_nameIsEmpty() {
        film.setName("");
        assertEquals(1, validator.validate(film).size());
    }

    @Test
    void createFilm_rejectName_nameIsNull() {
        film.setName(null);
        assertEquals(1, validator.validate(film).size());
    }

    @Test
    void createFilm_acceptName_nameIsNotEmptyAndNotNull() {
        assertTrue(validator.validate(film).isEmpty());
    }

    @Test
    void createFilm_acceptDescription_descriptionIsNotEmptyAndNotNull() {
        assertTrue(validator.validate(film).isEmpty());
    }

    @Test
    void createFilm_rejectDescription_descriptionIsNull() {
        film.setDescription(null);
        assertEquals(1, validator.validate(film).size());
    }

    @Test
    void createFilm_rejectDescription_descriptionIsEmpty() {
        film.setDescription("");
        assertEquals(1, validator.validate(film).size());
    }

    @Test
    void createFilm_rejectReleaseDate_releaseDateIsIncorrect() {
        film.setReleaseDate(LocalDate.of(1000, 1, 1));
        assertEquals(1, validator.validate(film).size());
    }

    @Test
    void createFilm_acceptReleaseDate_releaseDateIsCorrect() {
        assertTrue(validator.validate(film).isEmpty());
    }

    @Test
    void createFilm_acceptDuration_durationIsCorrect() {
        assertTrue(validator.validate(film).isEmpty());
    }

    @Test
    void addFilm_existFilmException_filmAlreadyExist() {
        filmController.createFilm(film);

        Film newFilm = new Film(null, "Title film", "Description film", LocalDate.now(), 60);

        assertThrows(ExistFilmException.class, () -> filmController.createFilm(newFilm));
    }

    @Test
    void updateFilm_validationException_filmIdIsNull() {
        filmController.createFilm(film);

        Film newFilm = new Film(null, "Title film", "Description film", LocalDate.now(), 60);

        assertThrows(ValidationException.class, () -> filmController.updateFilm(newFilm));
    }

    @Test
    void updateFilm_validationException_filmIdIsIncorrect() {
        filmController.createFilm(film);

        Film newFilm = new Film(-5, "Title film", "Description film", LocalDate.now(), 60);

        assertThrows(ValidationException.class, () -> filmController.updateFilm(newFilm));
    }

    @Test
    void updateFilm_noSuchFilmException_filmIdIsNotExist() {
        filmController.createFilm(film);

        Film newFilm = new Film(10, "Title film", "Description film", LocalDate.now(), 60);

        assertThrows(NoSuchFilmException.class, () -> filmController.updateFilm(newFilm));
    }
}
