package ru.yandex.practicum.tracker.utils;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import ru.yandex.practicum.tracker.tasks.State;
import ru.yandex.practicum.tracker.tasks.Task;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

public class TaskAdapter extends TypeAdapter<Task> {

    @Override
    public void write(JsonWriter jsonWriter, Task task) throws IOException {
        jsonWriter.beginObject();
        jsonWriter.name("name").value(task.getTaskName());
        jsonWriter.name("description").value(task.getTaskDescription());
        jsonWriter.name("id").value(task.getTaskId());
        jsonWriter.name("status").value(String.valueOf(task.getTaskStatus()));
        jsonWriter.name("duration").value(task.getDuration().toMinutes());
        jsonWriter.name("startTime").value(task.getStartTime().format(DateFormat.getDateTimeFormat()));
        jsonWriter.endObject();
    }

    @Override
    public Task read(JsonReader jsonReader) throws IOException {
        jsonReader.beginObject();
        String name = jsonReader.nextString();
        String description = jsonReader.nextString();
        long id = jsonReader.nextLong();
        State status = State.valueOf(jsonReader.nextString());
        Duration duration = Duration.ofMinutes(jsonReader.nextLong());
        LocalDateTime startTime = LocalDateTime.parse(jsonReader.nextString(),
                DateFormat.getDateTimeFormat());
        jsonReader.endObject();

        return new Task(name, description, id, status, duration, startTime);
    }
}
