package ru.urstannightmare.cathelpserver.task;

import ru.urstannightmare.cathelpserver.task.dto.PostTaskRequest;
import ru.urstannightmare.cathelpserver.task.dto.TasksResponse;
import ru.urstannightmare.cathelpserver.task.model.Task;

import java.time.LocalDate;
import java.util.Calendar;

public interface DefaultTaskService {
    TasksResponse getTasksByDate(LocalDate date);
    Task addTask(PostTaskRequest taskDto);
    Task updateDoneState(long id);
    String deleteTask(long id);
    int countAdditionalDaysInPreviousMonth(int dayOfWeek);
    int countAdditionalDaysInNextMonth(int dayOfWeek);

    void resetCalendar(Calendar calendar, LocalDate date);
}
