package ru.yandex.practicum.tracker;

import java.util.ArrayList;
import java.util.HashMap;

//класс менеджера задач
public class TaskManager {
    private HashMap<Long, Task> tasks; //таблица всех задач

    public TaskManager() {
        tasks = new HashMap<>();
    }

    //Получение списка всех задач.
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
    public Task getTask(long taskId) {
        return tasks.get(taskId);
    }

    //Добавление новой задачи, эпика и подзадачи.
    public void addTask(Task newTask) {
        newTask.setTaskStatus(State.NEW); //установить статус Новая
        tasks.put(newTask.getTaskId(), newTask); //добавить в список задач

        if (newTask instanceof SubTask) { //если это подзадача, то добавить к эпику
            setSubTasks(((SubTask) newTask).getEpicId(), (SubTask) newTask);
        }
    }

    //Обновление задачи любого типа по идентификатору.
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
        for (SubTask subTask : subTasks) {
            //если нет выполненных подзадач то вернуть false
            if (!subTask.getTaskStatus().equals(State.DONE)) {
                return false;
            }
        }
        return true;
    }

    //Удаление ранее всех добавленных задач.
    public void removeTask() {
        if (isAnyTasks()) { //есть ли задачи
            tasks.clear(); //удалить все задачи
        }
    }

    //Удаление задачи по идентификатору.
    public void removeTask(long newTaskId) {
        //удаление подзадач если переданный ID является эпиком
        if (tasks.get(newTaskId) instanceof Epic) {
            ArrayList<SubTask> subTasks = getSubTasks(newTaskId); //список подзадач эпика
            for (SubTask subTask : subTasks) {
                tasks.remove(subTask.getTaskId()); //удалить задачу
            }
        }
        tasks.remove(newTaskId);
    }

    //проверить есть ли задачи в таблице
    private boolean isAnyTasks() {
        return !tasks.isEmpty();
    }
}
