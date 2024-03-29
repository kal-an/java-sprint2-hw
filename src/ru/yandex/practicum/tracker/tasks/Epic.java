package ru.yandex.practicum.tracker.tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

// Класс для крупных задач
public class Epic extends Task{
    private ArrayList<Long> subTasks= new ArrayList<>(); // Список id подзадач эпика

    public Epic(String taskName,
                String taskDescription) {
        super(taskName, taskDescription, Duration.ZERO, LocalDateTime.now());
    }

    public ArrayList<Long> getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(long subTaskId) {
        subTasks.add(subTaskId);
    }

    @Override
    public String toString() {
        String result = "Epic{" +
                "taskName='" + this.getTaskName() + '\'' +
                ", taskDescription='" + this.getTaskDescription() + '\'' +
                ", taskId=" + this.getTaskId() +
                ", taskStatus='" + this.getTaskStatus() + '\'' +
                ", duration='" + this.getDuration().toMinutes() + '\'' +
                ", startTime='" + this.getStartTime() + '\'';
        if (this.getSubTasks() != null) {
            result = result + ", subTasks=" + subTasks.toString() + '}';
        } else {
            result = result + ", subTasks=" + null + '}';
        }

        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(this.getTaskId(), task.getTaskId()) &&
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
}
