package ru.yandex.practicum.tracker.utils;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import ru.yandex.practicum.tracker.tasks.State;
import ru.yandex.practicum.tracker.tasks.SubTask;
import ru.yandex.practicum.tracker.tasks.Task;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

public class SubTaskAdapter extends TypeAdapter<SubTask> {

    @Override
    public void write(JsonWriter jsonWriter, SubTask task) throws IOException {
        jsonWriter.beginObject();
        jsonWriter.name("name").value(task.getTaskName());
        jsonWriter.name("description").value(task.getTaskDescription());
        jsonWriter.name("id").value(task.getTaskId());
        jsonWriter.name("status").value(String.valueOf(task.getTaskStatus()));
        jsonWriter.name("duration").value(task.getDuration().toMinutes());
        jsonWriter.name("startTime").value(task.getStartTime().format(DateFormat.getDateTimeFormat()));
        jsonWriter.name("epic").value(task.getEpicId());
        jsonWriter.endObject();
    }

    @Override
    public SubTask read(JsonReader jsonReader) throws IOException {
        jsonReader.beginObject();
        jsonReader.nextName();
        String name = jsonReader.nextString();
        jsonReader.nextName();
        String description = jsonReader.nextString();
        jsonReader.nextName();
        long id = jsonReader.nextLong();
        jsonReader.nextName();
        State status = State.valueOf(jsonReader.nextString());
        jsonReader.nextName();
        Duration duration = Duration.ofMinutes(jsonReader.nextLong());
        jsonReader.nextName();
        LocalDateTime startTime = LocalDateTime.parse(jsonReader.nextString(),
                DateFormat.getDateTimeFormat());
        jsonReader.nextName();
        long epic = jsonReader.nextLong();
        jsonReader.endObject();
        return new SubTask(name, description, id, status, epic, duration, startTime);
    }
}
