import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tracker.manager.history.HistoryManager;
import ru.yandex.practicum.tracker.manager.history.InMemoryHistoryManager;
import ru.yandex.practicum.tracker.tasks.State;
import ru.yandex.practicum.tracker.tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

class HistoryManagerTest {

    HistoryManager historyManager;

    @BeforeEach
    public void createBeforeEach() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    public void shouldReturnEmptyHistory() {
        List<Task> taskList = historyManager.getHistory();
        Assertions.assertTrue(taskList.isEmpty());
    }

    @Test
    public void shouldReturnUniqueList() {
        Task task1 = new Task("Задача 1", "Собрание в 14:00",
                1,
                State.NEW,
                Duration.ofMinutes(15),
                LocalDateTime.of(2022, 3, 15, 10, 30));
        Task task2 = new Task("Задача 2", "Собрание в 14:30",
                2,
                State.NEW,
                Duration.ofMinutes(10),
                LocalDateTime.of(2022, 3, 14, 14, 30));
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task1);

        List<Task> taskList = historyManager.getHistory();
        Assertions.assertIterableEquals(List.of(task2, task1), taskList);
    }
}