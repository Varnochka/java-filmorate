package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.FilmStorage;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserService userService;


    public List<Film> getAllFilms() {
        log.info("Request get all films");
        return filmStorage.findAllFilm();
    }

    public Film getFilmById(Long id) {
        log.info("Request get film by id='{}'", id);
        return filmStorage.findFilmById(id).orElseThrow(
                () -> new NoSuchElementException("Film with id='" + id + "' not found"));
    }

    public Film createFilm(Film film) {
        log.info("Request create new Film {}", film);
        return filmStorage.save(film);
    }

    public Film updateFilm(Film film) {
        log.info("Request update film");
        filmStorage.findFilmById(film.getId())
                .orElseThrow(() -> new NoSuchElementException("Film with id='" + film.getId() + "' not found"));

        return filmStorage.update(film);
    }

    public void addLikeByFilmId(Long filmId, Integer userId) {
        log.info("Request add like user with id='{}' to film by id='{}'", userId, filmId);
        filmStorage.findFilmById(filmId)
                .orElseThrow(() -> new NoSuchElementException("Film with id='" + filmId + "' not found"));

        userService.getUserById(userId);
        filmStorage.addLike(filmId, userId);
    }

    public void deleteLikeByFilmId(Long filmId, Integer userId) {
        log.info("Request delete like user with id='{}' to film by id='{}'", userId, filmId);
        filmStorage.findFilmById(filmId)
                .orElseThrow(() -> new NoSuchElementException("Film with id='" + filmId + "' not found"));

        userService.getUserById(userId);
        filmStorage.deleteLike(filmId, userId);
    }

    public List<Film> getPopularFilms(int count) {
        log.info("Request get popular films");
        return filmStorage.findAllFilm()
                .stream()
                .sorted((o1, o2) -> o2.getUserLikes().size() - o1.getUserLikes().size())
                .limit(count)
                .collect(Collectors.toList());
    }

}
