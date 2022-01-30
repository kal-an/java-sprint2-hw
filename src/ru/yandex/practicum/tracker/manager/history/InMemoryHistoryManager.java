package ru.yandex.practicum.tracker.manager.history;

import ru.yandex.practicum.tracker.tasks.Task;

import java.util.List;

//менеджер управления историей
public class InMemoryHistoryManager implements HistoryManager {

    //список задач истории просмотра
    HistoryLinkedList<Task> historyTaskList = new HistoryLinkedList<>();

    //класс для собственного списка задач истории
    class HistoryLinkedList<T> {
        private Node<T> head;
        private Node<T> tail;
        private int size = 0;

        //добавить задачу в историю просмотров
        public void addLast(T element) {
            final Node<T> oldTail = tail; //хвост
            final Node<T> newNode = new Node<>(oldTail, element, null); //новый узел
            tail = newNode;
            if (oldTail == null) { //если старого хвоста нет
                head = newNode; //головным узлом становится новый узел
            } else { //иначе следующим узлом становится новый узел
                oldTail.next = newNode;
            }
            size++; //увеличить размер списка
        }

        //получить размер списка
        public int size() {
            return this.size;
        }
    }

    //добавить новый просмотр задачи
    @Override
    public void add(Task task) {

    }

    //удалить просмотр задачи из истории
    @Override
    public void remove(int id) {

    }

    //получить историю последних просмотров
    @Override
    public List<Task> getHistory() {
        return null;
    }
}
