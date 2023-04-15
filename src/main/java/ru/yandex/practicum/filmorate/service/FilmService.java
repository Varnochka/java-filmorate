package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ExistFilmException;
import ru.yandex.practicum.filmorate.exception.NoSuchFilmException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class FilmService {
    private final FilmStorage filmRepository;
    private final UserService userService;

    public List<Film> getAllFilms() {
        log.info("Request get all films");
        return filmRepository.findAll();
    }

    public Film getFilmById(Long id) {
        log.info("Request get film by id='{}'", id);
        return filmRepository.findById(id).orElseThrow(
                () -> new NoSuchFilmException("Film with id='" + id + "' not found"));
    }

    public Film createFilm(Film film) {
        log.info("Request create new Film {}", film);

        if (isExistFilm(film)) {
            log.error("Film same as {} already exists", film);
            throw new ExistFilmException("Film already exists");
        }

        return filmRepository.save(film).orElse(new Film());
    }

    public Film updateFilm(Film film) {
        log.info("Request update film");

        if (film.getId() == null || film.getId() <= 0) {
            throw new ValidationException("Invalid id='" + film.getId() + "' of updatable film");
        }

        Optional<Film> filmOptional = filmRepository.findById(film.getId());

        if (filmOptional.isEmpty()) {
            throw new NoSuchFilmException("Film with id='" + film.getId() + "' not found");
        }

        return filmRepository.update(film).orElse(new Film());
    }

    public void addLikeByFilmId(Long filmId, Integer userId) {
        log.info("Request add like user with id='{}' to film by id='{}'", userId, filmId);

        Optional<Film> optionalFilm = filmRepository.findById(filmId);
        if (optionalFilm.isEmpty()) {
            throw new NoSuchFilmException("Film with id='" + filmId + "' not found");
        }
        userService.getUserById(userId);

        filmRepository.addLike(filmId, userId);
    }

    public void deleteLikeByFilmId(Long filmId, Integer userId) {
        log.info("Request delete like user with id='{}' to film by id='{}'", userId, filmId);

        Optional<Film> optionalFilm = filmRepository.findById(filmId);
        if (optionalFilm.isEmpty()) {
            throw new NoSuchFilmException("Film with id='" + filmId + "' not found");
        }
        userService.getUserById(userId);

        filmRepository.deleteLike(filmId, userId);
    }

    private boolean isExistFilm(Film film) {
        return filmRepository.isExistFilm(film).isPresent();
    }


    public List<Film> getPopularFilms(Integer count) {
        log.info("Request get popular films");
        return filmRepository.findPopularFilms(count);
    }
}
