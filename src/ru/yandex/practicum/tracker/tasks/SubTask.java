package ru.yandex.practicum.tracker.tasks;

//класс для подзадач
public class SubTask extends Task{
    private long epicId;

    public SubTask(String taskName,
                   String taskDescription,
                   long taskId,
                   State taskStatus,
                   long epicId) {
        super(taskName, taskDescription, taskId, taskStatus);
        this.epicId = epicId;
    }

    public long getEpicId() {
        return epicId;
    }
}
