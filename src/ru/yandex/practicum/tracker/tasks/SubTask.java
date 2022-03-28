package ru.yandex.practicum.tracker.tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

//класс для подзадач
public class SubTask extends Task{
    private long epicId;

    public SubTask(String taskName,
                   String taskDescription,
                   long taskId,
                   State taskStatus,
                   long epicId,
                   Duration duration,
                   LocalDateTime startTime) {
        super(taskName, taskDescription, taskId, taskStatus, duration, startTime);
        this.epicId = epicId;
    }

    public SubTask(String taskName,
                   String taskDescription,
                   State taskStatus,
                   long epicId,
                   Duration duration,
                   LocalDateTime startTime) {
        super(taskName, taskDescription, taskStatus, duration, startTime);
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "taskName='" + this.getTaskName() + '\'' +
                ", taskDescription='" + this.getTaskDescription() + '\'' +
                ", taskId=" + this.getTaskId() +
                ", taskStatus='" + this.getTaskStatus() + '\'' +
                ", duration='" + this.getDuration().toMinutes() + '\'' +
                ", startTime='" + this.getStartTime() + '\'' +
                ", epicId='" + this.getEpicId() + '\'';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return this.getTaskId() == task.getTaskId() &&
                this.getTaskName().equals(task.getTaskName()) &&
                this.getTaskDescription().equals(task.getTaskDescription()) &&
                this.getTaskStatus().equals(task.getTaskStatus()) &&
                this.getStartTime().equals(task.getStartTime());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getTaskName(),
                this.getTaskDescription(),
                this.getTaskId(),
                this.getTaskStatus(),
                this.getStartTime());
    }

    public long getEpicId() {
        return epicId;
    }
}
