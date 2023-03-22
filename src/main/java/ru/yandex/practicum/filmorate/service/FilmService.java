package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exception.ExistFilmException;
import ru.yandex.practicum.filmorate.exception.NoSuchFilmException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;

    public Film createFilm(@Valid @RequestBody Film film) {
        log.info("Request create new Film");

        if (filmStorage.exists(film)) {
            log.error("Film same as {} already exists", film);
            throw new ExistFilmException("Film already exists");
        }

        Film createdFilm = filmStorage.create(film);

        log.info("Added new film {}", film);
        return createdFilm;
    }

    public Film updateFilm(@RequestBody Film film) {
        log.info("Request update film");

        if (film.getId() == null || film.getId() <= 0) {
            throw new ValidationException("Invalid id='" + film.getId() + "' of updatable film");
        }

        Optional<Film> filmOptional = filmStorage.findById(film.getId());

        if (filmOptional.isEmpty()) {
            throw new NoSuchFilmException("Film with id='" + film.getId() + "' not found");
        }

        log.info("Updatable film {}", film);
        return filmStorage.update(film);
    }

    public Film getFilmById(Integer id) {
        log.info("Request get film by id='{}'", id);
        return filmStorage.findById(id).orElseThrow(
                () -> new NoSuchFilmException("Film with id='" + id + "' not found"));
    }

    public List<Film> getAllFilms() {
        log.info("Request get all films");
        return filmStorage.getAllFilms();
    }

    public void addLikeByFilmId(Integer filmId, Integer userId) {
        log.info("Request add like user with id='{}' to film by id='{}'", userId, filmId);
        Film film = getFilmById(filmId);
        film.addLike(userId);
    }

    public void deleteLikeByFilmId(Integer filmId, Integer userId) {
        log.info("Request delete like user with id='{}' to film by id='{}'", userId, filmId);
        Film film = getFilmById(filmId);
        userService.getUserById(userId);
        film.deleteLike(userId);
    }

    public List<Film> getPopularFilms(Integer count) {
        log.info("Request get popular films");
        return filmStorage.findPopularFilms(count);
    }
}
