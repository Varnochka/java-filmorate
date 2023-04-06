package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ExistUserException;
import ru.yandex.practicum.filmorate.exception.NoSuchUserException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.HashSet;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {
    private final UserStorage userStorage;

    public User createUser(User user) {
        log.info("Request create new User");

        Optional<User> userOptional = userStorage.findById(user.getId());
        if (userOptional.isPresent()) {
            log.error("User with id='{}' already exist", user.getId());
            throw new ExistUserException("User with login='" + user.getLogin() + "' already exist");
        }

        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }

        User newUser = userStorage.create(user);

        log.info("Added new user {}", newUser);
        return newUser;
    }

    public User updateUser(User user) {
        log.info("Request update user");

        if (user.getId() == null || user.getId() <= 0) {
            throw new ValidationException("Invalid id='" + user.getId() + "' of updatable user");
        }

        Optional<User> userOptional = userStorage.findById(user.getId());

        if (userOptional.isEmpty()) {
            log.error("User with id='{}' not found", user.getId());
            throw new NoSuchUserException("User with id='" + user.getId() + "' not found");
        }

        userOptional = userStorage.findByLogin(user.getLogin());

        if (userOptional.isPresent()) {
            log.error("User with login='{}' already exist", user.getId());
            throw new ExistUserException("User with login='" + user.getLogin() + "' already exist");
        }

        User updatableUser = userStorage.update(user);

        log.info("Updatable user {}", user);
        return updatableUser;
    }

    public List<User> getAllUsers() {
        log.info("Request get all users");
        return userStorage.getAllUsers();
    }

    public User getUserById(Integer id) {
        log.info("Request get user by id='{}'", id);
        return userStorage.findById(id)
                .orElseThrow(() -> new NoSuchUserException("User with id='" + id + "' not found"));
    }

    public void addFriend(Integer userId, Integer friendId) {
        log.info("Request add friend");

        User foundUser = getUserById(userId);
        User foundFriend = getUserById(friendId);

        foundUser.addFriend(friendId);
        foundFriend.addFriend(userId);
    }

    public void deleteFriend(Integer userId, Integer friendId) {
        log.info("Request delete friend");

        User user = getUserById(userId);
        User friend = getUserById(friendId);

        friend.deleteFriend(friendId);
        user.deleteFriend(userId);
    }

    public List<User> getFriendsByUserId(Integer userId) {
        log.info("Request get friend by user id");

        User user = getUserById(userId);
        return userStorage.getUsersByIds(user.getFriends());
    }

    public List<User> getMutualFriends(Integer id, Integer otherId) {
        log.info("Request get mutual friends by user id");

        User user = getUserById(id);
        User otherUser = getUserById(otherId);

        Set<Integer> userFriendsIds = user.getFriends();
        Set<Integer> otherUserFriendsIds = otherUser.getFriends();

        Set<Integer> friends = new HashSet<>(userFriendsIds);

        friends.retainAll(otherUserFriendsIds);

        return userStorage.getUsersByIds(friends);
    }
}
