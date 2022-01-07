package ru.yandex.practicum.tracker;

import ru.yandex.practicum.tracker.tasks.SubTask;
import ru.yandex.practicum.tracker.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;

//класс менеджера задач
public interface TaskManager {
    HashMap<Long, Task> tasks = null; //таблица всех задач

    //Получение списка всех задач.
    public ArrayList<Task> getAllTasks();

    //Получение списка всех эпиков.
    public ArrayList<Task> getEpics();

    //Получение списка всех подзадач определённого эпика.
    public ArrayList<SubTask> getSubTasks(long epicId);

    //Получение задачи любого типа по идентификатору.
    public Task getTask(long taskId);

    //Добавление новой задачи, эпика и подзадачи.
    public void addTask(Task newTask);

    //Обновление задачи любого типа по идентификатору.
    public void updateTask(Task newTask);

    //Удаление ранее всех добавленных задач.
    public void removeTask();

    //Удаление задачи по идентификатору.
    public void removeTask(long newTaskId);
}
