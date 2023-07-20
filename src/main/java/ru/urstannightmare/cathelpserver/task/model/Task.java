package ru.urstannightmare.cathelpserver.task.model;

import jakarta.validation.constraints.NotNull;

import java.util.Date;
import java.util.Objects;

public class Task {
    @NotNull
    private Integer id;
    @NotNull
    private Date date;
    @NotNull
    private String description;
    @NotNull
    private boolean isDone;

    public Task(Integer id, Date date, String description, boolean isDone) {
        this.id = id;
        this.date = date;
        this.description = description;
        this.isDone = isDone;
    }

    public Task() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return isDone == task.isDone && Objects.equals(id, task.id) && Objects.equals(date, task.date) && Objects.equals(description, task.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, date, description, isDone);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", date=" + date +
                ", description='" + description + '\'' +
                ", isDone=" + isDone +
                '}';
    }
}
