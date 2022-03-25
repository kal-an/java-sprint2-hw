package ru.yandex.practicum.tracker.tasks;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocaDateTimeAdapter extends TypeAdapter<LocalDateTime> {

    private static final DateTimeFormatter fWriter = DateTimeFormatter
            .ofPattern("dd.MM.yyyy HH:mm");
    private static final DateTimeFormatter fReader = DateTimeFormatter
            .ofPattern("dd.MM.yyyy HH:mm");

    @Override
    public void write(JsonWriter jsonWriter, LocalDateTime localDateTime) throws IOException {
        jsonWriter.value(localDateTime.format(fWriter));
    }

    @Override
    public LocalDateTime read(JsonReader jsonReader) throws IOException {
        return LocalDateTime.parse(jsonReader.nextString(), fReader);
    }
}
