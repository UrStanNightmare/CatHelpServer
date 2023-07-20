package ru.urstannightmare.cathelpserver.task.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.urstannightmare.cathelpserver.task.model.Task;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class TaskMapper implements RowMapper<Task> {

    @Override
    public Task mapRow(ResultSet rs, int rowNum) throws SQLException {

        var id = rs.getInt("id");
        var compDate = rs.getDate("comp_date");
        var desc = rs.getString("description");
        var isDone = rs.getBoolean("is_done");

        return new Task(id, compDate, desc, isDone);
    }
}
