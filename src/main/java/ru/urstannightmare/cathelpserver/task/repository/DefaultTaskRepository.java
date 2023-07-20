package ru.urstannightmare.cathelpserver.task.repository;

import ru.urstannightmare.cathelpserver.task.model.Task;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface DefaultTaskRepository {
    Optional<Task> findById(long id);
    Task updateTask(Task task);
    Optional<List<Task>> getTasksByStartAndEndDate(Date start, Date end);

    Optional<Task> addTask(Date date, String description, boolean isDone);

    boolean deleteTaskById(long id);
}
