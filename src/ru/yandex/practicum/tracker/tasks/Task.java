package ru.yandex.practicum.tracker.tasks;

import java.util.Objects;

//класс для задач
public class Task {
    private String taskName;
    private String taskDescription;
    private long taskId;
    private State taskStatus;

    public Task(String taskName,
                String taskDescription,
                long taskId,
                State taskStatus) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.taskId = taskId;
        this.taskStatus = taskStatus;
    }

    //задать имя задачи
    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    //задать описание задачи
    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    //задать статус задачи
    public void setTaskStatus(State taskStatus) {
        this.taskStatus = taskStatus;
    }

    //получить имя задачи
    public String getTaskName() {
        return taskName;
    }

    //получить описание задачи
    public String getTaskDescription() {
        return taskDescription;
    }

    //получить идентификатор задачи
    public long getTaskId() {
        return taskId;
    }

    //получить статус задачи
    public State getTaskStatus() {
        return taskStatus;
    }

    @Override
    public String toString() {
        return "Task{" +
                "taskName='" + taskName + '\'' +
                ", taskDescription='" + taskDescription + '\'' +
                ", taskId=" + taskId +
                ", taskStatus='" + taskStatus + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return taskId == task.taskId &&
                taskName.equals(task.taskName) &&
                taskDescription.equals(task.taskDescription) &&
                taskStatus.equals(task.taskStatus);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskName, taskDescription, taskId, taskStatus);
    }
}
