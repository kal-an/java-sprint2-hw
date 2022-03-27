package ru.yandex.practicum.tracker.utils;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;

public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
    @Override
    public void write(JsonWriter jsonWriter, LocalDateTime localDateTime) throws IOException {
        jsonWriter.value(localDateTime.format(DateFormat.getDateTimeFormat()));
    }

    @Override
    public LocalDateTime read(JsonReader jsonReader) throws IOException {
        return LocalDateTime.parse(jsonReader.nextString(), DateFormat.getDateTimeFormat());
    }
}
