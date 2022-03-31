package ru.yandex.practicum.tracker.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import ru.yandex.practicum.tracker.client.KVTaskClient;
import ru.yandex.practicum.tracker.tasks.Task;
import ru.yandex.practicum.tracker.utils.DurationAdapter;
import ru.yandex.practicum.tracker.utils.LocalDateTimeAdapter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HTTPTaskManager extends FileBackedTasksManager {

    private final KVTaskClient kvTaskClient;
    private final Gson gson;

    public HTTPTaskManager(String url) {
        kvTaskClient = new KVTaskClient(url);
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
        load();
    }

    //сохранить в хранилище
    @Override
    protected void save() {
        String tasks = gson.toJson(getAllTasks());
        String history = gson.toJson(getHistory());
        kvTaskClient.put("tasks", tasks);
        kvTaskClient.put("history", history);
    }

    //загрузить из хранилища
    private void load() {
        List<Task> tasks = gson.fromJson(kvTaskClient.load("tasks"), //все задачи
                new TypeToken<ArrayList<Task>>() {
                }.getType());
        if (tasks != null) {
            tasks.forEach(this::addTask);
        }

        List<Task> history =  gson.fromJson(kvTaskClient.load("history"), //история просмотров
                new TypeToken<ArrayList<Task>>() {
                }.getType());
        if (history != null) {
            history.forEach(this::addTask);
        }
    }
}
