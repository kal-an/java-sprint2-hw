package ru.yandex.practicum.tracker.manager;

import ru.yandex.practicum.tracker.exceptions.ManagerSaveException;
import ru.yandex.practicum.tracker.manager.history.HistoryManager;
import ru.yandex.practicum.tracker.manager.history.InMemoryHistoryManager;
import ru.yandex.practicum.tracker.tasks.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

//класс менеджера для автосохранения в файл
public class FileBackedTasksManager extends InMemoryTasksManager {
    private final static String BACKUP_FILE = "./src/ru/yandex/practicum/tracker/state.csv";
    private static List<Long> recentlyTasks;
    private static FileBackedTasksManager fileBackedTasksManager;

    public FileBackedTasksManager(HistoryManager historyManager) {
        super(historyManager);
    }

    public static void main(String[] args) {
        start();
    }

    private static void start() {
        File file = new File(BACKUP_FILE);
        fileBackedTasksManager = loadFromFile(file);
        long taskId1 = TaskId.getNewId();
        fileBackedTasksManager.addTask(new Task("Задача 1", "Собрание в 14:00",
                taskId1,
                State.IN_PROGRESS));
        long taskId2 = TaskId.getNewId();
        fileBackedTasksManager.addTask(new Task("Задача 2", "Вынести мусор",
                taskId2,
                State.NEW));

        long epicId1 = TaskId.getNewId();
        fileBackedTasksManager.addTask(new Epic("Эпик 1", "Отпраздновать новый год",
                epicId1,
                State.NEW));

        long subTaskId1 = TaskId.getNewId();
        fileBackedTasksManager.addTask(new SubTask("Подзадача 1", "Купить подарки",
                subTaskId1,
                State.NEW,
                epicId1));

        long subTaskId2 = TaskId.getNewId();
        fileBackedTasksManager.addTask(new SubTask("Подзадача 2", "Пригласить друзей",
                subTaskId2,
                State.NEW,
                epicId1));

        long subTaskId3 = TaskId.getNewId();
        fileBackedTasksManager.addTask(new SubTask("Подзадача 3", "За продуктами",
                subTaskId3,
                State.NEW,
                epicId1));

        long epicId2 = TaskId.getNewId();
        fileBackedTasksManager.addTask(new Epic("Эпик 2", "Убраться в квартире",
                epicId2,
                State.NEW));

        System.out.println("    Запрос задачи  " + fileBackedTasksManager.getTask(epicId2));
        System.out.println("    Запрос задачи  " + fileBackedTasksManager.getTask(epicId1));
        System.out.println("    Запрос задачи  " + fileBackedTasksManager.getTask(epicId2));
        System.out.println("    Запрос задачи  " + fileBackedTasksManager.getTask(subTaskId3));
        System.out.println("    Запрос задачи  " + fileBackedTasksManager.getTask(subTaskId1));
        System.out.println("    Запрос задачи  " + fileBackedTasksManager.getTask(subTaskId1));
        System.out.println("    Запрос задачи  " + fileBackedTasksManager.getTask(taskId1));
        System.out.println("    Запрос задачи  " + fileBackedTasksManager.getTask(subTaskId2));
        System.out.println("    Запрос задачи  " + fileBackedTasksManager.getTask(epicId2));
        System.out.println("    Запрос задачи  " + fileBackedTasksManager.getTask(epicId1));
    }

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

    protected void setSubTasks(long epicId, SubTask newSubTask) {
        super.setSubTasks(epicId, newSubTask);
        save();
    }

    @Override
    public Task getTask(long taskId) {
        save();
        return super.getTask(taskId);
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

    @Override
    public List<Task> getHistory() {
        List<Task> list = new ArrayList<>();
        for (Long id : recentlyTasks) {
            list.add(tasks.get(id));
        }
        return list;
    }

    //сохранить текущее состояние менеджера
    private void save() throws ManagerSaveException {
        StringBuilder sb = new StringBuilder();
        sb.append("id,type,name,status,description,epic").append("\n");
        
        try (BufferedWriter fileWriter = new BufferedWriter(
                new FileWriter(BACKUP_FILE, StandardCharsets.UTF_8))) {
            fileWriter.write(sb.toString());

            for (Task task : tasks.values()) {
                fileWriter.write(taskToString(task));
                fileWriter.newLine();
            }
            
            fileWriter.newLine();
            fileWriter.write(toString(historyManager));

        } catch (IOException e) {
            throw new ManagerSaveException();
        }
    }

    //сохранение задачи в строку
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
        sb.append(task.getTaskId()).append(",");
        sb.append(type).append(",");
        sb.append(task.getTaskName()).append(",");
        sb.append(task.getTaskStatus()).append(",");
        sb.append(task.getTaskDescription()).append(",");

        if (type.equals(TaskType.SUBTASK)) {
            sb.append(((SubTask) task).getEpicId()).append(",");
        }

        return sb.toString();
    }

    //создание задачи из строки
    private Task getTaskFromString(String value) {
        String[] line = value.split(",");
        long id = Long.parseLong(line[0]);
        TaskType type = TaskType.valueOf(line[1]);
        String name = line[2];
        State status = State.valueOf(line[3]);
        String description = line[4];
        Task task ;

        if (type == TaskType.SUBTASK) {
            long epicId = Long.parseLong(line[5]);
            task = new SubTask(name, description, id, status, epicId);
            setSubTasks(epicId, (SubTask) task);
        } else if (type == TaskType.EPIC) {
            task = new Epic(name, description, id, status);
        } else {
            task = new Task(name, description, id, status);
        }

        return task;
    }

    //сохранить историю просмотров в строку
    private static String toString(HistoryManager historyManager) {
        StringBuilder sb = new StringBuilder();
        for (Task task : historyManager.getHistory()) {
            sb.append(task.getTaskId()).append(",");
        }
        return sb.toString();
    }

    //создать список id задач из строки
    private static List<Long> fromString(String value) {
        List<Long> list = new ArrayList<>();
        String[] line = value.split(",");

        for (String string : line) {
            list.add(Long.parseLong(string));
        }
        return list;
    }

    //восстановить данные менеджера из файла
    private static FileBackedTasksManager loadFromFile(File file) {
        HistoryManager historyManager = new InMemoryHistoryManager();
        fileBackedTasksManager = new FileBackedTasksManager(historyManager);

        try (BufferedReader fileReader = new BufferedReader(
                new FileReader(file, StandardCharsets.UTF_8))) {
            fileReader.readLine();
            while (fileReader.ready()) {
                String line = fileReader.readLine();

                if (line.isEmpty()) {
                    break;
                } else {
                    Task task = fileBackedTasksManager.getTaskFromString(line);
                    long id = task.getTaskId();
                    tasks.put(id, task);
                }
            }
            String history = fileReader.readLine();
            recentlyTasks = fromString(history);

        } catch (IOException e) {
            System.out.println("Не удалось восстановить данные из файла");
            e.printStackTrace();
        }
        return fileBackedTasksManager;
    }
}
