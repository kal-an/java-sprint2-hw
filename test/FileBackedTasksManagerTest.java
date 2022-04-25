import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tracker.manager.FileBackedTasksManager;
import ru.yandex.practicum.tracker.tasks.Task;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;

class FileBackedTasksManagerTest extends TaskManagerTest {

    private Task task1;
    private Task task2;

    @Test
    @DisplayName("Сохранение и восстановление задач")
    void saveAndLoad() {
        final String filename = "./src/ru/yandex/practicum/tracker/test.csv";
        File file = new File(filename);
        taskManager = FileBackedTasksManager.loadFromFile(file);
        taskManager.removeTask();

        Assertions.assertEquals(0, taskManager.getAllTasks().size());
        Assertions.assertEquals(0, taskManager.getHistory().size());

        task1 = new Task("Задача 1", "Собрание в 14:00",
                Duration.ofMinutes(30),
                LocalDateTime.of(2020, 3, 15, 14, 0));
        taskManager.addTask(task1);

        task2 = new Task("Задача 2", "Вынести мусор",
                Duration.ofMinutes(5),
                LocalDateTime.of(2020, 4, 15, 10, 30));
        taskManager.addTask(task2);
        taskManager.getTask(task1.getTaskId());
        taskManager.getTask(task2.getTaskId());

        FileBackedTasksManager newTaskManager = FileBackedTasksManager.loadFromFile(file);

        Assertions.assertEquals(2, newTaskManager.getAllTasks().size());
        Assertions.assertEquals(taskManager.getHistory().size(),
                newTaskManager.getHistory().size());
    }
}