package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.repository.imp.database.MpaRepository;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class MpaService {

    private final MpaRepository mpaRepository;

    public List<Mpa> getAllMpa() {
        return mpaRepository.findAll();
    }

    public Mpa getMpaRatingById(Integer id) {
        return mpaRepository.findById(id);
    }

}
