import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tracker.exceptions.ManagerTaskException;
import ru.yandex.practicum.tracker.manager.InMemoryTasksManager;
import ru.yandex.practicum.tracker.manager.TaskManager;
import ru.yandex.practicum.tracker.manager.history.HistoryManager;
import ru.yandex.practicum.tracker.manager.history.InMemoryHistoryManager;
import ru.yandex.practicum.tracker.tasks.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

//класс тестирования менеджера в памяти
class InMemoryTasksManagerTest {

    HistoryManager historyManager;
    TaskManager taskManager;

    @BeforeEach
    public void createBeforeEach() {
        historyManager = new InMemoryHistoryManager();
        taskManager = new InMemoryTasksManager(historyManager);
    }


    @Test
    public void shouldReturnTaskList() {
        long taskId1 = TaskId.getNewId();
        Task task1 = new Task("Задача 1", "Собрание в 14:00",
                taskId1,
                State.NEW,
                Duration.ofMinutes(5),
                LocalDateTime.of(2022, 3, 15, 10, 30));
        taskManager.addTask(task1);
        long taskId2 = TaskId.getNewId();
        Task task2 = new Task("Задача 2", "Вынести мусор",
                taskId2,
                State.NEW,
                Duration.ofMinutes(5),
                LocalDateTime.of(2022, 3, 15, 12, 30));
        taskManager.addTask(task2);

        Assertions.assertIterableEquals(List.of(task1, task2), taskManager.getAllTasks());
    }

    @Test
    public void shouldReturnEmptyTaskList() {
        Assertions.assertIterableEquals(Collections.emptyList(), taskManager.getAllTasks());
    }

    @Test
    public void shouldCheckIfEpicExistInSubTask() {
        long epicId1 = 1;
        taskManager.addTask(new Epic("Эпик 1", "Отпраздновать новый год",
                State.NEW,
                epicId1));

        long subTaskId1 = 2;
        SubTask subTask1 = new SubTask("Подзадача 1", "Купить подарки",
                subTaskId1,
                State.NEW,
                1,
                Duration.ofMinutes(5),
                LocalDateTime.of(2022, 3, 15, 13, 30));
        taskManager.addTask(subTask1);

        Assertions.assertEquals(1L, subTask1.getEpicId());
    }

    @Test
    public void shouldCheckEpicStatusDoneWhenSubTaskDone() {
        long epicId1 = 1;
        taskManager.addTask(new Epic("Эпик 1", "Отпраздновать новый год",
                State.NEW,
                epicId1));

        long subTaskId1 = 2;
        SubTask subTask1 = new SubTask("Подзадача 1", "Купить подарки",
                subTaskId1,
                State.NEW,
                1,
                Duration.ofMinutes(5),
                LocalDateTime.of(2022, 3, 15, 13, 30));
        taskManager.addTask(subTask1);
        subTask1 = new SubTask("Подзадача 1", "Купить подарки",
                subTaskId1,
                State.DONE,
                1,
                Duration.ofMinutes(5),
                LocalDateTime.of(2022, 3, 15, 13, 30));
        taskManager.updateTask(subTask1);

        Assertions.assertEquals(State.DONE, taskManager.getTask(epicId1).getTaskStatus());
    }

    @Test
    public void shouldCheckEpicStatusInProgressWhenSubTaskInProgress() {
        long epicId1 = 1;
        taskManager.addTask(new Epic("Эпик 1", "Отпраздновать новый год",
                State.NEW,
                epicId1));

        long subTaskId1 = 2;
        SubTask subTask1 = new SubTask("Подзадача 1", "Купить подарки",
                subTaskId1,
                State.NEW,
                1,
                Duration.ofMinutes(5),
                LocalDateTime.of(2022, 3, 15, 13, 30));
        taskManager.addTask(subTask1);
        subTask1 = new SubTask("Подзадача 1", "Купить подарки",
                subTaskId1,
                State.IN_PROGRESS,
                1,
                Duration.ofMinutes(5),
                LocalDateTime.of(2022, 3, 15, 13, 30));
        taskManager.updateTask(subTask1);

        Assertions.assertEquals(State.IN_PROGRESS, taskManager.getTask(epicId1).getTaskStatus());
    }

    @Test
    public void shouldReturnNullWhenTaskIdIsNotExist() {
        Assertions.assertNull(taskManager.getTask(1));
    }

    @Test
    public void shouldReturnEpicList() {
        long epicId1 = TaskId.getNewId();
        Epic epic1 = new Epic("Эпик 1", "Отпраздновать новый год",
                State.NEW,
                epicId1);
        taskManager.addTask(epic1);
        long epicId2 = TaskId.getNewId();
        Epic epic2 = new Epic("Эпик 2", "Убраться в квартире",
                State.NEW,
                epicId2);
        taskManager.addTask(epic2);

        Assertions.assertIterableEquals(List.of(epic1, epic2), taskManager.getEpics());
    }

    @Test
    public void shouldReturnSubTaskListFromEpic() {
        long epicId1 = TaskId.getNewId();
        Epic epic = new Epic("Эпик 1", "Отпраздновать новый год",
                State.NEW,
                epicId1);
        taskManager.addTask(epic);

        long subTaskId1 = TaskId.getNewId();
        SubTask subTask1 = new SubTask("Подзадача 1", "Купить подарки",
                subTaskId1,
                State.NEW,
                1,
                Duration.ofMinutes(5),
                LocalDateTime.of(2022, 3, 15, 13, 30));
        taskManager.addTask(subTask1);

        long subTaskId2 = TaskId.getNewId();
        SubTask subTask2 = new SubTask("Подзадача 2", "Пригласить друзей",
                subTaskId2,
                State.NEW,
                epicId1,
                Duration.ofMinutes(5),
                LocalDateTime.now());
        taskManager.addTask(subTask2);

        Assertions.assertIterableEquals(List.of(subTaskId1, subTaskId2), epic.getSubTasks());
    }

    @Test
    public void shouldReturnTaskWhenCreated() {
        long taskId1 = 1;
        Task task = new Task("Задача 1", "Собрание в 14:00",
                taskId1,
                State.IN_PROGRESS,
                Duration.ofMinutes(5),
                LocalDateTime.now());
        taskManager.addTask(task);
        Assertions.assertEquals(task, taskManager.getTask(1));
    }

    @Test
    public void shouldReturnTrueWhenTasksInOneTime() {
        long epicId1 = TaskId.getNewId();
        Epic epic = new Epic("Эпик 1", "Отпраздновать новый год",
                State.NEW,
                epicId1);
        taskManager.addTask(epic);

        long subTaskId1 = TaskId.getNewId();
        SubTask subTask1 = new SubTask("Подзадача 1", "Купить подарки",
                subTaskId1,
                State.NEW,
                1,
                Duration.ofMinutes(5),
                LocalDateTime.of(2022, 10, 3, 10, 30));
        taskManager.addTask(subTask1);

        long subTaskId2 = TaskId.getNewId();
        SubTask subTask2 = new SubTask("Подзадача 2", "Пригласить друзей",
                subTaskId2,
                State.NEW,
                epicId1,
                Duration.ofMinutes(5),
                LocalDateTime.of(2022, 10, 3, 10, 30));

        ManagerTaskException ex = Assertions.assertThrows(
                ManagerTaskException.class,
                () -> taskManager.addTask(subTask2)
        );
        Assertions.assertEquals("Невозможно запланировать задачу на это время", ex.getMessage());
    }

    @Test
    public void shouldCheckRemovingTask() {
        long taskId1 = TaskId.getNewId();
        Task task1 = new Task("Задача 1", "Собрание в 14:00",
                taskId1,
                State.IN_PROGRESS,
                Duration.ofMinutes(5),
                LocalDateTime.now());
        taskManager.addTask(task1);

        long taskId2 = TaskId.getNewId();
        Task task2 = new Task("Задача 2", "Вынести мусор",
                taskId2,
                State.NEW,
                Duration.ofMinutes(5),
                LocalDateTime.now());
        taskManager.addTask(task2);
        taskManager.getTask(taskId1);
        taskManager.removeTask(taskId1);

        Assertions.assertIterableEquals(List.of(task2), taskManager.getAllTasks());
    }

    @Test
    public void shouldCheckPriorityOfTask() {
        long taskId1 = TaskId.getNewId();
        Task task1 = new Task("Задача 1", "Собрание в 14:00",
                taskId1,
                State.IN_PROGRESS,
                Duration.ofMinutes(5),
                LocalDateTime.of(2022, 12, 10, 10, 30));
        taskManager.addTask(task1);

        long taskId2 = TaskId.getNewId();
        Task task2 = new Task("Задача 2", "Вынести мусор",
                taskId2,
                State.NEW,
                Duration.ofMinutes(5),
                LocalDateTime.of(2022, 10, 3, 10, 30));
        taskManager.addTask(task2);

        Assertions.assertIterableEquals(List.of(task2, task1), taskManager.getPrioritizedTasks());
    }
}