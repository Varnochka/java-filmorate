package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NoSuchGenreException;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.imp.dao.MpaRepository;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class MpaService {
    private final MpaRepository mpaRepository;

    public List<Rating> getAllMpa() {
        return mpaRepository.findAll();
    }

    public Rating getMpaRatingById(Integer id) {
        try {
            return mpaRepository.findById(id).get();
        } catch (DataAccessException exception) {
            throw new NoSuchGenreException("Mpa rating with id='" + id + "' not found");
        }
    }
}
