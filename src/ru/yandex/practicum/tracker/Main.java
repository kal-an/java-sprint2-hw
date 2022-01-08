package ru.yandex.practicum.tracker;

import ru.yandex.practicum.tracker.manager.InMemoryTasksManager;
import ru.yandex.practicum.tracker.manager.Managers;
import ru.yandex.practicum.tracker.tests.Generator;

public class Main {
    public static void main(String[] args) {

        InMemoryTasksManager taskManager = Managers.getDefault();
        Generator generator = new Generator(taskManager);
        generator.start();
    }
}
