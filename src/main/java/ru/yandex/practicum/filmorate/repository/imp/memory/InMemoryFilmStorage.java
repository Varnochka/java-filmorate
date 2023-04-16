package ru.yandex.practicum.filmorate.repository.imp.memory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.FilmStorage;

import java.util.*;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();
    private long idFilm = 1;

    @Override
    public Film save(Film film) {
        film.setId(idFilm++);
        films.put(film.getId(), film);
        return films.get(film.getId());
    }

    @Override
    public Film update(Film film) {
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public List<Film> findAllFilm() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Optional<Film> findFilmById(Long id) {
        return Optional.empty();
    }


    @Override
    public void addLike(long filmId, long userId) {
        films.get(filmId).getUserLikes().add(userId);
    }

    @Override
    public void deleteLike(long filmId, long userId) {
        films.get(filmId).getUserLikes().remove(userId);
    }

}
