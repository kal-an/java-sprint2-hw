package ru.yandex.practicum.tracker.server;

import ru.yandex.practicum.tracker.manager.Managers;
import ru.yandex.practicum.tracker.manager.TaskManager;

import java.net.URI;

public class HttpTaskServer {

    TaskManager taskManager;

    public HttpTaskServer(URI uri) {
    }

    public void start() {
        taskManager = Managers.getDefault();
    }
}
