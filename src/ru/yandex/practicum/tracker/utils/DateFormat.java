package ru.yandex.practicum.tracker.utils;

import java.time.format.DateTimeFormatter;

public class DateFormat {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter
            .ofPattern("dd.MM.yyyy HH:mm");

    public static DateTimeFormatter getDateTimeFormat() {
        return DATE_TIME_FORMATTER;
    }
}
