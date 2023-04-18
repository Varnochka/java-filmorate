package ru.yandex.practicum.filmorate.repository.imp.memory;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.UserStorage;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();
    private int idUser = 1;

    @Override
    public Optional<User> create(User user) {
        user.setId(idUser++);
        users.put(user.getId(), user);
        return Optional.of(user);
    }

    @Override
    public Optional<User> findById(Integer id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public User update(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void deleteById(Integer id) {
        Optional<User> optionalUser = findById(id);
        optionalUser.ifPresent(user -> users.remove(user.getId()));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public List<User> findUsersByIds(Set<Integer> usersIds) {
        List<User> foundUsers = new ArrayList<>();

        usersIds.forEach(id -> foundUsers.add(users.get(id)));
        return foundUsers;
    }

    @Override
    public Optional<User> findByLogin(String login) {
        return users.values()
                .stream().filter(user -> user.getLogin().equals(login))
                .findAny();
    }

}
