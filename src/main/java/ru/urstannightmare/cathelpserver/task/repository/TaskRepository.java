package ru.urstannightmare.cathelpserver.task.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.urstannightmare.cathelpserver.task.model.Task;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static ru.urstannightmare.cathelpserver.task.repository.DefaultQueries.*;

@Repository
public class TaskRepository implements DefaultTaskRepository {
    private static final Logger log = LoggerFactory.getLogger(TaskRepository.class);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Task> taskRowMapper;

    @Autowired
    public TaskRepository(JdbcTemplate jdbcTemplate, RowMapper<Task> taskRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.taskRowMapper = taskRowMapper;
    }

    @Override
    public Optional<Task> findById(long id) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(
                    GET_TASK_BY_ID,
                    taskRowMapper,
                    id));

        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Task updateTask(Task task) {
        jdbcTemplate.update(UPDATE_TASK_BY_ID,
                task.getDate(),
                task.getDescription(),
                task.isDone(),
                task.getId());
        return task;
    }

    @Override
    public Optional<List<Task>> getTasksByStartAndEndDate(Date start, Date end) {
        try {

            var data = jdbcTemplate.query(
                    GET_ALL_TASKS_BY_START_AND_END_DATE_QUERY,
                    taskRowMapper,
                    dateFormat.format(start),
                    dateFormat.format(end)
            );

            if (!data.isEmpty()) {
                log.info("Got {} tasks.", data.size());
                return Optional.of(data);
            }

        } catch (DataAccessException e) {
            log.error("Can't access tasks! {}", e.getMessage(), e);
            throw new DataRetrievalFailureException("Can't get tasks", e);
        }

        return Optional.empty();
    }

    @Override
    public Optional<Task> addTask(Date date, String description, boolean isDone) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("tasks")
                .usingGeneratedKeyColumns("ID");

        var args = Map.of(
                "COMP_DATE", dateFormat.format(date),
                "DESCRIPTION", description,
                "IS_DONE", isDone
        );

        Number taskId = simpleJdbcInsert.executeAndReturnKey(args);

        if (taskId != null) {
            log.info("Task {} added.", taskId.intValue());
            return Optional.of(new Task(taskId.intValue(), date, description, isDone));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public boolean deleteTaskById(long id) {
        return jdbcTemplate.update(DELETE_TASK_BY_ID_QUERY, id) == 1;
    }
}
