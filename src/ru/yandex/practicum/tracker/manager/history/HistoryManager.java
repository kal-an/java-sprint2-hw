package ru.yandex.practicum.tracker.manager.history;

import ru.yandex.practicum.tracker.tasks.Task;

import java.util.List;

public interface HistoryManager {

    //добавить новый просмотр задачи
    void add(Task task);

    //удаление просмотра из истории
    void remove(int id);

    //получить историю последних просмотров
    List<Task> getHistory();
}
