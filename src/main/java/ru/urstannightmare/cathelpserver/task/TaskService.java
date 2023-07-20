package ru.urstannightmare.cathelpserver.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Service;
import ru.urstannightmare.cathelpserver.task.dto.PostTaskRequest;
import ru.urstannightmare.cathelpserver.task.dto.TasksResponse;
import ru.urstannightmare.cathelpserver.task.model.Task;
import ru.urstannightmare.cathelpserver.task.repository.DefaultTaskRepository;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService implements DefaultTaskService {
    private static final Logger log = LoggerFactory.getLogger(TaskService.class);

    private final DefaultTaskRepository taskRepository;

    @Autowired
    public TaskService(DefaultTaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public TasksResponse getTasksByDate(LocalDate date) {
        var calendar = new GregorianCalendar();
        resetCalendar(calendar, date);

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        var prevDays = countAdditionalDaysInPreviousMonth(calendar.get(Calendar.DAY_OF_WEEK));
        calendar.add(Calendar.DAY_OF_MONTH, -prevDays);

        var startDate = calendar.getTime();

        resetCalendar(calendar, date);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        var pastDays = countAdditionalDaysInNextMonth(calendar.get(Calendar.DAY_OF_WEEK));
        calendar.add(Calendar.DAY_OF_MONTH, pastDays);

        var endDate = calendar.getTime();

        Optional<List<Task>> tasksByStartAndEndDate = taskRepository.getTasksByStartAndEndDate(startDate, endDate);

        var response = new TasksResponse();
        tasksByStartAndEndDate.ifPresent(response::setTasks);
        response.setGeneratedDate();

        return response;
    }

    @Override
    public Task addTask(PostTaskRequest taskDto) {
        var result = taskRepository.addTask(
                taskDto.getDate(),
                taskDto.getDescription(),
                taskDto.isDone());
        return result.orElseThrow(() -> new IncorrectResultSizeDataAccessException(1, 0));
    }

    @Override
    public Task updateDoneState(long id) {
        var taskOptional = taskRepository.findById(id);

        if (taskOptional.isEmpty()){
            throw new TaskNotFoundException("Can't find task with id " + id);
        }

        var task = taskOptional.get();
        task.setDone(!task.isDone());

        return taskRepository.updateTask(task);
    }

    @Override
    public String deleteTask(long id) {
        var result = taskRepository.deleteTaskById(id);

        if (result) {
            log.info("Deleted.");
            return "Ok";
        } else {
            log.info("Not deleted.");
            return "Nothing deleted";
        }
    }

    @Override
    public int countAdditionalDaysInPreviousMonth(int dayOfWeek) {

        if (dayOfWeek == Calendar.SUNDAY) {
            return 6;
        }

        if (dayOfWeek == Calendar.MONDAY) {
            return 0;
        }

        return dayOfWeek - 2;
    }

    @Override
    public int countAdditionalDaysInNextMonth(int dayOfWeek) {
        if (dayOfWeek == Calendar.MONDAY) {
            return 6;
        }

        if (dayOfWeek == Calendar.SUNDAY) {
            return 0;
        }

        return 8 - dayOfWeek;
    }

    @Override
    public void resetCalendar(Calendar calendar, LocalDate date) {
        calendar.set(Calendar.YEAR, date.getYear());
        calendar.set(Calendar.MONTH, date.getMonthValue() - 1);
        calendar.set(Calendar.DAY_OF_MONTH, date.getDayOfMonth());
    }
}
