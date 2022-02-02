package ru.yandex.practicum.tracker.manager.history;

import ru.yandex.practicum.tracker.tasks.Task;

import java.util.List;

public interface HistoryManager {

    void add(Task task);

    void remove(Long id);

    List<Task> getHistory();
}
