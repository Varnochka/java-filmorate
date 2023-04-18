package ru.yandex.practicum.filmorate.repository.imp.database;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserRepositoryTest {

    private final UserRepository underTest;

    @BeforeEach
    public void init() {
        User user = User.builder()
                .id(1)
                .name("Name")
                .email("email@email.email")
                .login("Login")
                .birthday(LocalDate.now())
                .build();

        if (underTest.findById(1).isEmpty()) {
            underTest.create(user);
            return;
        }
        underTest.update(user);
    }

    @AfterEach
    public void deleteAllCreatedUsers() {
        underTest.findAll().stream()
                .filter(user -> user.getId() != 1L)
                .forEach(user -> underTest.deleteById(user.getId()));
    }

    @Test
    void findAll_size1ListSize_created1User() {
        List<User> result = underTest.findAll();
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void findById_user_userWithIdExist() {
        Optional<User> userOptional = underTest.findById(1);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user -> assertThat(user)
                        .hasFieldOrPropertyWithValue("id", 1))
                .hasValueSatisfying(user -> assertThat(user)
                        .hasFieldOrPropertyWithValue("login", "Login"));
    }

    @Test
    public void findById_emptyResult_incorrectId() {
        Optional<User> userOptional = underTest.findById(2);
        assertThat(userOptional).isNotPresent();
    }

    @Test
    public void update_userNotWasUpdate_incorrectIdUpdatableUser() {
        User userTestUpdate = User.builder()
                .id(2)
                .name("Updatable name")
                .email("email@email.email")
                .login("Login")
                .birthday(LocalDate.now())
                .build();

        underTest.update(userTestUpdate);

        Optional<User> userOptional = underTest.findById(1);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("name", "Name"));
    }

    @Test
    public void update_updatableUser_correctIdUpdatableUser() {
        System.out.println(underTest.findAll());
        User updatableUser = User.builder()
                .id(1)
                .name("Updatable name")
                .email("email@email.email")
                .login("Login")
                .birthday(LocalDate.now())
                .build();

        underTest.update(updatableUser);

        Optional<User> userOptional = underTest.findById(updatableUser.getId());

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("name", "Updatable name"));
    }

    @Test
    public void delete_emptyResult_correctIdDeletedUser() {
        User user = User.builder()
                .name("Updatable name")
                .email("emailNEW@email.email")
                .login("uniqueLogin")
                .birthday(LocalDate.now())
                .build();

        underTest.create(user);

        underTest.deleteById(2);

        Optional<User> userOptional = underTest.findById(2);
        assertThat(userOptional).isNotPresent();
    }
}