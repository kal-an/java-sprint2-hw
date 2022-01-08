package ru.yandex.practicum.tracker.manager;

import ru.yandex.practicum.tracker.tasks.Epic;
import ru.yandex.practicum.tracker.tasks.State;
import ru.yandex.practicum.tracker.tasks.SubTask;
import ru.yandex.practicum.tracker.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryTasksManager implements TaskManager<Task> {
    private final HashMap<Long, Task> tasks; //таблица всех задач
    private static final int MAX_QUEUE_CAPACITY = 10; //макс. количесто недавних задач
    private final ArrayList<Task> recentlyTasks; //список недавних задач

    public InMemoryTasksManager() {
        tasks = new HashMap<>();
        recentlyTasks = new ArrayList<>();
    }

    //Получение списка всех задач.
    @Override
    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> allTasks = new ArrayList<>();
        if (isAnyTasks()) { //есть задачи
            for (Long taskId : tasks.keySet()) {
                allTasks.add(tasks.get(taskId));
            }
        }
        return allTasks;
    }

    //Получение списка всех эпиков.
    @Override
    public ArrayList<Task> getEpics() {
        ArrayList<Task> epics = new ArrayList<>();
        if (isAnyTasks()) { //если список задач не пустой
            for (Long taskId : tasks.keySet()) {
                if (tasks.get(taskId) instanceof Epic) { //если это эпик
                    epics.add(tasks.get(taskId));
                }
            }
        }
        return epics;
    }

    //Получение списка всех подзадач определённого эпика.
    @Override
    public ArrayList<SubTask> getSubTasks(long epicId) {
        ArrayList<SubTask> subTasks = new ArrayList<>();
        Epic epic = (Epic) tasks.get(epicId);
        for (Long taskId : epic.getSubTasks()) {
            subTasks.add((SubTask) tasks.get(taskId));
        }
        return subTasks;
    }

    //Добавление подзадачи к эпику.
    private void setSubTasks(long epicId, SubTask newSubTask) {
        Epic epic = (Epic) tasks.get(epicId); //найти нужный эпик
        ArrayList<Long> subTasks = epic.getSubTasks();
        subTasks.add(newSubTask.getTaskId()); //добавить подзадачу к эпику
    }

    //Получение задачи любого типа по идентификатору.
    @Override
    public Task getTask(long taskId) {
        Task task = tasks.get(taskId);
        updateHistory(task);
        return task;
    }

    //Добавление новой задачи, эпика и подзадачи.
    @Override
    public void addTask(Task newTask) {
        newTask.setTaskStatus(State.NEW); //установить статус Новая
        tasks.put(newTask.getTaskId(), newTask); //добавить в список задач

        if (newTask instanceof SubTask) { //если это подзадача, то добавить к эпику
            setSubTasks(((SubTask) newTask).getEpicId(), (SubTask) newTask);
        }
    }

    //Обновление задачи любого типа по идентификатору.
    @Override
    public void updateTask(Task newTask) {
        if (newTask != null) { //если задача не пустая
            State newStatus = newTask.getTaskStatus();
            setTaskStatus(newTask, newStatus); //установить новый статус

            if (newTask instanceof SubTask) { //если это подзадача
                long epicId = ((SubTask) newTask).getEpicId(); //найти id эпика
                Epic epic = (Epic) tasks.get(epicId);
                if (isAllSubTaskInEpicDone(epic)) {
                    //если все подзадачи готовы, то эпик тоже готов
                    setTaskStatus(epic, State.DONE);
                } else { //если есть задачи в процессе выполнения, то эпик на выполнении
                    setTaskStatus(epic, State.IN_PROGRESS);
                }

            }
        }
    }

    //установить статус задачи
    private void setTaskStatus(Task task, State newStatus) {
        tasks.get(task.getTaskId()).setTaskStatus(newStatus);
    }

    //проверить готовность подзадач эпика
    private boolean isAllSubTaskInEpicDone(Epic epicId) {
        ArrayList<SubTask> subTasks = getSubTasks(epicId.getTaskId());
        for (Task subTask : subTasks) {
            //если нет выполненных подзадач то вернуть false
            if (!subTask.getTaskStatus().equals(State.DONE)) {
                return false;
            }
        }
        return true;
    }

    //Удаление ранее всех добавленных задач.
    @Override
    public void removeTask() {
        if (isAnyTasks()) { //есть ли задачи
            tasks.clear(); //удалить все задачи
        }
    }

    //Удаление задачи по идентификатору.
    @Override
    public void removeTask(long newTaskId) {
        //удаление подзадач если переданный ID является эпиком
        if (tasks.get(newTaskId) instanceof Epic) {
            ArrayList<SubTask> subTasks = getSubTasks(newTaskId); //список подзадач эпика
            for (Task subTask : subTasks) {
                tasks.remove(subTask.getTaskId()); //удалить задачу
            }
        }
        tasks.remove(newTaskId);
    }

    //проверить есть ли задачи в таблице
    private boolean isAnyTasks() {
        return !tasks.isEmpty();
    }

    //Просмотр истории задач.
    @Override
    public ArrayList<Task> getHistory() {
        return recentlyTasks;
    }

    //Обновление истории задач.
    @Override
    public void updateHistory(Task viewedTask) {
        int currentCapacity = recentlyTasks.size();
        if (currentCapacity < MAX_QUEUE_CAPACITY) { //если есть место в списке
            recentlyTasks.add(viewedTask);
        } else {
            recentlyTasks.remove(0); //удалить первую задачу
            recentlyTasks.add(viewedTask); //добавить новую просмотренную задачу
        }
    }
}
