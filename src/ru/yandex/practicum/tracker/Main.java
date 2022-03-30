package ru.yandex.practicum.tracker;

import ru.yandex.practicum.tracker.manager.Managers;
import ru.yandex.practicum.tracker.manager.TaskManager;
import ru.yandex.practicum.tracker.server.HttpTaskServer;
import ru.yandex.practicum.tracker.server.KVServer;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {

        TaskManager taskManager = Managers.getDefault();
        new HttpTaskServer(8080, taskManager);
        new KVServer().start();
    }
}
