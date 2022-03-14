package ru.yandex.practicum.tracker.manager;

import ru.yandex.practicum.tracker.manager.history.HistoryManager;
import ru.yandex.practicum.tracker.tasks.Epic;
import ru.yandex.practicum.tracker.tasks.State;
import ru.yandex.practicum.tracker.tasks.SubTask;
import ru.yandex.practicum.tracker.tasks.Task;

import java.util.*;

public class InMemoryTasksManager implements TaskManager {
    protected static HashMap<Long, Task> tasks = null; //таблица всех задач
    protected HistoryManager historyManager; //менеджер истории

    public InMemoryTasksManager(HistoryManager historyManager) {
        tasks = new HashMap<>();
        this.historyManager = historyManager;
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
    protected void setSubTasks(SubTask newSubTask) {
        long subTaskDuration = newSubTask.getDuration().toMinutes();
        long epicId = newSubTask.getEpicId(); //найти нужный эпик
        Epic epic = (Epic) tasks.get(epicId);
        ArrayList<Long> subTasks = epic.getSubTasks();
        epic.setDuration(epic.getDuration().plusMinutes(subTaskDuration));
        subTasks.add(newSubTask.getTaskId()); //добавить подзадачу к эпику
    }

    //Получение задачи любого типа по идентификатору.
    @Override
    public Task getTask(long taskId) {
        Task task = tasks.get(taskId);
        historyManager.add(task);
        return task;
    }

    //Добавление новой задачи, эпика и подзадачи.
    @Override
    public void addTask(Task newTask) {
        newTask.setTaskStatus(State.NEW); //установить статус Новая
        tasks.put(newTask.getTaskId(), newTask); //добавить в список задач

        if (newTask instanceof SubTask) { //если это подзадача, то добавить к эпику
            setSubTasks((SubTask) newTask);
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
    protected void setTaskStatus(Task task, State newStatus) {
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
                historyManager.remove(subTask.getTaskId()); //удалить задачу из просмотров
            }
        }
        //удаление задачи из эпика, если переданный ID является подзадачей
        if (tasks.get(newTaskId) instanceof SubTask) {
            SubTask newSubTask = (SubTask) tasks.get(newTaskId);
            long subTaskDuration = newSubTask.getDuration().toMinutes();
            long epicId = newSubTask.getEpicId();
            Epic epic = (Epic) tasks.get(epicId);
            epic.setDuration(epic.getDuration().minusMinutes(subTaskDuration));
            removeSubTaskFromEpic(newTaskId);
        }
        tasks.remove(newTaskId); //удалить задачу
        historyManager.remove(newTaskId); //удалить задачу из просмотров
    }

    //Удаление подзадачи из эпика.
    private void removeSubTaskFromEpic(long oldSubTaskId) {
        long epicId = ((SubTask) tasks.get(oldSubTaskId)).getEpicId();
        ((Epic) tasks.get(epicId)).getSubTasks().remove(oldSubTaskId);
    }

    //Получение списка просмотренных задач.
    @Override
    public List<Task> getHistory() {

        return historyManager.getHistory();
    }

    //проверить есть ли задачи в таблице
    private boolean isAnyTasks() {
        return !tasks.isEmpty();
    }

    //Получить список задач в порядке приоритета
    private Set<Task> getPrioritizedTasks() {
        Comparator<Task> taskComparator = Comparator.comparing(Task::getStartTime);

        return new TreeSet<>(taskComparator);
    }

}
