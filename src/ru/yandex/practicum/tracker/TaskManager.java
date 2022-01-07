package ru.yandex.practicum.tracker;

import ru.yandex.practicum.tracker.tasks.Task;

import java.util.ArrayList;

//интерфейс менеджера задач
public interface TaskManager<T extends Task> {

    //Получение списка всех задач.
    ArrayList<T> getAllTasks();

    //Получение списка всех эпиков.
    ArrayList<T> getEpics();

    //Получение списка всех подзадач определённого эпика.
    ArrayList<T> getSubTasks(long epicId);

    //Получение задачи любого типа по идентификатору.
    T getTask(long taskId);

    //Добавление новой задачи, эпика и подзадачи.
    void addTask(T newTask);

    //Обновление задачи любого типа по идентификатору.
    void updateTask(T newTask);

    //Удаление ранее всех добавленных задач.
    void removeTask();

    //Удаление задачи по идентификатору.
    void removeTask(long newTaskId);

    //Получение списка просмотренных задач.
    ArrayList<T> getHistory();

    //Обновление истории задач.
    void updateHistory(T viewedTask);
}
