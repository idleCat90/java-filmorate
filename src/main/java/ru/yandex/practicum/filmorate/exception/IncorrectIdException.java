package ru.yandex.practicum.filmorate.exception;

public class IncorrectIdException extends RuntimeException {

    public IncorrectIdException(String message) {
        super(message);
    }

    public IncorrectIdException(String message, Throwable cause) {
        super(message, cause);
    }

    public IncorrectIdException(Throwable cause) {
        super(cause);
    }


}
