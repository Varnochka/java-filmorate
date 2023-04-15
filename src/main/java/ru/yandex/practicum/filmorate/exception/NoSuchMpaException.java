package ru.yandex.practicum.filmorate.exception;

import java.util.NoSuchElementException;

public class NoSuchMpaException extends NoSuchElementException {

    public NoSuchMpaException(String message) {
        super(message);
    }
}
