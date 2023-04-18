package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.imp.database.GenreRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class GenreService {

    private final GenreRepository genreRepository;

    public List<Genre> getAllGenres() {
        return genreRepository.findAll();
    }

    public Genre getGenreById(Integer id) {
        if (id < 0) {
            throw new ValidationException("Invalid genre id");
        }
        return genreRepository.findById(id);
    }

}
