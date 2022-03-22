package ru.yandex.practicum.tracker.manager;

import ru.yandex.practicum.tracker.exceptions.ManagerTaskException;
import ru.yandex.practicum.tracker.manager.history.HistoryManager;
import ru.yandex.practicum.tracker.manager.history.InMemoryHistoryManager;
import ru.yandex.practicum.tracker.tasks.Epic;
import ru.yandex.practicum.tracker.tasks.State;
import ru.yandex.practicum.tracker.tasks.SubTask;
import ru.yandex.practicum.tracker.tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTasksManager implements TaskManager {
    protected static HashMap<Long, Task> tasks = null; //таблица всех задач
    protected HistoryManager historyManager = new InMemoryHistoryManager(); //менеджер истории
    private Set<Task> sortedTasks; //сортированные задачи

    public InMemoryTasksManager() {
        tasks = new HashMap<>();
        Comparator<Task> taskComparator = Comparator.comparing(Task::getStartTime);
        sortedTasks = new TreeSet<>(taskComparator);
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
        if (tasks.containsKey(epicId)) {
            Epic epic = (Epic) tasks.get(epicId);
            for (Long taskId : epic.getSubTasks()) {
                subTasks.add((SubTask) tasks.get(taskId));
            }
        }

        return subTasks;
    }

    //Получение задачи любого типа по идентификатору.
    @Override
    public Task getTask(long taskId) {
        Task task = tasks.get(taskId);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    //Добавление новой задачи, эпика и подзадачи.
    @Override
    public void addTask(Task newTask) {
        if (newTask != null) {
            if (!isAnyTasks()) { //если никаких задач еще нет
                sortedTasks.add(newTask); //добавить задачу в сортированное множество
                tasks.put(newTask.getTaskId(), newTask); //добавить в список задач
            } else {
                if (isAnyTaskIntersections(newTask)) { //если есть пересечения
                    throw new ManagerTaskException("Невозможно запланировать задачу на это время");
                }
                if (!(newTask instanceof Epic)) { //не учитываем эпики
                    sortedTasks.add(newTask); //добавить задачу в сортированное множество
                }
                if (newTask instanceof SubTask) { //если это подзадача, то добавить к эпику
                    long epicId = ((SubTask) newTask).getEpicId(); //найти нужный эпик
                    Epic epic = (Epic) tasks.get(epicId);
                    epic.setSubTasks(newTask.getTaskId()); //добавить подзадачу к эпику
                    long subTaskDuration = newTask.getDuration().toMinutes();
                    //если время старта эпика позже чем новая подзадача
                    if (epic.getStartTime().isAfter(newTask.getStartTime())) {
                        //установить время по подзадаче
                        epic.setStartTime(newTask.getStartTime());
                    }
                    //увеличить продолжительность эпика
                    epic.setDuration(epic.getDuration().plusMinutes(subTaskDuration));
                }
                tasks.put(newTask.getTaskId(), newTask); //добавить в список задач
            }
        }
    }

    //Обновление задачи любого типа по идентификатору.
    @Override
    public void updateTask(Task newTask) {
        if (newTask != null) { //если задача не пустая
            State newStatus = newTask.getTaskStatus();
            tasks.get(newTask.getTaskId()).setTaskStatus(newStatus); //установить новый статус

            if (newTask instanceof SubTask) { //если это подзадача
                long epicId = ((SubTask) newTask).getEpicId(); //найти id эпика
                Epic epic = (Epic) tasks.get(epicId);
                if (isAllSubTaskInEpicDone(epic)) {
                    //если все подзадачи готовы, то эпик тоже готов
                    tasks.get(epicId).setTaskStatus(State.DONE);
                } else { //если есть задачи в процессе выполнения, то эпик на выполнении
                    tasks.get(epicId).setTaskStatus(State.IN_PROGRESS);
                }
            }
        }
    }

    //Проверить есть ли задача на это время
    private boolean isAnyTaskIntersections(Task newTask) {
        long newTaskDuration = newTask.getDuration().toMinutes(); //продолжительность новой задачи
        LocalDateTime newTaskStartTime = newTask.getStartTime(); //время старта новой задачи
        LocalDateTime newTaskFinishTime = newTaskStartTime
                .plusMinutes(newTaskDuration); //время финиша новой задачи
        for (Task task : sortedTasks) {
            long taskDuration = task.getDuration().toMinutes(); //продолжительность задачи
            LocalDateTime taskStartTime = task.getStartTime(); //время старта задачи
            LocalDateTime taskFinishTime = taskStartTime
                    .plusMinutes(taskDuration); //время финиша задачи

            //если старт или финиш новой задачи
            // пересекается с текущей задаче по врени старта или ее окончания
            if (newTaskFinishTime.isAfter(taskStartTime)
                    && newTaskStartTime.isBefore(taskStartTime)
                    || newTaskStartTime.isBefore(taskFinishTime)
                    && newTaskFinishTime.isAfter(taskFinishTime)) {
                return true;
            }
        }
        return false;
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
        historyManager.clear();
    }

    //Удаление задачи по идентификатору.
    @Override
    public void removeTask(long newTaskId) {
        Task task = tasks.getOrDefault(newTaskId, null);
        if (task != null) {
            //удаление подзадач если переданный ID является эпиком
            if (tasks.get(newTaskId) instanceof Epic) {
                ArrayList<SubTask> subTasks = getSubTasks(newTaskId); //список подзадач эпика
                for (Task subTask : subTasks) {
                    tasks.remove(subTask.getTaskId()); //удалить задачу
                    historyManager.remove(subTask.getTaskId()); //удалить задачу из просмотров
                }
            }
            //удаление подзадачи из эпика
            if (tasks.get(newTaskId) instanceof SubTask) {
                SubTask newSubTask = (SubTask) tasks.get(newTaskId);
                long subTaskDuration = newSubTask.getDuration().toMinutes();
                long epicId = newSubTask.getEpicId();
                Epic epic = (Epic) tasks.get(epicId);
                epic.setDuration(epic.getDuration().minusMinutes(subTaskDuration));
                epic.getSubTasks().remove(newTaskId);
            }
            tasks.remove(newTaskId); //удалить задачу
            historyManager.remove(newTaskId); //удалить задачу из просмотров
        } else {
            throw new ManagerTaskException("Задачи с таким ID не найдено");
        }

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

    @Override
    public Set<Task> getPrioritizedTasks() {
        return sortedTasks;
    }

}
