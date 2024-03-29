package ru.yandex.practicum.tracker.manager;

import ru.yandex.practicum.tracker.exceptions.ManagerSaveException;
import ru.yandex.practicum.tracker.manager.history.HistoryManager;
import ru.yandex.practicum.tracker.tasks.*;
import ru.yandex.practicum.tracker.utils.DateFormat;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// класс менеджера для автосохранения в файл
public class FileBackedTasksManager extends InMemoryTasksManager {
    private static File backupFile;

    @Override
    public ArrayList<Task> getAllTasks() {

        return super.getAllTasks();
    }

    @Override
    public ArrayList<Task> getEpics() {

        return super.getEpics();
    }

    @Override
    public ArrayList<SubTask> getSubTasks(long epicId) {

        return super.getSubTasks(epicId);
    }

    @Override
    public Task getTask(long taskId) {
        Task task = super.getTask(taskId);
        save();
        return task;
    }

    @Override
    public void addTask(Task newTask) {
        super.addTask(newTask);
        save();
    }

    @Override
    public void updateTask(Task newTask) {
        super.updateTask(newTask);
        save();
    }

    @Override
    public void removeTask() {
        super.removeTask();
        save();
    }

    @Override
    public void removeTask(long newTaskId) {
        super.removeTask(newTaskId);
        save();
    }

    // сохранить текущее состояние менеджера
    protected void save() throws ManagerSaveException {
        StringBuilder sb = new StringBuilder();
        sb.append("id,type,name,status,description,duration,startTime,epic").append("\n");

        try (BufferedWriter fileWriter = new BufferedWriter(
                new FileWriter(backupFile, StandardCharsets.UTF_8))) {
            fileWriter.write(sb.toString());

            for (Task task : getAllTasks()) {
                fileWriter.write(taskToString(task));
                fileWriter.newLine();
            }
            fileWriter.newLine();
            fileWriter.write(toString(historyManager));

        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось сохранить данные в файл");
        }
    }

    // сохранение задачи в строку
    private String taskToString(Task task) {
        StringBuilder sb = new StringBuilder();
        TaskType type;

        if (task instanceof SubTask) {
            type = TaskType.SUBTASK;
        } else if (task instanceof Epic) {
            type = TaskType.EPIC;
        } else {
            type = TaskType.TASK;
        }
        sb.append(task.getTaskId());
        sb.append(",").append(type);
        sb.append(",").append(task.getTaskName());
        sb.append(",").append(task.getTaskStatus());
        sb.append(",").append(task.getTaskDescription());
        sb.append(",").append(task.getDuration().toMinutes());
        sb.append(",").append(task.getStartTime().format(DateFormat.getDateTimeFormat()));

        if (type.equals(TaskType.SUBTASK)) {
            sb.append(",").append(((SubTask) task).getEpicId());
        }

        return sb.toString();
    }

    // создание задачи из строки
    private Task getTaskFromString(String value) {
        String[] line = value.split(",");
        long id = Long.parseLong(line[0]);
        TaskType type = TaskType.valueOf(line[1]);
        String name = line[2];
        State status = State.valueOf(line[3]);
        String description = line[4];
        Duration duration = Duration.ofMinutes(Long.parseLong(line[5]));
        LocalDateTime startTime = LocalDateTime.parse(line[6], DateFormat.getDateTimeFormat());
        Task task;

        if (type == TaskType.SUBTASK) {
            long epicId = Long.parseLong(line[7]);
            task = new SubTask(name, description, epicId, duration, startTime);
        } else if (type == TaskType.EPIC) {
            task = new Epic(name, description);
        } else {
            task = new Task(name, description, duration, startTime);
        }
        task.setTaskId(id);
        task.setTaskStatus(status);

        return task;
    }

    // сохранить историю просмотров в строку
    private static String toString(HistoryManager historyManager) {
        StringBuilder sb = new StringBuilder();
        for (Task task : historyManager.getHistory()) {
            sb.append(task.getTaskId()).append(",");
        }
        return sb.toString();
    }

    // создать список id задач из строки
    private static List<Long> fromString(String value) {
        List<Long> list = new ArrayList<>();
        String[] line = value.split(",");

        for (String string : line) {
            list.add(Long.parseLong(string));
        }
        return list;
    }

    // восстановить данные менеджера из файла
    public static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager taskManager = new FileBackedTasksManager();
        backupFile = file;
        try (BufferedReader fileReader = new BufferedReader(
                new FileReader(file, StandardCharsets.UTF_8))) {
            fileReader.readLine();
            while (fileReader.ready()) {
                String line = fileReader.readLine();
                if (!line.isEmpty()) {
                    Task task = taskManager.getTaskFromString(line);
                    tasks.put(task.getTaskId(), task);
                } else {
                    break;
                }
            }
            String history = fileReader.readLine();
            if (history != null) {
                for (Long id : fromString(history)) {
                    historyManager.add(tasks.get(id));
                }
            }

        } catch (IOException e) {
            System.out.println("Не удалось восстановить данные из файла");
            e.printStackTrace();
        }
        return taskManager;
    }
}
