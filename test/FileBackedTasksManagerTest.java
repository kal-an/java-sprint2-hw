import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tracker.manager.FileBackedTasksManager;
import ru.yandex.practicum.tracker.tasks.SubTask;
import ru.yandex.practicum.tracker.tasks.Task;

import java.util.List;

class FileBackedTasksManagerTest {

    private FileBackedTasksManager fileBackedTasksManager;

    @BeforeEach
    public void createBeforeEach() {
        fileBackedTasksManager = FileBackedTasksManager.start();
    }

    @Test
    public void shouldReturnTrueWhenTaskListIsEmpty() {
        List<Task> taskList = fileBackedTasksManager.getAllTasks();
        Assertions.assertTrue(taskList.isEmpty());
    }

    @Test
    public void shouldReturnTrueWhenEpicWithoutSubTask() {
        List<SubTask> taskList = fileBackedTasksManager.getSubTasks(7);
        Assertions.assertTrue(taskList.isEmpty());
    }

    @Test
    public void shouldReturnTrueWhenHistoryIsEmpty() {
        List<Task> taskList = fileBackedTasksManager.getHistory();
        Assertions.assertTrue(taskList.isEmpty());
    }
}