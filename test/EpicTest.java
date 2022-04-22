import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tracker.manager.InMemoryTasksManager;
import ru.yandex.practicum.tracker.manager.TaskManager;
import ru.yandex.practicum.tracker.tasks.Epic;
import ru.yandex.practicum.tracker.tasks.State;
import ru.yandex.practicum.tracker.tasks.SubTask;

import java.time.Duration;
import java.time.LocalDateTime;

// Класс тестирования для эпика
class EpicTest {

    TaskManager taskManager;

    @BeforeEach
    public void createBeforeEach() {
        taskManager = new InMemoryTasksManager();
    }

    @Test
    public void shouldSetStatusNewWhenEpicCreated() {
        Epic epic = new Epic("Эпик 1", "Отпраздновать новый год",
                State.NEW);
        taskManager.addTask(epic);
        Assertions.assertEquals(State.NEW, epic.getTaskStatus());
    }

    @Test
    public void shouldSetStatusEpicNewWhenNewSubTasks() {
        Epic epic = new Epic("Эпик 1", "Отпраздновать новый год",
                State.NEW);
        taskManager.addTask(epic);

        SubTask subTask1 = new SubTask("Подзадача 1", "Купить подарки",
                State.NEW,
                epic.getTaskId(),
                Duration.ofMinutes(30),
                LocalDateTime.of(2021, 3, 19, 13, 30));
        taskManager.addTask(subTask1);

        SubTask subTask2 = new SubTask("Подзадача 2", "Пригласить друзей",
                State.NEW,
                epic.getTaskId(),
                Duration.ofMinutes(10),
                LocalDateTime.of(2021, 4, 15, 15, 30));
        taskManager.addTask(subTask2);

        Assertions.assertEquals(State.NEW, epic.getTaskStatus());
    }

    @Test
    public void shouldSetStatusEpicNewWhenAllSubTaskIsDone() {
        Epic epic = new Epic("Эпик 1", "Отпраздновать новый год",
                State.NEW);
        taskManager.addTask(epic);

        SubTask subTask1 = new SubTask("Подзадача 1", "Купить подарки",
                State.NEW,
                epic.getTaskId(),
                Duration.ofMinutes(10),
                LocalDateTime.of(2022, 7, 15, 13, 30));
        taskManager.addTask(subTask1);

        subTask1 = new SubTask("Подзадача 1", "Купить подарки",
                State.DONE,
                epic.getTaskId(),
                Duration.ofMinutes(20),
                LocalDateTime.of(2022, 8, 15, 16, 30));
        taskManager.updateTask(subTask1);

        SubTask subTask2 = new SubTask("Подзадача 2", "Пригласить друзей",
                State.NEW,
                epic.getTaskId(),
                Duration.ofMinutes(10),
                LocalDateTime.of(2022, 9, 15, 15, 30));
        taskManager.addTask(subTask2);
        subTask2 = new SubTask("Подзадача 2", "Пригласить друзей",
                State.DONE,
                epic.getTaskId(),
                Duration.ofMinutes(10),
                LocalDateTime.of(2022, 10, 15, 15, 30));
        taskManager.updateTask(subTask2);

        Assertions.assertEquals(State.DONE, epic.getTaskStatus());
    }

    @Test
    public void shouldSetStatusEpicInProgressWhenOneSubTaskIsDone() {
        Epic epic = new Epic("Эпик 1", "Отпраздновать новый год",
                State.NEW);
        taskManager.addTask(epic);

        SubTask subTask1 = new SubTask("Подзадача 1", "Купить подарки",
                State.NEW,
                epic.getTaskId(),
                Duration.ofMinutes(30),
                LocalDateTime.of(2022, 6, 15, 13, 30));
        taskManager.addTask(subTask1);

        SubTask subTask2 = new SubTask("Подзадача 2", "Пригласить друзей",
                State.NEW,
                epic.getTaskId(),
                Duration.ofMinutes(10),
                LocalDateTime.of(2022, 12, 15, 15, 30));
        taskManager.addTask(subTask2);

        subTask2 = new SubTask("Подзадача 2", "Пригласить друзей",
                State.DONE,
                epic.getTaskId(),
                Duration.ofMinutes(10),
                LocalDateTime.of(2022, 3, 15, 15, 30));
        taskManager.updateTask(subTask2);

        Assertions.assertEquals(State.IN_PROGRESS, epic.getTaskStatus());
    }

    @Test
    public void shouldSetStatusEpicInProgressWhenAllSubTaskIsInProgress() {
        Epic epic = new Epic("Эпик 1", "Отпраздновать новый год",
                State.NEW);
        taskManager.addTask(epic);

        SubTask subTask1 = new SubTask("Подзадача 1", "Купить подарки",
                State.NEW,
                epic.getTaskId(),
                Duration.ofMinutes(30),
                LocalDateTime.of(2022, 4, 13, 13, 30));
        taskManager.addTask(subTask1);

        subTask1 = new SubTask("Подзадача 1", "Купить подарки",
                State.IN_PROGRESS,
                epic.getTaskId(),
                Duration.ofMinutes(30),
                LocalDateTime.of(2022, 3, 15, 13, 30));
        taskManager.updateTask(subTask1);

        SubTask subTask2 = new SubTask("Подзадача 2", "Пригласить друзей",
                State.NEW,
                epic.getTaskId(),
                Duration.ofMinutes(10),
                LocalDateTime.of(2022, 2, 20, 15, 30));
        taskManager.addTask(subTask2);

        subTask2 = new SubTask("Подзадача 2", "Пригласить друзей",
                State.IN_PROGRESS,
                epic.getTaskId(),
                Duration.ofMinutes(10),
                LocalDateTime.of(2022, 3, 15, 15, 30));
        taskManager.updateTask(subTask2);

        Assertions.assertEquals(State.IN_PROGRESS, epic.getTaskStatus());
    }
}