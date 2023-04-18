package ru.yandex.practicum.filmorate.repository.imp.database;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Primary
@Repository
@RequiredArgsConstructor
public class UserRepository implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<User> create(User user) {
        jdbcTemplate.update("INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)",
                user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());

        return findByLogin(user.getLogin());
    }

    @Override
    public Optional<User> findById(Integer id) {
        List<User> users = jdbcTemplate.query("SELECT * FROM users WHERE id = ?", new Object[]{id},
                new BeanPropertyRowMapper<>(User.class));

        return users
                .stream()
                .findAny();
    }

    @Override
    public Optional<User> findByLogin(String login) {
        List<User> users = jdbcTemplate.query("SELECT * FROM users WHERE login = ?", new Object[]{login},
                        new BeanPropertyRowMapper<>(User.class));

        return users
                .stream()
                .findAny();
    }

    @Override
    public List<User> findAll() {
        return jdbcTemplate.query("SELECT * FROM users", new BeanPropertyRowMapper<>(User.class));
    }

    @Override
    public List<User> findUsersByIds(Set<Integer> ids) {
        ArrayList<User> list = new ArrayList<>();
        for (Integer id : ids) {
            list.add(findById(id).get());
        }
        return list;
    }

    @Override
    public User update(User user) {
        jdbcTemplate.update("UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?",
                user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId()
        );
        return user;
    }

    @Override
    public void deleteById(Integer id) {
        jdbcTemplate.update("DELETE FROM users WHERE id=?", id);
    }

}
