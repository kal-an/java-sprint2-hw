import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tracker.exceptions.ManagerTaskException;
import ru.yandex.practicum.tracker.manager.InMemoryTasksManager;
import ru.yandex.practicum.tracker.manager.TaskManager;
import ru.yandex.practicum.tracker.tasks.Epic;
import ru.yandex.practicum.tracker.tasks.State;
import ru.yandex.practicum.tracker.tasks.SubTask;
import ru.yandex.practicum.tracker.tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public abstract class TaskManagerTest {

    TaskManager taskManager = new InMemoryTasksManager();

    @Test
    public void shouldReturnTaskList() {
        taskManager.removeTask();
        Task task1 = new Task("Задача 1", "Собрание в 14:00",
                State.NEW,
                Duration.ofMinutes(5),
                LocalDateTime.of(2022, 3, 15, 10, 30));
        taskManager.addTask(task1);

        Task task2 = new Task("Задача 2", "Вынести мусор",
                State.NEW,
                Duration.ofMinutes(5),
                LocalDateTime.of(2022, 3, 15, 12, 40));
        taskManager.addTask(task2);

        Assertions.assertIterableEquals(List.of(task1, task2), taskManager.getAllTasks());
    }

    @Test
    public void shouldReturnEmptyTaskList() {
        taskManager.removeTask();
        Assertions.assertIterableEquals(Collections.emptyList(), taskManager.getAllTasks());
    }

    @Test
    public void shouldCheckIfEpicExistInSubTask() {
        Epic epic1 = new Epic("Эпик 1", "Отпраздновать новый год",
                State.NEW);
        taskManager.addTask(epic1);

        SubTask subTask1 = new SubTask("Подзадача 1", "Купить подарки",
                State.NEW,
                epic1.getTaskId(),
                Duration.ofMinutes(5),
                LocalDateTime.of(2022, 3, 15, 13, 30));
        taskManager.addTask(subTask1);

        Assertions.assertEquals(epic1.getTaskId(), subTask1.getEpicId());
    }

    @Test
    public void shouldCheckEpicStatusDoneWhenSubTaskDone() {
        Epic epic1 = new Epic("Эпик 1", "Отпраздновать новый год",
                State.NEW);
        taskManager.addTask(epic1);

        SubTask subTask1 = new SubTask("Подзадача 1", "Купить подарки",
                State.NEW,
                epic1.getTaskId(),
                Duration.ofMinutes(5),
                LocalDateTime.of(2022, 3, 15, 13, 30));
        taskManager.addTask(subTask1);
        subTask1 = new SubTask("Подзадача 1", "Купить подарки",
                State.DONE,
                epic1.getTaskId(),
                Duration.ofMinutes(5),
                LocalDateTime.of(2022, 3, 15, 13, 30));
        taskManager.updateTask(subTask1);

        Assertions.assertEquals(State.DONE, taskManager.getTask(epic1.getTaskId()).getTaskStatus());
    }

    @Test
    public void shouldCheckEpicStatusInProgressWhenSubTaskInProgress() {
        Epic epic = new Epic("Эпик 1", "Отпраздновать новый год",
                State.NEW);
        taskManager.addTask(epic);

        SubTask subTask1 = new SubTask("Подзадача 1", "Купить подарки",
                State.NEW,
                epic.getTaskId(),
                Duration.ofMinutes(5),
                LocalDateTime.of(2022, 3, 15, 13, 30));
        taskManager.addTask(subTask1);
        subTask1 = new SubTask("Подзадача 1", "Купить подарки",
                State.IN_PROGRESS,
                epic.getTaskId(),
                Duration.ofMinutes(5),
                LocalDateTime.of(2022, 3, 15, 13, 30));
        taskManager.updateTask(subTask1);

        Assertions.assertEquals(State.IN_PROGRESS,
                taskManager.getTask(epic.getTaskId()).getTaskStatus());
    }

    @Test
    public void shouldReturnNullWhenTaskIdIsNotExist() {
        ManagerTaskException ex = Assertions.assertThrows(
                ManagerTaskException.class,
                () -> taskManager.getTask(56436));
        Assertions.assertEquals("Задача не найдена", ex.getMessage());
    }

    @Test
    public void shouldThrowsWhenRemoveTaskIsNotExist() {
        ManagerTaskException ex = Assertions.assertThrows(
                ManagerTaskException.class,
                () -> taskManager.removeTask(121452)
        );
        Assertions.assertEquals("Задачи с таким ID не найдено", ex.getMessage());
    }

    @Test
    public void shouldReturnEpicList() {
        taskManager.removeTask();
        Epic epic1 = new Epic("Эпик 1", "Отпраздновать новый год",
                State.NEW);
        taskManager.addTask(epic1);

        Assertions.assertIterableEquals(List.of(epic1), taskManager.getEpics());
    }

    @Test
    public void shouldReturnSubTaskListFromEpic() {
        Epic epic = new Epic("Эпик 1", "Отпраздновать новый год",
                State.NEW);
        taskManager.addTask(epic);

        SubTask subTask1 = new SubTask("Подзадача 1", "Купить подарки",
                State.NEW,
                epic.getTaskId(),
                Duration.ofMinutes(5),
                LocalDateTime.of(2022, 3, 15, 13, 30));
        taskManager.addTask(subTask1);

        SubTask subTask2 = new SubTask("Подзадача 2", "Пригласить друзей",
                State.NEW,
                epic.getTaskId(),
                Duration.ofMinutes(5),
                LocalDateTime.now());
        taskManager.addTask(subTask2);

        Assertions.assertIterableEquals(List.of(subTask1.getTaskId(), subTask2.getTaskId()),
                epic.getSubTasks());
    }

    @Test
    public void shouldReturnTaskWhenCreated() {
        Task task = new Task("Задача 1", "Собрание в 14:00",
                State.IN_PROGRESS,
                Duration.ofMinutes(5),
                LocalDateTime.now());
        taskManager.addTask(task);
        Assertions.assertEquals(task, taskManager.getTask(1));
    }

    @Test
    public void shouldTrowsExceptionWhenTasksInOneTime() {
        Task task1 = new Task("Задача 1", "Собрание в 14:00",
                State.IN_PROGRESS,
                Duration.ofMinutes(60),
                LocalDateTime.of(2022, 10, 3, 8, 30));
        taskManager.addTask(task1);

        Task task2 = new Task("Задача 2", "Вынести мусор",
                State.NEW,
                Duration.ofMinutes(20),
                LocalDateTime.of(2022, 10, 3, 9, 29));

        ManagerTaskException ex = Assertions.assertThrows(
                ManagerTaskException.class,
                () -> taskManager.addTask(task2)
        );
        Assertions.assertEquals("Невозможно запланировать задачу на это время",
                ex.getMessage());
    }

    @Test
    public void shouldCheckRemovingTask() {
        taskManager.removeTask();
        Task task1 = new Task("Задача 1", "Собрание в 14:00",
                State.IN_PROGRESS,
                Duration.ofMinutes(5),
                LocalDateTime.now());
        taskManager.addTask(task1);

        Task task2 = new Task("Задача 2", "Вынести мусор",
                State.NEW,
                Duration.ofMinutes(5),
                LocalDateTime.of(2022, 3, 15, 10, 30));
        taskManager.addTask(task2);
        taskManager.getTask(task2.getTaskId());
        taskManager.removeTask(task2.getTaskId());

        Assertions.assertIterableEquals(List.of(task2), taskManager.getAllTasks());
    }

    @Test
    public void shouldCheckEpicDurationWhenRemoveSubTask() {
        Epic epic = new Epic("Эпик 1", "Отпраздновать новый год",
                State.NEW);
        taskManager.addTask(epic);

        SubTask subTask1 = new SubTask("Подзадача 1", "Купить подарки",
                State.NEW,
                epic.getTaskId(),
                Duration.ofMinutes(10),
                LocalDateTime.of(2022, 3, 15, 13, 30));
        taskManager.addTask(subTask1);

        SubTask subTask2 = new SubTask("Подзадача 2", "Пригласить друзей",
                State.NEW,
                epic.getTaskId(),
                Duration.ofMinutes(30),
                LocalDateTime.now());
        taskManager.addTask(subTask2);
        taskManager.getTask(subTask2.getTaskId());
        taskManager.removeTask(subTask2.getTaskId());

        Assertions.assertEquals(10,
                taskManager.getTask(epic.getTaskId()).getDuration().toMinutes());
    }

    @Test
    public void shouldCheckPriorityOfTask() {
        Task task1 = new Task("Задача 1", "Собрание в 14:00",
                State.IN_PROGRESS,
                Duration.ofMinutes(5),
                LocalDateTime.of(2022, 12, 10, 10, 30));
        taskManager.addTask(task1);

        Task task2 = new Task("Задача 2", "Вынести мусор",
                State.NEW,
                Duration.ofMinutes(5),
                LocalDateTime.of(2022, 10, 3, 10, 30));
        taskManager.addTask(task2);

        Assertions.assertIterableEquals(List.of(task2, task1), taskManager.getPrioritizedTasks());
    }
}
