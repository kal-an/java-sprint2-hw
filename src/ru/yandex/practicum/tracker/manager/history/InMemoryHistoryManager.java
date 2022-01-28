package ru.yandex.practicum.tracker.manager.history;

import ru.yandex.practicum.tracker.tasks.Task;

import java.util.List;

//менеджер управления историей
public class InMemoryHistoryManager implements HistoryManager {

    //добавить новый просмотр задачи
    @Override
    public void add(Task task) {

    }

    //удаление просмотра из истории
    @Override
    public void remove(int id) {

    }

    //получить историю последних просмотров
    @Override
    public List<Task> getHistory() {
        return null;
    }
}
