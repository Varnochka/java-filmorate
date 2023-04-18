package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserStorage {
    Optional<User> create(User user);

    List<User> findAll();

    Optional<User> findById(Integer id);

    User update(User user);

    void deleteById(Integer id);

    List<User> findUsersByIds(Set<Integer> ids);

    Optional<User> findByLogin(String login);

}

