package ru.yandex.practicum.tracker;

import java.util.ArrayList;

//класс для крупных задач
public class Epic extends Task{
    private ArrayList<Long> subTasks= new ArrayList<>(); //список id подзадач эпика

    public Epic(String taskName,
                String taskDescription,
                long taskId,
                State taskStatus) {
        super(taskName, taskDescription, taskId, taskStatus);
    }

    public ArrayList<Long> getSubTasks() {
        return subTasks;
    }

    @Override
    public String toString() {
        String result = "Epic{" +
                "taskName='" + this.getTaskName() + '\'' +
                ", taskDescription='" + this.getTaskDescription() + '\'' +
                ", taskId=" + this.getTaskId() +
                ", taskStatus='" + this.getTaskStatus() + '\'';
        if (this.getSubTasks() != null) {
            result = result + ", subTasks=" + subTasks.toString() + '}';
        } else {
            result = result + ", subTasks=" + null + '}';
        }

        return result;
    }
}
