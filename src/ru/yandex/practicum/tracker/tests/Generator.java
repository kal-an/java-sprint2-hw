package ru.yandex.practicum.tracker.tests;

import ru.yandex.practicum.tracker.manager.InMemoryTasksManager;
import ru.yandex.practicum.tracker.manager.TaskManager;
import ru.yandex.practicum.tracker.tasks.*;

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

        long epicId1 = TaskId.getNewId();
        taskManager.addTask(new Epic("Эпик 1", "Отпраздновать новый год",
                epicId1,
                State.NEW));
        System.out.println("--- Создана задача " + taskManager.getTask(epicId1));
        printHistory();

        long subTaskId1 = TaskId.getNewId();
        taskManager.addTask(new SubTask("Подзадача 1", "Купить подарки",
                subTaskId1,
                State.NEW,
                epicId1));
        System.out.println("--- Создана подзадача " + taskManager.getTask(subTaskId1));
        printHistory();

        long subTaskId2 = TaskId.getNewId();
        taskManager.addTask(new SubTask("Подзадача 2", "Пригласить друзей",
                subTaskId2,
                State.NEW,
                epicId1));

        long subTaskId3 = TaskId.getNewId();
        taskManager.addTask(new SubTask("Подзадача 3", "За продуктами",
                subTaskId3,
                State.NEW,
                epicId1));

        long epicId2 = TaskId.getNewId();
        taskManager.addTask(new Epic("Эпик 2", "Убраться в квартире",
                epicId2,
                State.NEW));

        long subTaskId4 = TaskId.getNewId();
        taskManager.addTask(new SubTask("Подзадача 1", "Помыть пол",
                subTaskId4,
                State.NEW,
                epicId2));

        long subTaskId5 = TaskId.getNewId();
        taskManager.addTask(new SubTask("Подзадача 2", "Протереть пыль",
                subTaskId5,
                State.NEW,
                epicId2));

        long subTaskId6 = TaskId.getNewId();
        taskManager.addTask(new SubTask("Подзадача 3", "Постирать коврики",
                subTaskId6,
                State.NEW,
                epicId2));

        SubTask task1 = new SubTask("",
                "",
                subTaskId5,
                State.IN_PROGRESS,
                epicId2);

        System.out.println("--- Обновление задачи " + taskManager.getTask(subTaskId5));
        printHistory();
        taskManager.updateTask(task1);
        System.out.println("    Обновлен эпик " +  taskManager.getTask(epicId2));
        printHistory();

        SubTask task2 = new SubTask("",
                "",
                subTaskId3,
                State.IN_PROGRESS,
                epicId1);

        System.out.println("--- Обновление задачи " + taskManager.getTask(subTaskId3));
        taskManager.updateTask(task2);
        printHistory();
        System.out.println("    Обновлен эпик " +  taskManager.getTask(epicId1));
        printHistory();

        long taskId1 = TaskId.getNewId();
        taskManager.addTask(new Task("Задача 1", "Собрание в 14:00",
                taskId1,
                State.IN_PROGRESS));
        long taskId2 = TaskId.getNewId();
        taskManager.addTask(new Task("Задача 2", "Вынести мусор",
                taskId2,
                State.NEW));

        System.out.println("--- Получение задачи Задача 2 " + taskManager.getTask(taskId2));
        printHistory();

        System.out.println("--- Удаление задачи " + subTaskId6);
        taskManager.removeTask(subTaskId6);
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
