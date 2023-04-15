package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Optional<Film> save(Film film);

    Optional<Film> update(Film film);

    List<Film> findAll();

    Optional<Film> findById(Long id);

    List<Film> findPopularFilms(Integer count);

    Optional<Film>  isExistFilm(Film film);

    void deleteById(Long id);

   void addLike(long filmId, long userId);

    void deleteLike(long filmId, long userId);

}
