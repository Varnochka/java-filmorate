package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ExistUserException;
import ru.yandex.practicum.filmorate.exception.NoSuchUserException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {
    private final UserStorage userStorage;
    private final FriendshipService friendshipService;

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

        userStorage.create(user);
        User createdUser = getUserByLogin(user.getLogin());

        log.info("Added new user {}", createdUser);
        return createdUser;
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
        return userStorage.findAll();
    }

    public User getUserById(Integer id) {
        return userStorage.findById(id)
                .orElseThrow(() -> new NoSuchUserException("User with id='" + id + "' not found"));
    }

    public User getUserByLogin(String login) {
        return userStorage.findByLogin(login)
                .orElseThrow(() -> new NoSuchUserException("User with login='" + login + "' not found"));
    }

    public void addFriend(Integer userId, Integer friendId) {
        log.info("Request add friend");

        checkExistById(userId);
        checkExistById(friendId);

        List<User> friend = friendshipService.getFriend(userId, friendId);

        if (friend.isEmpty()) {
            friendshipService.addFriend(userId, friendId);
        }
    }

    public void deleteFriend(Integer userId, Integer friendId) {
        log.info("Request delete friend");
        friendshipService.deleteFriendship(userId, friendId);
    }

    public List<User> getFriendsByUserId(Integer userId) {
        log.info("Request get friend by user id");

        checkExistById(userId);

        return friendshipService.getFriendsByUserId(userId);
    }

    public List<User> getCommonFriends(Integer id, Integer otherId) {
        log.info("Request get common friends by users id='{}' and id='{}'", id, otherId);
        return friendshipService.getCommonFriends(id, otherId);
    }


    private void checkExistById(Integer id) {
        if (userStorage.findById(id).isEmpty()) {
            throw new NoSuchUserException("User with id='" + id + "' not found");
        }
    }
}
