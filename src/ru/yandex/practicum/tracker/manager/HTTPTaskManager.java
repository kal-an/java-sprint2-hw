package ru.yandex.practicum.tracker.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import ru.yandex.practicum.tracker.client.KVTaskClient;
import ru.yandex.practicum.tracker.tasks.Epic;
import ru.yandex.practicum.tracker.tasks.SubTask;
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
        String history = gson.toJson(getHistory());
        List<Task> tasksList = new ArrayList<>();
        List<Epic> epicsList = new ArrayList<>();
        List<SubTask> subTasksList = new ArrayList<>();

        for (Task task : getAllTasks()) {
            if (task instanceof Epic) {
                epicsList.add((Epic) task);
            } else if (task instanceof SubTask) {
                subTasksList.add((SubTask) task);
            } else {
                tasksList.add(task);
            }
        }
        String tasks = gson.toJson(tasksList);
        String epics = gson.toJson(epicsList);
        String subTasks = gson.toJson(subTasksList);

        kvTaskClient.put("tasks", tasks);
        kvTaskClient.put("epics", epics);
        kvTaskClient.put("subtasks", subTasks);
        kvTaskClient.put("history", history);
    }

    //загрузить из хранилища
    private void load() {
        List<Task> tasks = gson.fromJson(kvTaskClient.load("tasks"), //задачи
                new TypeToken<ArrayList<Task>>() {
                }.getType());
        if (tasks != null) {
            tasks.forEach(this::addTask);
        }

        List<Epic> epics = gson.fromJson(kvTaskClient.load("epics"), // эпики
                new TypeToken<ArrayList<Epic>>() {
                }.getType());
        if (epics != null) {
            epics.forEach(this::addTask);
        }

        List<SubTask> subTasks = gson.fromJson(kvTaskClient.load("subtasks"), // подзадачи
                new TypeToken<ArrayList<SubTask>>() {
                }.getType());
        if (subTasks != null) {
            subTasks.forEach(this::addTask);
        }

        List<Task> history =  gson.fromJson(kvTaskClient.load("history"), //история просмотров
                new TypeToken<ArrayList<Task>>() {
                }.getType());
        if (history != null) {
            history.forEach(this::addTask);
        }
    }
}
