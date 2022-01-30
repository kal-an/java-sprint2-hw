package ru.yandex.practicum.tracker.manager;

import ru.yandex.practicum.tracker.tasks.SubTask;
import ru.yandex.practicum.tracker.tasks.Task;

import java.util.ArrayList;

//интерфейс менеджера задач
public interface TaskManager {

    //Получение списка всех задач.
    ArrayList<Task> getAllTasks();

    //Получение списка всех эпиков.
    ArrayList<Task> getEpics();

    //Получение списка всех подзадач определённого эпика.
    ArrayList<SubTask> getSubTasks(long epicId);

    //Получение задачи любого типа по идентификатору.
    Task getTask(long taskId);

    //Добавление новой задачи, эпика и подзадачи.
    void addTask(Task newTask);

    //Обновление задачи любого типа по идентификатору.
    void updateTask(Task newTask);

    //Удаление ранее всех добавленных задач.
    void removeTask();

    //Удаление задачи по идентификатору.
    void removeTask(long newTaskId);

}
