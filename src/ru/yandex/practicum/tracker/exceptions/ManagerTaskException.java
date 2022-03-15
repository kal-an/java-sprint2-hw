package ru.yandex.practicum.tracker.exceptions;

public class ManagerTaskException extends RuntimeException{

    public ManagerTaskException() {
    }

    public ManagerTaskException(String message) {
        super(message);
    }
}
