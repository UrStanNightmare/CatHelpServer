package ru.urstannightmare.cathelpserver.task.dto;

import ru.urstannightmare.cathelpserver.task.model.Task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class TasksResponse {
    private List<Task> tasks;
    private LocalDate generatedDate;

    public TasksResponse(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }
    public TasksResponse() {
        this.tasks = new LinkedList<>();
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    public LocalDate getGeneratedDate() {
        return generatedDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TasksResponse that = (TasksResponse) o;
        return Objects.equals(tasks, that.tasks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tasks);
    }

    @Override
    public String toString() {
        return "TasksResponse{" +
                "tasks=" + tasks +
                ", generatedDate=" + generatedDate +
                '}';
    }

    public void setGeneratedDate() {
        this.generatedDate = LocalDate.now();
    }
}
