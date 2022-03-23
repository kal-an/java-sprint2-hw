package ru.yandex.practicum.tracker.manager.history;

import ru.yandex.practicum.tracker.tasks.Task;

import java.util.*;

//менеджер управления историей
public class InMemoryHistoryManager implements HistoryManager {
    //список задач истории просмотра
    private final HistoryLinkedList<Task> historyTaskList;
    private final Map<Long, Node<Task>> historyTaskHashMap;

    public InMemoryHistoryManager() {
        historyTaskList = new HistoryLinkedList<>();
        historyTaskHashMap = new HashMap<>();
    }

    //класс для собственного списка задач истории
    class HistoryLinkedList<T> {
        private Node<T> head;
        private Node<T> tail;

        //добавить задачу в конец
        private Node<T> addLast(T element) {
            final Node<T> oldTail = tail; //предыдущий хвост
            final Node<T> newNode = new Node<>(oldTail, element, null); //новый узел
            tail = newNode;
            if (oldTail == null) { //если старого хвоста нет
                head = newNode; //головным узлом становится новый узел
            } else { //иначе предыдущий хвост ссылается на новый узел
                oldTail.next = newNode;
            }
            return newNode;
        }

        //удалить узел
        private void removeNode(Node<T> node) {
            final Node<T> next = node.next;
            final Node<T> prev = node.prev;

            if (prev == null) { //если предыдущий узел null
                head = next;
            } else {
                prev.next = next;
                node.prev = null;
            }

            if (next == null) { //если следующий узел null
                tail = prev;
            } else {
                next.prev = prev;
                node.next = null;
            }
        }

        //получить список задач
        private ArrayList<Task> getTasks() {
            ArrayList<Task> taskList = new ArrayList<>();
            Node<T> node = head; //начинаем с головы
            while (node != null) { //пока есть связь со следующим элементом
                taskList.add((Task) node.data); //добавить в список
                node = node.next; //ссылка на следующую ноду
            }
            return taskList;
        }

        private void removeAllNodes() {
            for (Node<T> x = head; x != null; ) {
                Node<T> next = x.next;
                x.data = null;
                x.next = null;
                x.prev = null;
                x = next;
            }
            head = tail = null;
        }
    }

    //добавить новый просмотр задачи
    @Override
    public void add(Task task) {
        Node<Task> node = historyTaskList.addLast(task); //добавить задачу в связный список
        //если такой id задачи есть в хеш-таблице
        if (historyTaskHashMap.containsKey(task.getTaskId())) {
            remove(task.getTaskId()); //удалить из списка задачу
        }
        historyTaskHashMap.put(task.getTaskId(), node); //добавить задачу в хеш-таблицу
    }

    //удалить просмотр задачи из списка
    @Override
    public void remove(Long id) {
        Node<Task> node = historyTaskHashMap.get(id); //получить ноду по id из хеш-таблицы
        if (node != null) {
            historyTaskList.removeNode(node); //удалить ноду из связного списка
        }
    }

    //Получение списка просмотренных задач
    @Override
    public List<Task> getHistory() {
        return historyTaskList.getTasks(); //вернуть список задач из связного списка
    }

    @Override
    public void clear() {
        historyTaskList.removeAllNodes();
    }
}
