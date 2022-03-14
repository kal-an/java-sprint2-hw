package ru.yandex.practicum.tracker.tasks;

import java.time.Duration;
import java.time.LocalDateTime;

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

    @Override
    public String toString() {
        return "SubTask{" +
                "taskName='" + this.getTaskName() + '\'' +
                ", taskDescription='" + this.getTaskDescription() + '\'' +
                ", taskId=" + this.getTaskId() +
                ", taskStatus='" + this.getTaskStatus() + '\'' +
                ", duration='" + this.getDuration().toMinutes() + '\'' +
                ", startTime='" + this.getStartTime() + '\'';
    }

    public long getEpicId() {
        return epicId;
    }
}
