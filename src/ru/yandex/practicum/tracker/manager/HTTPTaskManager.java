package ru.yandex.practicum.tracker.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import ru.yandex.practicum.tracker.client.KVTaskClient;
import ru.yandex.practicum.tracker.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class HTTPTaskManager extends FileBackedTasksManager {

    private final KVTaskClient kvTaskClient;

    public HTTPTaskManager(String url) {
        kvTaskClient = new KVTaskClient(url);
    }

    private void saveToStorage() {
        Gson gson = new GsonBuilder().create();
        String tasks = gson.toJson(getAllTasks());
        String history = gson.toJson(getHistory());
        kvTaskClient.put("tasks", tasks);
        kvTaskClient.put("history", history);
    }

    private void loadFromStorage() {
        Gson gson = new GsonBuilder().create();
        List<Task> tasks = gson.fromJson(kvTaskClient.load("tasks"),
                new TypeToken<ArrayList<Task>>() {
                }.getType());
        List<Task> history = gson.fromJson(kvTaskClient.load("history"),
                new TypeToken<ArrayList<Task>>() {
                }.getType());
    }
}
