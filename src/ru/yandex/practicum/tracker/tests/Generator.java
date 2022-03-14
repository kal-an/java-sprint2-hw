package ru.yandex.practicum.tracker.tests;

import ru.yandex.practicum.tracker.manager.TaskManager;
import ru.yandex.practicum.tracker.tasks.*;

import java.time.Duration;
import java.time.LocalDateTime;

//класс для тестирования
public class Generator {
    TaskManager taskManager;

    public Generator(TaskManager taskManager) {

        this.taskManager = taskManager;
    }

    //старт других методов
    public void start() {
        fillsData();
    }

    //Наполнение данными
    private void fillsData() {

        long taskId1 = TaskId.getNewId();
        taskManager.addTask(new Task("Задача 1", "Собрание в 14:00",
                taskId1,
                State.IN_PROGRESS,
                Duration.ofMinutes(5),
                LocalDateTime.now()));
        long taskId2 = TaskId.getNewId();
        taskManager.addTask(new Task("Задача 2", "Вынести мусор",
                taskId2,
                State.NEW,
                Duration.ofMinutes(5),
                LocalDateTime.now()));

        long epicId1 = TaskId.getNewId();
        taskManager.addTask(new Epic("Эпик 1", "Отпраздновать новый год",
                epicId1,
                State.NEW,
                Duration.ofMinutes(0),
                LocalDateTime.now()));

        long subTaskId1 = TaskId.getNewId();
        taskManager.addTask(new SubTask("Подзадача 1", "Купить подарки",
                subTaskId1,
                State.NEW,
                epicId1,
                Duration.ofMinutes(5),
                LocalDateTime.now()));

        long subTaskId2 = TaskId.getNewId();
        taskManager.addTask(new SubTask("Подзадача 2", "Пригласить друзей",
                subTaskId2,
                State.NEW,
                epicId1,
                Duration.ofMinutes(5),
                LocalDateTime.now()));

        long subTaskId3 = TaskId.getNewId();
        taskManager.addTask(new SubTask("Подзадача 3", "За продуктами",
                subTaskId3,
                State.NEW,
                epicId1,
                Duration.ofMinutes(5),
                LocalDateTime.now()));

        long epicId2 = TaskId.getNewId();
        taskManager.addTask(new Epic("Эпик 2", "Убраться в квартире",
                epicId2,
                State.NEW,
                Duration.ofMinutes(0),
                LocalDateTime.now()));

        System.out.println("    Запрос задачи  " + taskManager.getTask(epicId2));
        printHistory();
        System.out.println("    Запрос задачи  " + taskManager.getTask(epicId1));
        printHistory();
        System.out.println("    Запрос задачи  " + taskManager.getTask(epicId2));
        printHistory();
        System.out.println("    Запрос задачи  " + taskManager.getTask(subTaskId3));
        printHistory();
        System.out.println("    Запрос задачи  " + taskManager.getTask(subTaskId1));
        printHistory();
        System.out.println("    Запрос задачи  " + taskManager.getTask(subTaskId1));
        printHistory();
        System.out.println("    Запрос задачи  " + taskManager.getTask(taskId1));
        printHistory();
        System.out.println("    Запрос задачи  " + taskManager.getTask(subTaskId2));
        printHistory();
        System.out.println("    Запрос задачи  " + taskManager.getTask(epicId2));
        printHistory();
        System.out.println("    Запрос задачи  " + taskManager.getTask(epicId1));
        printHistory();

        System.out.println("    Удаление задачи  " + subTaskId1);
        taskManager.removeTask(subTaskId1);
        printHistory();

        System.out.println("    Удаление задачи  " + epicId1);
        taskManager.removeTask(epicId1);
        printHistory();
    }

    //Распечатать историю недавних задач
    private void printHistory() {
        System.out.println("--- Список недавних задач");
        for (Task task : taskManager.getHistory()) {
            System.out.print("  " + task.getTaskId());
        }
        System.out.println();
    }
}
