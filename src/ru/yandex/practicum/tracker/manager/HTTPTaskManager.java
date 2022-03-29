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

    public HTTPTaskManager(String url) {
        kvTaskClient = new KVTaskClient(url);
        load();
    }

    @Override
    public Task getTask(long taskId) {
        Task task = super.getTask(taskId);
        save();
        return task;
    }

    @Override
    public void addTask(Task newTask) {
        super.addTask(newTask);
        save();
    }

    @Override
    public void updateTask(Task newTask) {
        super.updateTask(newTask);
        save();
    }

    @Override
    public void removeTask() {
        super.removeTask();
        save();
    }

    @Override
    public void removeTask(long newTaskId) {
        super.removeTask(newTaskId);
        save();
    }

    //сохранить в хранилище
    @Override
    protected void save() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
        String tasks = gson.toJson(getAllTasks());
        String history = gson.toJson(getHistory());
        kvTaskClient.put("tasks", tasks);
        kvTaskClient.put("history", history);
    }

    //загрузить из хранилища
    private void load() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
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
