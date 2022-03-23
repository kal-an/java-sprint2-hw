package ru.yandex.practicum.tracker.manager;

//класс для создания менеджера
public class Managers {

    public static FileBackedTasksManager getDefault() {

        return new FileBackedTasksManager();
    }
}
