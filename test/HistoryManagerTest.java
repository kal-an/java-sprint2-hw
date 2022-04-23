import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tracker.manager.history.HistoryManager;
import ru.yandex.practicum.tracker.manager.history.InMemoryHistoryManager;
import ru.yandex.practicum.tracker.tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

class HistoryManagerTest {

    private HistoryManager historyManager;

    @Test
    @BeforeEach
    @DisplayName("Создание менеджера истории задач")
    public void createBeforeEach() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    @DisplayName("Проверка пустой истории")
    public void shouldReturnEmptyHistory() {
        Assertions.assertTrue(historyManager.getHistory().isEmpty());
    }

    @Test
    @DisplayName("Проверка отсутствия дублей в истории")
    public void shouldReturnUniqueList() {
        Task task1 = new Task("Задача 1", "Собрание в 14:00",
                Duration.ofMinutes(15),
                LocalDateTime.of(2022, 3, 15, 10, 30));
        task1.setTaskId(1L);
        Task task2 = new Task("Задача 2", "Собрание в 14:30",
                Duration.ofMinutes(10),
                LocalDateTime.of(2022, 3, 14, 14, 30));
        task2.setTaskId(2L);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task1);

        System.out.println(historyManager.getHistory());
        Assertions.assertIterableEquals(List.of(task2, task1), historyManager.getHistory());
        Assertions.assertEquals(2, historyManager.getHistory().size());
    }

    @Test
    @DisplayName("Проверка удаления из начали истории")
    public void shouldCheckRemoveFromStartHistory() {
        Task task1 = new Task("Задача 1", "Собрание в 14:00",
                Duration.ofMinutes(15),
                LocalDateTime.of(2022, 1, 15, 10, 30));
        task1.setTaskId(1L);
        Task task2 = new Task("Задача 2", "Собрание в 14:30",
                Duration.ofMinutes(10),
                LocalDateTime.of(2022, 2, 14, 14, 30));
        task2.setTaskId(2L);
        Task task3 = new Task("Задача 3", "Собрание в 16:30",
                Duration.ofMinutes(15),
                LocalDateTime.of(2022, 3, 14, 14, 30));
        task3.setTaskId(3L);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(1L);

        Assertions.assertIterableEquals(List.of(task2, task3), historyManager.getHistory());
    }

    @Test
    @DisplayName("Проверка удаления с конца истории")
    public void shouldCheckRemovingFinishHistory() {
        Task task1 = new Task("Задача 1", "Собрание в 14:00",
                Duration.ofMinutes(15),
                LocalDateTime.of(2022, 1, 15, 10, 30));
        task1.setTaskId(1L);
        Task task2 = new Task("Задача 2", "Собрание в 14:30",
                Duration.ofMinutes(10),
                LocalDateTime.of(2022, 2, 14, 14, 30));
        task2.setTaskId(2L);
        Task task3 = new Task("Задача 3", "Собрание в 16:30",
                Duration.ofMinutes(15),
                LocalDateTime.of(2022, 3, 14, 14, 30));
        task3.setTaskId(3L);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(3L);

        Assertions.assertIterableEquals(List.of(task1, task2), historyManager.getHistory());
    }

    @Test
    @DisplayName("Проверка удаления с середины истории")
    public void shouldCheckRemovingMiddleHistory() {
        Task task1 = new Task("Задача 1", "Собрание в 14:00",
                Duration.ofMinutes(15),
                LocalDateTime.of(2022, 1, 15, 10, 30));
        task1.setTaskId(1L);
        Task task2 = new Task("Задача 2", "Собрание в 14:30",
                Duration.ofMinutes(10),
                LocalDateTime.of(2022, 2, 14, 14, 30));
        task2.setTaskId(2L);
        Task task3 = new Task("Задача 3", "Собрание в 16:30",
                Duration.ofMinutes(15),
                LocalDateTime.of(2022, 3, 14, 14, 30));
        task3.setTaskId(3L);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(2L);

        Assertions.assertIterableEquals(List.of(task1, task3), historyManager.getHistory());
    }

    @Test
    @DisplayName("Проверки очистки истории")
    public void shouldCheckClearHistory() {
        Task task1 = new Task("Задача 1", "Собрание в 14:00",
                Duration.ofMinutes(15),
                LocalDateTime.of(2022, 3, 15, 10, 30));
        Task task2 = new Task("Задача 2", "Собрание в 14:30",
                Duration.ofMinutes(10),
                LocalDateTime.of(2022, 3, 14, 14, 30));
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task1);
        historyManager.clear();

        Assertions.assertIterableEquals(List.of(), historyManager.getHistory());
    }
}