import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

    private TaskManager taskManager;
    private Task task1;
    private Task task2;
    private Epic epic1;
    private Epic epic2;
    private SubTask subTask1;
    private SubTask subTask2;

    @Test
    @BeforeEach
    @DisplayName("Создание менеджера задач")
    public void createManagerAndTaskList() {
        taskManager = new InMemoryTasksManager();

        task1 = new Task("Задача 1", "Собрание в 14:00",
                Duration.ofMinutes(30),
                LocalDateTime.of(2020, 3, 15, 14, 0));
        taskManager.addTask(task1);

        task2 = new Task("Задача 2", "Вынести мусор",
                Duration.ofMinutes(5),
                LocalDateTime.of(2020, 4, 15, 10, 30));
        taskManager.addTask(task2);

        epic1 = new Epic("Эпик 1", "Отпраздновать новый год");
        taskManager.addTask(epic1);

        subTask1 = new SubTask("Подзадача 1", "Купить подарки",
                epic1.getTaskId(),
                Duration.ofMinutes(60),
                LocalDateTime.of(2020, 12, 15, 13, 30));
        taskManager.addTask(subTask1);

        subTask2 = new SubTask("Подзадача 2", "Пригласить друзей",
                epic1.getTaskId(),
                Duration.ofMinutes(5),
                LocalDateTime.of(2022, 12, 17, 12, 0));
        taskManager.addTask(subTask2);

        epic2 = new Epic("Эпик 2", "Убраться в квартире");
        taskManager.addTask(epic2);
    }

    @Test
    @DisplayName("Удаление всех задач")
    public void shouldReturnEmptyTaskList() {
        taskManager.getTask(task1.getTaskId());
        taskManager.getTask(task2.getTaskId());
        Assertions.assertIterableEquals(List.of(task1, task2), taskManager.getHistory());
        taskManager.removeTask();
        Assertions.assertIterableEquals(Collections.emptyList(), taskManager.getHistory());
        Assertions.assertIterableEquals(Collections.emptyList(), taskManager.getAllTasks());
    }

    @Test
    @DisplayName("Удаление задачи по идентификатору")
    public void shouldReturnTaskListWhenRemoveTask() {
        taskManager.getTask(task1.getTaskId());
        taskManager.getTask(task2.getTaskId());
        Assertions.assertIterableEquals(List.of(task1, task2), taskManager.getHistory());
        taskManager.removeTask(task1.getTaskId());
        Assertions.assertIterableEquals(List.of(task2), taskManager.getHistory());
        Assertions.assertIterableEquals(List.of(task2, epic1, subTask1, subTask2, epic2),
                taskManager.getAllTasks());
    }

    @Test
    @DisplayName("Удаление задачи если идентификатор не существует")
    public void shouldThrowsWhenRemoveTaskIdNotExist() {
        ManagerTaskException ex = Assertions.assertThrows(
                ManagerTaskException.class,
                () -> taskManager.removeTask(13346345)
        );
        Assertions.assertEquals("Задачи с таким ID не найдено", ex.getMessage());
    }

    @Test
    @DisplayName("Удаление эпика по идентификатору без подзадач")
    public void shouldReturnTaskListWhenRemoveEpicWithEmptySubTask() {
        taskManager.removeTask(epic2.getTaskId());
        Assertions.assertIterableEquals(List.of(task1, task2, epic1, subTask1, subTask2),
                taskManager.getAllTasks());
    }

    @Test
    @DisplayName("Удаление эпика по идентификатору с подзадачами")
    public void shouldReturnTaskListWhenRemoveEpicWithSubTask() {
        taskManager.removeTask(epic1.getTaskId());
        Assertions.assertIterableEquals(List.of(task1, task2, epic2),
                taskManager.getAllTasks());
    }

    @Test
    @DisplayName("Удаление подзадачи по идентификатору")
    public void shouldReturnTaskListWhenRemoveSubTask() {
        taskManager.removeTask(subTask1.getTaskId());
        Assertions.assertIterableEquals(List.of(task1, task2, epic1, subTask2, epic2),
                taskManager.getAllTasks());
        Assertions.assertEquals(List.of(subTask2), taskManager.getSubTasks(epic1.getTaskId()));
    }

    @Test
    @DisplayName("Проверка наличия эпика в подзадаче")
    public void shouldCheckIfEpicExistInSubTask() {
        Assertions.assertEquals(epic1.getTaskId(), subTask1.getEpicId());
        Assertions.assertEquals(epic1.getTaskId(), subTask2.getEpicId());
    }

    @Test
    @DisplayName("Статус эпика DONE когда все подзадачи DONE")
    public void shouldCheckEpicStatusDoneWhenSubTaskDone() {
        subTask1.setTaskStatus(State.DONE);
        subTask2.setTaskStatus(State.DONE);
        taskManager.updateTask(subTask1);
        taskManager.updateTask(subTask2);
        Assertions.assertEquals(State.DONE, taskManager.getTask(epic1.getTaskId()).getTaskStatus());
    }

    @Test
    @DisplayName("Статус эпика IN_PROGRESS когда одна подзадача IN_PROGRESS")
    public void shouldCheckEpicStatusInProgressWhenSubTaskInProgress() {
        subTask1.setTaskStatus(State.DONE);
        subTask2.setTaskStatus(State.IN_PROGRESS);
        taskManager.updateTask(subTask1);
        taskManager.updateTask(subTask2);
        Assertions.assertEquals(State.IN_PROGRESS,
                taskManager.getTask(epic1.getTaskId()).getTaskStatus());
    }

    @Test
    @DisplayName("Обновление статуса задачи")
    public void shouldUpdateTaskStatus() {
        Assertions.assertEquals(State.NEW,
                taskManager.getTask(task2.getTaskId()).getTaskStatus());
        task2.setTaskStatus(State.IN_PROGRESS);
        taskManager.updateTask(task2);
        Assertions.assertEquals(State.IN_PROGRESS,
                taskManager.getTask(task2.getTaskId()).getTaskStatus());
    }

    @Test
    @DisplayName("Обновление статуса подзадачи")
    public void shouldUpdateSubTaskStatus() {
        Assertions.assertEquals(State.NEW,
                taskManager.getTask(subTask2.getTaskId()).getTaskStatus());
        subTask2.setTaskStatus(State.IN_PROGRESS);
        taskManager.updateTask(subTask2);
        Assertions.assertEquals(State.IN_PROGRESS,
                taskManager.getTask(subTask2.getTaskId()).getTaskStatus());
    }

    @Test
    @DisplayName("Получение списка эпиков")
    public void shouldReturnEpicList() {
        Assertions.assertIterableEquals(List.of(epic1, epic2), taskManager.getEpics());
    }

    @Test
    @DisplayName("Получение списка всех задач")
    public void shouldReturnTaskList() {
        Assertions.assertIterableEquals(List.of(task1, task2, epic1, subTask1, subTask2, epic2),
                taskManager.getAllTasks());
    }

    @Test
    @DisplayName("Получение задачи по идентификатору")
    public void shouldReturnTask() {
        Assertions.assertEquals(task1, taskManager.getTask(task1.getTaskId()));
        Assertions.assertEquals(epic1, taskManager.getTask(epic1.getTaskId()));
        Assertions.assertEquals(subTask1, taskManager.getTask(subTask1.getTaskId()));
    }

    @Test
    @DisplayName("Получение подзадач эпика")
    public void shouldReturnSubTasks() {
        Assertions.assertIterableEquals(List.of(subTask1, subTask2),
                taskManager.getSubTasks(epic1.getTaskId()));
        Assertions.assertIterableEquals(Collections.emptyList(),
                taskManager.getSubTasks(epic2.getTaskId()));
    }

    @Test
    @DisplayName("Проверка пересечения задачи с началом другой")
    public void shouldTrowsExceptionWhenTasksStartInOneTime() {
        Task task3 = new Task("Задача 3", "Вынести мусор",
                Duration.ofMinutes(5),
                LocalDateTime.of(2020, 3, 15, 13, 58));

        ManagerTaskException ex = Assertions.assertThrows(
                ManagerTaskException.class,
                () -> taskManager.addTask(task3)
        );
        Assertions.assertEquals("Невозможно запланировать задачу на это время",
                ex.getMessage());
    }

    @Test
    @DisplayName("Проверка пересечения задачи с концом другой")
    public void shouldTrowsExceptionWhenTasksEndInOneTime() {
        Task task3 = new Task("Задача 3", "Вынести мусор",
                Duration.ofMinutes(5),
                LocalDateTime.of(2020, 3, 15, 14, 29));

        ManagerTaskException ex = Assertions.assertThrows(
                ManagerTaskException.class,
                () -> taskManager.addTask(task3)
        );
        Assertions.assertEquals("Невозможно запланировать задачу на это время",
                ex.getMessage());
    }

    @Test
    @DisplayName("Проверка продолжительности эпика без подзадач")
    public void shouldReturnEpicWithoutSubTask() {
        Assertions.assertEquals(0,
                taskManager.getTask(epic2.getTaskId()).getDuration().toMinutes());
    }

    @Test
    @DisplayName("Проверка продолжительности эпика с подзадачами")
    public void shouldReturnEpicWithSubTask() {
        Assertions.assertEquals(65,
                taskManager.getTask(epic1.getTaskId()).getDuration().toMinutes());
    }

    @Test
    @DisplayName("Проверка продолжительности эпика при удалении подзадачи")
    public void shouldCheckEpicDurationWhenRemoveSubTask() {
        taskManager.removeTask(subTask2.getTaskId());
        Assertions.assertEquals(60,
                taskManager.getTask(epic1.getTaskId()).getDuration().toMinutes());
    }

    @Test
    @DisplayName("Проверка продолжительности эпика при добавлении подзадачи")
    public void shouldCheckEpicDurationWhenAddSubTask() {
        SubTask subTask3 = new SubTask("Подзадача 3", "Придумать меню",
                epic1.getTaskId(),
                Duration.ofMinutes(20),
                LocalDateTime.of(2022, 12, 19, 12, 0));
        taskManager.addTask(subTask3);
        Assertions.assertEquals(85,
                taskManager.getTask(epic1.getTaskId()).getDuration().toMinutes());
    }

    @Test
    @DisplayName("Получение задач по приоритету")
    public void shouldCheckPriorityOfTask() {
        Task task3 = new Task("Задача 3", "Вынести мусор",
                Duration.ofMinutes(5),
                LocalDateTime.of(2020, 3, 10, 14, 29));
        taskManager.addTask(task3);
        Assertions.assertIterableEquals(List.of(task3, task1, task2, subTask1, subTask2),
                taskManager.getPrioritizedTasks());
    }

    @Test
    @DisplayName("Добавление задач")
    public void shouldCheckAddTasks() {
        Task task3 = new Task("Задача 3", "Вынести мусор",
                Duration.ofMinutes(5),
                LocalDateTime.of(2020, 3, 10, 14, 29));
        taskManager.addTask(task3);
        Assertions.assertEquals(7, taskManager.getAllTasks().size());

        SubTask subTask3 = new SubTask("Подзадача 3", "Придумать меню",
                epic1.getTaskId(),
                Duration.ofMinutes(20),
                LocalDateTime.of(2022, 12, 19, 12, 0));
        taskManager.addTask(subTask3);
        Assertions.assertEquals(8, taskManager.getAllTasks().size());

        Epic epic3 = new Epic("Эпик 3", "Написать статью");
        taskManager.addTask(epic3);
        Assertions.assertEquals(9, taskManager.getAllTasks().size());
    }
}