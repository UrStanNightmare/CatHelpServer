package ru.urstannightmare.cathelpserver.task.dto;

import jakarta.validation.constraints.NotNull;

import java.util.Date;
import java.util.Objects;

public class PostTaskRequest {
    @NotNull
    private Date date;
    @NotNull
    private String description;
    @NotNull
    private boolean isDone;

    public PostTaskRequest() {
    }

    public PostTaskRequest(Date date, String description, boolean isDone) {
        this.date = date;
        this.description = description;
        this.isDone = isDone;
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
        PostTaskRequest that = (PostTaskRequest) o;
        return isDone == that.isDone && Objects.equals(date, that.date) && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, description, isDone);
    }

    @Override
    public String toString() {
        return "PostTaskRequest{" +
                "date=" + date +
                ", description='" + description + '\'' +
                ", isDone=" + isDone +
                '}';
    }
}
