package ru.yandex.practicum.filmorate.storage.imp.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

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
    public void create(User user) {
        jdbcTemplate.update("INSERT INTO USERS (EMAIL, LOGIN, NAME, BIRTHDAY) VALUES (?, ?, ?, ?)",
                user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());

        int lastId = jdbcTemplate.queryForObject("SELECT max(id) FROM users", Integer.class);
    }

    @Override
    public Optional<User> findById(Integer id) {
        List<User> users = jdbcTemplate.query("SELECT * FROM USERS WHERE ID = ?", new Object[]{id},
                new BeanPropertyRowMapper<>(User.class));

        Optional<User> optionalUser = users
                .stream()
                .findAny();
        return optionalUser;
    }

    @Override
    public Optional<User> findByLogin(String login) {
        List<User> users = jdbcTemplate
                .query("SELECT * FROM USERS WHERE LOGIN = ?", new Object[]{login},
                        new BeanPropertyRowMapper<>(User.class));

        Optional<User> optionalUser = users
                .stream()
                .findAny();
        return optionalUser;
    }

    @Override
    public List<User> findAll() {
        List<User> users = jdbcTemplate.query("SELECT * FROM USERS", new BeanPropertyRowMapper<>(User.class));
        return users;
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
        jdbcTemplate.update("UPDATE USERS SET EMAIL = ?, LOGIN = ?, NAME = ?, BIRTHDAY = ? WHERE ID = ?",
                user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId()
        );
        return user;
    }

    @Override
    public void deleteById(Integer id) {
        jdbcTemplate.update("DELETE FROM USERS WHERE id=?", id);
    }
}
