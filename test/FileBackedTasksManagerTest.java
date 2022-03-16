import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tracker.manager.FileBackedTasksManager;
import ru.yandex.practicum.tracker.manager.TaskManager;
import ru.yandex.practicum.tracker.tasks.*;

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
        long taskId1 = TaskId.getNewId();
        fileBackedTasksManager.addTask(new Task("Задача 1", "Собрание в 14:00",
                taskId1,
                State.NEW,
                Duration.ofMinutes(5),
                LocalDateTime.of(2022, 3, 15, 10, 30)));
        long taskId2 = TaskId.getNewId();
        fileBackedTasksManager.addTask(new Task("Задача 2", "Вынести мусор",
                taskId2,
                State.NEW,
                Duration.ofMinutes(5),
                LocalDateTime.of(2022, 3, 15, 12, 30)));

        long epicId1 = TaskId.getNewId();
        fileBackedTasksManager.addTask(new Epic("Эпик 1", "Отпраздновать новый год",
                State.NEW,
                epicId1));

        long subTaskId1 = TaskId.getNewId();
        fileBackedTasksManager.addTask(new SubTask("Подзадача 1", "Купить подарки",
                subTaskId1,
                State.NEW,
                epicId1,
                Duration.ofMinutes(5),
                LocalDateTime.of(2022, 3, 15, 13, 30)));

        long subTaskId2 = TaskId.getNewId();
        fileBackedTasksManager.addTask(new SubTask("Подзадача 2", "Пригласить друзей",
                subTaskId2,
                State.NEW,
                epicId1,
                Duration.ofMinutes(5),
                LocalDateTime.of(2022, 3, 15, 15, 30)));

        fileBackedTasksManager.updateTask(new SubTask("Подзадача 2", "Пригласить друзей",
                subTaskId2,
                State.IN_PROGRESS,
                epicId1,
                Duration.ofMinutes(5),
                LocalDateTime.of(2022, 3, 15, 15, 30)));

        long subTaskId3 = TaskId.getNewId();
        fileBackedTasksManager.addTask(new SubTask("Подзадача 3", "За продуктами",
                subTaskId3,
                State.NEW,
                epicId1,
                Duration.ofMinutes(5),
                LocalDateTime.of(2022, 3, 14, 2, 30)));

        long epicId2 = TaskId.getNewId();
        fileBackedTasksManager.addTask(new Epic("Эпик 2", "Убраться в квартире",
                State.NEW,
                epicId2));

        fileBackedTasksManager.getTask(taskId1);
        fileBackedTasksManager.getTask(subTaskId2);
        fileBackedTasksManager.getTask(taskId2);
        fileBackedTasksManager.getTask(epicId2);

    }
}