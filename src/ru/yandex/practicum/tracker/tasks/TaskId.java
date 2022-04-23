package ru.yandex.practicum.tracker.tasks;

//класс для создания идентификаторов
public class TaskId {
    private static Long id;

    //получить новый ID
    public static Long getNewId() {
        return ++id;
    }
}
