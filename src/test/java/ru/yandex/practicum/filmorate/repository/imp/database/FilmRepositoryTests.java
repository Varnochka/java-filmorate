package ru.yandex.practicum.filmorate.repository.imp.database;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmRepositoryTests {

    private final FilmRepository underTest;

    @BeforeEach
    void init() {
        Film film = Film.builder()
                .id(1L)
                .name("Test film")
                .description("Describe film")
                .releaseDate(LocalDate.now())
                .duration(30L)
                .mpa(new Mpa(1, "G"))
                .build();

        if (underTest.findFilmById(1L).isEmpty()) {
            underTest.save(film);
            return;
        }
        underTest.update(film);
    }

    @AfterEach
    void deleteAllCreatedFilms() {
        underTest.findAllFilm()
                .stream()
                .filter(film -> film.getId() != 1L)
                .forEach(film -> underTest.deleteById(film.getId()));
    }

    @Test
    void findAll_size1ListSize_created1Films() {
        List<Film> result = underTest.findAllFilm();
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    void findAll_emptyList_notCreatedFilms() {
        underTest.findAllFilm()
                .forEach(film -> underTest.deleteById(film.getId()));
        List<Film> result = underTest.findAllFilm();
        assertThat(result.size()).isEqualTo(0);
    }



    @Test
    void update_filmNotUpdated_idIsNotFound() {
        Film updatableFilm = Film.builder()
                .id(2L)
                .name("Updated Name Film")
                .description("Describe film")
                .releaseDate(LocalDate.now())
                .duration(30L)
                .mpa(new Mpa(1, "G"))
                .build();

        underTest.update(updatableFilm);

        Optional<Film> filmOptional = underTest.findFilmById(2L);
        assertThat(filmOptional).isNotPresent();
    }

    @Test
    void update_updatedNameFilm_correctUpdatableFilm() {
        Film deletedFilm = underTest.findAllFilm().get(0);
        Film updatableFilm = Film.builder()
                .id(deletedFilm.getId())
                .name("Updated Name Film")
                .description("Describe film")
                .releaseDate(LocalDate.now())
                .duration(30L)
                .mpa(new Mpa(1, "G"))
                .build();

        underTest.update(updatableFilm);

        Optional<Film> filmOptional = underTest.findFilmById(deletedFilm.getId());
        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("name", "Updated Name Film"));
    }

    @Test
    void delete_emptyResultFindById_correctIdDeletableFilm() {
        Film film = Film.builder()
                .id(2L)
                .name("Second Film")
                .description("Describe film")
                .releaseDate(LocalDate.now())
                .duration(30L)
                .mpa(new Mpa(1, "G"))
                .build();

        underTest.save(film);
        underTest.deleteById(2L);

        Optional<Film> filmOptional = underTest.findFilmById(2L);
        assertThat(filmOptional).isNotPresent();
    }

    @Test
    void delete_film_incorrectIdDeletableFilm() {
        Film film = Film.builder()
                .name("Second Film")
                .description("Describe film")
                .releaseDate(LocalDate.now())
                .duration(70L)
                .mpa(new Mpa(1, "G"))
                .build();

        underTest.save(film);
        underTest.deleteById(4L);

        Optional<Film> resultOptional = underTest.findFilmById(film.getId());
        assertThat(resultOptional).isPresent();
    }

    @Test
    void findAll_listWithFilmsIs2_created2Films() {
        Film film = Film.builder()
                .id(2L)
                .name("Second Film")
                .description("Describe film")
                .releaseDate(LocalDate.now())
                .duration(30L)
                .mpa(new Mpa(1, "G"))
                .build();

        underTest.save(film);

        List<Film> result = underTest.findAllFilm();
        Assertions.assertEquals(2, result.size());
    }

}