import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tracker.manager.Managers;
import ru.yandex.practicum.tracker.manager.TaskManager;
import ru.yandex.practicum.tracker.tasks.Epic;
import ru.yandex.practicum.tracker.tasks.State;
import ru.yandex.practicum.tracker.tasks.SubTask;
import ru.yandex.practicum.tracker.tasks.TaskId;

import java.time.Duration;
import java.time.LocalDateTime;

//класс тестирования для эпика
class EpicTest {

    TaskManager taskManager;

    @BeforeEach
    public void createBeforeEach() {
        taskManager = Managers.getDefault();
    }

    @Test
    public void shouldSetStatusNewWhenEpicCreated() {
        long epicId = TaskId.getNewId();
        Epic epic = new Epic("Эпик 1", "Отпраздновать новый год",
                State.NEW, epicId);
        taskManager.addTask(epic);
        Assertions.assertEquals(State.NEW, epic.getTaskStatus());
    }

    @Test
    public void shouldSetStatusEpicNewWhenNewSubTasks() {
        long epicId = TaskId.getNewId();
        Epic epic = new Epic("Эпик 1", "Отпраздновать новый год",
                State.NEW, epicId);
        taskManager.addTask(epic);

        SubTask subTask1 = new SubTask("Подзадача 1", "Купить подарки",
                State.NEW,
                epicId,
                Duration.ofMinutes(30),
                LocalDateTime.of(2021, 3, 19, 13, 30));
        taskManager.addTask(subTask1);

        SubTask subTask2 = new SubTask("Подзадача 2", "Пригласить друзей",
                State.NEW,
                epicId,
                Duration.ofMinutes(10),
                LocalDateTime.of(2021, 3, 15, 15, 30));
        taskManager.addTask(subTask2);

        Assertions.assertEquals(State.NEW, epic.getTaskStatus());
    }

    @Test
    public void shouldSetStatusEpicNewWhenAllSubTaskIsDone() {
        long epicId = TaskId.getNewId();
        Epic epic = new Epic("Эпик 1", "Отпраздновать новый год",
                State.NEW, epicId);
        taskManager.addTask(epic);

        long subTaskId1 = TaskId.getNewId();
        SubTask subTask1 = new SubTask("Подзадача 1", "Купить подарки",
                subTaskId1,
                State.NEW,
                epicId,
                Duration.ofMinutes(10),
                LocalDateTime.of(2022, 7, 15, 13, 30));
        taskManager.addTask(subTask1);
        subTask1 = new SubTask("Подзадача 1", "Купить подарки",
                subTaskId1,
                State.DONE,
                epicId,
                Duration.ofMinutes(20),
                LocalDateTime.of(2022, 8, 15, 16, 30));
        taskManager.updateTask(subTask1);

        long subTaskId2 = TaskId.getNewId();
        SubTask subTask2 = new SubTask("Подзадача 2", "Пригласить друзей",
                subTaskId2,
                State.NEW,
                epicId,
                Duration.ofMinutes(10),
                LocalDateTime.of(2022, 9, 15, 15, 30));
        taskManager.addTask(subTask2);
        subTask2 = new SubTask("Подзадача 2", "Пригласить друзей",
                subTaskId2,
                State.DONE,
                epicId,
                Duration.ofMinutes(10),
                LocalDateTime.of(2022, 10, 15, 15, 30));
        taskManager.updateTask(subTask2);

        Assertions.assertEquals(State.DONE, epic.getTaskStatus());
    }

    @Test
    public void shouldSetStatusEpicInProgressWhenOneSubTaskIsDone() {
        long epicId = TaskId.getNewId();
        Epic epic = new Epic("Эпик 1", "Отпраздновать новый год",
                State.NEW, epicId);
        taskManager.addTask(epic);

        long subTaskId1 = TaskId.getNewId();
        SubTask subTask1 = new SubTask("Подзадача 1", "Купить подарки",
                subTaskId1,
                State.NEW,
                epicId,
                Duration.ofMinutes(30),
                LocalDateTime.of(2022, 6, 15, 13, 30));
        taskManager.addTask(subTask1);

        long subTaskId2 = TaskId.getNewId();
        SubTask subTask2 = new SubTask("Подзадача 2", "Пригласить друзей",
                subTaskId2,
                State.NEW,
                epicId,
                Duration.ofMinutes(10),
                LocalDateTime.of(2022, 12, 15, 15, 30));
        taskManager.addTask(subTask2);
        subTask2 = new SubTask("Подзадача 2", "Пригласить друзей",
                subTaskId2,
                State.DONE,
                epicId,
                Duration.ofMinutes(10),
                LocalDateTime.of(2022, 3, 15, 15, 30));
        taskManager.updateTask(subTask2);

        Assertions.assertEquals(State.IN_PROGRESS, epic.getTaskStatus());
    }

    @Test
    public void shouldSetStatusEpicInProgressWhenAllSubTaskIsInProgress() {
        long epicId = TaskId.getNewId();
        Epic epic = new Epic("Эпик 1", "Отпраздновать новый год",
                State.NEW, epicId);
        taskManager.addTask(epic);

        long subTaskId1 = TaskId.getNewId();
        SubTask subTask1 = new SubTask("Подзадача 1", "Купить подарки",
                subTaskId1,
                State.NEW,
                epicId,
                Duration.ofMinutes(30),
                LocalDateTime.of(2022, 4, 15, 13, 30));
        taskManager.addTask(subTask1);
        subTask1 = new SubTask("Подзадача 1", "Купить подарки",
                subTaskId1,
                State.IN_PROGRESS,
                epicId,
                Duration.ofMinutes(30),
                LocalDateTime.of(2022, 3, 15, 13, 30));
        taskManager.updateTask(subTask1);

        long subTaskId2 = TaskId.getNewId();
        SubTask subTask2 = new SubTask("Подзадача 2", "Пригласить друзей",
                subTaskId2,
                State.NEW,
                epicId,
                Duration.ofMinutes(10),
                LocalDateTime.of(2022, 2, 15, 15, 30));
        taskManager.addTask(subTask2);
        subTask2 = new SubTask("Подзадача 2", "Пригласить друзей",
                subTaskId2,
                State.IN_PROGRESS,
                epicId,
                Duration.ofMinutes(10),
                LocalDateTime.of(2022, 3, 15, 15, 30));
        taskManager.updateTask(subTask2);

        Assertions.assertEquals(State.IN_PROGRESS, epic.getTaskStatus());
    }
}