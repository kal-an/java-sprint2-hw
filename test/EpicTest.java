import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tracker.manager.InMemoryTasksManager;
import ru.yandex.practicum.tracker.manager.TaskManager;
import ru.yandex.practicum.tracker.tasks.Epic;
import ru.yandex.practicum.tracker.tasks.State;
import ru.yandex.practicum.tracker.tasks.SubTask;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

// Класс тестирования для эпика
class EpicTest {

    private TaskManager taskManager;
    private Epic epic;
    private SubTask subTask1;
    private SubTask subTask2;

    @BeforeEach
    @DisplayName("Создание задач")
    @Test
    public void createBeforeEach() {
        taskManager = new InMemoryTasksManager();
        epic = new Epic("Эпик 1", "Отпраздновать новый год",
                State.NEW);
        taskManager.addTask(epic);

        Assertions.assertEquals(State.NEW, epic.getTaskStatus());
        Assertions.assertEquals(0, taskManager.getSubTasks(epic.getTaskId()).size());

        subTask1 = new SubTask("Подзадача 1", "Купить подарки",
                State.NEW,
                epic.getTaskId(),
                Duration.ofMinutes(30),
                LocalDateTime.of(2021, 3, 19, 13, 30));
        taskManager.addTask(subTask1);

        subTask2 = new SubTask("Подзадача 2", "Пригласить друзей",
                State.NEW,
                epic.getTaskId(),
                Duration.ofMinutes(10),
                LocalDateTime.of(2021, 4, 15, 15, 30));
        taskManager.addTask(subTask2);

        Assertions.assertEquals(State.NEW, epic.getTaskStatus());
        Assertions.assertEquals(3, taskManager.getAllTasks().size());
        Assertions.assertEquals(2, taskManager.getSubTasks(epic.getTaskId()).size());
        Assertions.assertEquals(List.of(subTask1, subTask2),
                taskManager.getSubTasks(epic.getTaskId()));
    }

    @Test
    @DisplayName("Статус эпика DONE когда все подзадачи DONE")
    public void shouldSetStatusEpicNewWhenAllSubTaskIsDone() {
        subTask1.setTaskStatus(State.DONE);
        taskManager.updateTask(subTask1);
        subTask2.setTaskStatus(State.DONE);
        taskManager.updateTask(subTask2);

        Assertions.assertEquals(State.DONE, epic.getTaskStatus());
    }

    @Test
    @DisplayName("Статус эпика IN_PROGRESS когда подзадачи со статусами NEW и DONE")
    public void shouldSetStatusEpicInProgressWhenOneSubTaskIsDone() {
        subTask1.setTaskStatus(State.IN_PROGRESS);
        taskManager.updateTask(subTask1);
        subTask2.setTaskStatus(State.DONE);
        taskManager.updateTask(subTask2);

        Assertions.assertEquals(State.IN_PROGRESS, epic.getTaskStatus());
    }

    @Test
    @DisplayName("Статус эпика IN_PROGRESS когда подзадачи со статусом IN_PROGRESS")
    public void shouldSetStatusEpicInProgressWhenAllSubTaskIsInProgress() {
        subTask1.setTaskStatus(State.IN_PROGRESS);
        taskManager.updateTask(subTask1);
        subTask2.setTaskStatus(State.IN_PROGRESS);
        taskManager.updateTask(subTask2);

        Assertions.assertEquals(State.IN_PROGRESS, epic.getTaskStatus());
    }

    @Test
    @DisplayName("Статус эпика IN_PROGRESS когда подзадачи со статусом IN_PROGRESS и DONE")
    public void shouldSetStatusEpicInProgressWithInProgressAndDoneSubTasks() {
        subTask1.setTaskStatus(State.IN_PROGRESS);
        taskManager.updateTask(subTask1);
        subTask2.setTaskStatus(State.DONE);
        taskManager.updateTask(subTask2);

        Assertions.assertEquals(State.IN_PROGRESS, epic.getTaskStatus());
    }

    @Test
    @DisplayName("Статус эпика IN_PROGRESS когда подзадачи со статусом NEW и IN_PROGRESS")
    public void shouldSetStatusEpicInProgressWithNewAndDoneSubTasks() {
        subTask1.setTaskStatus(State.NEW);
        taskManager.updateTask(subTask1);
        subTask2.setTaskStatus(State.DONE);
        taskManager.updateTask(subTask2);

        Assertions.assertEquals(State.IN_PROGRESS, epic.getTaskStatus());
    }
}