package ru.yandex.practicum.tracker.manager;

//класс для создания менеджера
public class Managers {

    public static InMemoryTasksManager getDefault() {
        return new InMemoryTasksManager();
    }
}
