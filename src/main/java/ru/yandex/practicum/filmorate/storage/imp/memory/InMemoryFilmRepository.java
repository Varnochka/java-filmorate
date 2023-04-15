package ru.yandex.practicum.filmorate.storage.imp.memory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryFilmRepository implements FilmStorage {

    private long idFilm = 1;
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Optional<Film> save(Film film) {
        film.setId(idFilm++);
        films.put(film.getId(), film);
        return Optional.of(films.get(film.getId()));
    }

    @Override
    public Optional<Film> update(Film film) {
        films.put(film.getId(), film);
        return Optional.of(film);
    }

    @Override
    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Optional<Film> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public List<Film> findPopularFilms(Integer count) {
        return films.values()
                .stream()
                .sorted((o1, o2) -> Integer.compare(o2.getUserLikes().size(), o1.getUserLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        films.remove(id);
    }

    @Override
    public void addLike(long filmId, long userId) {
        films.get(filmId).getUserLikes().add(userId);
    }

    @Override
    public void deleteLike(long filmId, long userId) {
        films.get(filmId).getUserLikes().remove(userId);
    }

    @Override
    public Optional<Film> isExistFilm(Film film) {
        return films.values()
                .stream()
                .filter(filmSaved -> (filmSaved.equals(film))).findAny();
    }

}