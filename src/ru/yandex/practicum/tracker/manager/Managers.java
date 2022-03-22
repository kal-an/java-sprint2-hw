package ru.yandex.practicum.tracker.manager;

import ru.yandex.practicum.tracker.manager.history.InMemoryHistoryManager;

//класс для создания менеджера
public class Managers {

    public static InMemoryTasksManager getDefault() {

        return new InMemoryTasksManager();
    }
}
