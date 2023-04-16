package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    Film save(Film film);

    Film update(Film film);

    List<Film> findAllFilm();

    Optional<Film> findFilmById(Long id);

    void addLike(long filmId, long userId);

    void deleteLike(long filmId, long userId);

}
