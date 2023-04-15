package ru.yandex.practicum.filmorate.storage.imp.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class FriendshipRepository {
    private final JdbcTemplate jdbcTemplate;

    public void addFriend(Integer userId, Integer friendId) {
        jdbcTemplate.update("INSERT INTO FRIENDSHIP (user_id, friend_id, approved) VALUES (?, ?, ?)",
                userId, friendId, 0);
    }

    public List<User> getFriend(Integer id, Integer friendId) {
        return jdbcTemplate.query(
                "SELECT u.* FROM USERS u RIGHT JOIN FRIENDSHIP f ON u.id = f.friend_id WHERE f.user_id = ? AND f.friend_id = ?",
                new UserMapper(), friendId, id);
    }

    public void deleteFriendship(Integer userId, Integer friendId) {
        jdbcTemplate.update("DELETE FROM FRIENDSHIP WHERE (user_id = ? AND friend_id = ?) " +
                        "OR (user_id = ? AND friend_id = ?)",
                userId, friendId, friendId, userId);
    }

    public List<User> getFriendsByUserId(Integer id) {
        return jdbcTemplate.query(
                "SELECT u.* FROM USERS u RIGHT JOIN FRIENDSHIP f ON u.id = f.friend_id WHERE f.user_id = ?",
                new UserMapper(), id);
    }

    public List<User> getCommonFriends(int userId, int friendId) {
        return jdbcTemplate.query("SELECT * FROM USERS us " +
                        "JOIN FRIENDSHIP AS fr1 ON us.ID = fr1.FRIEND_ID " +
                        "JOIN FRIENDSHIP AS fr2 ON us.ID = fr2.FRIEND_ID " +
                        "WHERE fr1.USER_ID = ? AND fr2.USER_ID = ?",
                new UserMapper(), userId, friendId);
    }

}
