package ru.yandex.practicum.tracker.utils;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import ru.yandex.practicum.tracker.tasks.Epic;
import ru.yandex.practicum.tracker.tasks.State;
import ru.yandex.practicum.tracker.tasks.Task;

import java.io.IOException;

public class EpicAdapter extends TypeAdapter<Epic> {

    @Override
    public void write(JsonWriter jsonWriter, Epic task) throws IOException {
        jsonWriter.beginObject();
        jsonWriter.name("name").value(task.getTaskName());
        jsonWriter.name("description").value(task.getTaskDescription());
        jsonWriter.name("id").value(task.getTaskId());
        jsonWriter.name("status").value(String.valueOf(task.getTaskStatus()));
        jsonWriter.name("duration").value(task.getDuration().toMinutes());
        jsonWriter.name("startTime").value(task.getStartTime().format(DateFormat.getDateTimeFormat()));
        jsonWriter.name("subTasks").value(task.getSubTasks().toString());
        jsonWriter.endObject();
    }

    @Override
    public Epic read(JsonReader jsonReader) throws IOException {
        jsonReader.beginObject();
        jsonReader.nextName();
        String name = jsonReader.nextString();
        jsonReader.nextName();
        String description = jsonReader.nextString();
        jsonReader.nextName();
        long id = jsonReader.nextLong();
        jsonReader.endObject();
        return new Epic(name, description, State.NEW, id);
    }
}
