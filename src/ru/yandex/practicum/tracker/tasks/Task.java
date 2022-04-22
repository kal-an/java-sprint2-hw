package ru.yandex.practicum.tracker.tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

// Класс для задач
public class Task {
    private String taskName;
    private String taskDescription;
    private long taskId;
    private State taskStatus;
    private Duration duration;
    private LocalDateTime startTime;

    public Task(String taskName,
                String taskDescription,
                State taskStatus,
                Duration duration,
                LocalDateTime startTime) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.taskStatus = taskStatus;
        this.duration = duration;
        this.startTime = startTime;
    }

    // Задать ID задачи
    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    // Задать имя задачи
    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    // Задать описание задачи
    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    // Задать статус задачи
    public void setTaskStatus(State taskStatus) {
        this.taskStatus = taskStatus;
    }

    // Получить имя задачи
    public String getTaskName() {
        return taskName;
    }

    // Получить описание задачи
    public String getTaskDescription() {
        return taskDescription;
    }

    // Получить идентификатор задачи
    public long getTaskId() {
        return taskId;
    }

    // Получить статус задачи
    public State getTaskStatus() {
        return taskStatus;
    }

    // Получить продолжительность задачи
    public Duration getDuration() {
        return duration;
    }

    // Задать продолжительность задачи
    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    // Хадать время старта задачи
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    // получить дату начала выполнения
    public LocalDateTime getStartTime() {
        return startTime;
    }

    @Override
    public String toString() {
        return "Task{" +
                "taskName='" + taskName + '\'' +
                ", taskDescription='" + taskDescription + '\'' +
                ", taskId=" + taskId +
                ", taskStatus=" + taskStatus +
                ", duration=" + duration.toMinutes() +
                ", startTime=" + startTime +
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
                taskStatus.equals(task.taskStatus) &&
                startTime.equals(task.startTime);
    }


    @Override
    public int hashCode() {
        return Objects.hash(taskName, taskDescription, taskId, taskStatus, startTime);
    }
}
