package ru.yandex.practicum.tracker.tasks;

//класс для создания идентификаторов
public class TaskId {
    private static long id;

    //получить новый ID
    public static long getNewId() {
        return ++id;
    }
}
