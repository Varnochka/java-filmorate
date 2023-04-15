package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.imp.dao.FilmUsersRepository;

@Service
@RequiredArgsConstructor
public class FilmUsersService {
    private final FilmUsersRepository filmUsersRepository;

    public void save(Integer filmId, Integer userId) {
        filmUsersRepository.save(filmId, userId);
    }

    public void delete(Integer filmId, Integer userId) {
        filmUsersRepository.delete(filmId, userId);
    }

    public int getCountLikeByFilmId(Integer filmId) {
        return filmUsersRepository.selectCountLikeByFilmId(filmId);
    }
}
