package ru.yandex.practicum.tracker.manager;

import ru.yandex.practicum.tracker.manager.history.HistoryManager;
import ru.yandex.practicum.tracker.tasks.SubTask;
import ru.yandex.practicum.tracker.tasks.Task;

import java.util.ArrayList;
import java.util.List;

//класс менеджера для автосохранения в файл
public class FileBackedTasksManager extends InMemoryTasksManager implements TaskManager {

    public FileBackedTasksManager(HistoryManager historyManager) {
        super(historyManager);
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        return null;
    }

    @Override
    public ArrayList<Task> getEpics() {
        return null;
    }

    @Override
    public ArrayList<SubTask> getSubTasks(long epicId) {
        return null;
    }

    @Override
    public Task getTask(long taskId) {
        return null;
    }

    @Override
    public void addTask(Task newTask) {

    }

    @Override
    public void updateTask(Task newTask) {

    }

    @Override
    public void removeTask() {

    }

    @Override
    public void removeTask(long newTaskId) {

    }

    @Override
    public List<Task> getHistory() {
        return null;
    }
}
