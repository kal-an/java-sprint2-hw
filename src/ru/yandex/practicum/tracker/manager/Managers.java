package ru.yandex.practicum.tracker.manager;

//класс для создания менеджера
public class Managers {

    public static HTTPTaskManager getDefault() {

        final String url = "http://localhost:8078";
        return new HTTPTaskManager(url);
    }
}
