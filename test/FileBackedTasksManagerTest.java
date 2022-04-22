import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tracker.manager.FileBackedTasksManager;
import ru.yandex.practicum.tracker.manager.TaskManager;
import ru.yandex.practicum.tracker.tasks.Epic;
import ru.yandex.practicum.tracker.tasks.State;
import ru.yandex.practicum.tracker.tasks.SubTask;
import ru.yandex.practicum.tracker.tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

class FileBackedTasksManagerTest extends TaskManagerTest {

    static TaskManager fileBackedTasksManager;

    @BeforeEach
    public void createBeforeEach() {
        fileBackedTasksManager = FileBackedTasksManager.start();
    }

    @Test
    public void shouldReturnTrueWhenTaskListIsEmpty() {
        fileBackedTasksManager.removeTask();
        Assertions.assertTrue(fileBackedTasksManager.getAllTasks().isEmpty());
    }

    @Test
    public void shouldReturnTrueWhenEpicWithoutSubTask() {
        List<SubTask> taskList = fileBackedTasksManager.getSubTasks(7);
        Assertions.assertTrue(taskList.isEmpty());
    }

    @Test
    public void shouldReturnTrueWhenHistoryIsEmpty() {
        fileBackedTasksManager.removeTask();
        Assertions.assertTrue(fileBackedTasksManager.getHistory().isEmpty());
    }

    @AfterAll
    public static void createAfterAll() {
        Task task1 = new Task("Задача 1", "Собрание в 14:00",
                State.NEW,
                Duration.ofMinutes(5),
                LocalDateTime.of(2022, 3, 15, 10, 30));
        fileBackedTasksManager.addTask(task1);

        Task task2 = new Task("Задача 2", "Вынести мусор",
                State.NEW,
                Duration.ofMinutes(5),
                LocalDateTime.of(2022, 3, 15, 12, 30));
        fileBackedTasksManager.addTask(task2);

        Epic epic1 = new Epic("Эпик 1", "Отпраздновать новый год",
                State.NEW);
        fileBackedTasksManager.addTask(epic1);

        fileBackedTasksManager.addTask(new SubTask("Подзадача 1",
                "Купить подарки",
                State.NEW,
                epic1.getTaskId(),
                Duration.ofMinutes(5),
                LocalDateTime.of(2022, 3, 15, 13, 30)));

        fileBackedTasksManager.addTask(new SubTask("Подзадача 2",
                "Пригласить друзей",
                State.NEW,
                epic1.getTaskId(),
                Duration.ofMinutes(5),
                LocalDateTime.of(2022, 3, 15, 15, 30)));

        SubTask subTask2 = new SubTask("Подзадача 2", "Пригласить друзей",
                State.IN_PROGRESS,
                epic1.getTaskId(),
                Duration.ofMinutes(5),
                LocalDateTime.of(2022, 3, 15, 15, 30));
        fileBackedTasksManager.updateTask(subTask2);

        fileBackedTasksManager.addTask(new SubTask("Подзадача 3",
                "За продуктами",
                State.NEW,
                epic1.getTaskId(),
                Duration.ofMinutes(5),
                LocalDateTime.of(2022, 3, 14, 2, 30)));

        Epic epic2 = new Epic("Эпик 2", "Убраться в квартире",
                State.NEW);
        fileBackedTasksManager.addTask(epic2);

        fileBackedTasksManager.getTask(task1.getTaskId());
        fileBackedTasksManager.getTask(subTask2.getTaskId());
        fileBackedTasksManager.getTask(task2.getTaskId());
        fileBackedTasksManager.getTask(epic1.getTaskId());

    }
}