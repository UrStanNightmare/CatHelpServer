package ru.urstannightmare.cathelpserver.task.repository;

public class DefaultQueries {
    public static final String GET_TASK_BY_ID =
            "SELECT ID, COMP_DATE, DESCRIPTION, IS_DONE " +
                    "FROM tasks " +
                    "WHERE ID = ?;";

    public static final String UPDATE_TASK_BY_ID =
            "UPDATE tasks " +
                    "SET COMP_DATE = ?, " +
                    "DESCRIPTION = ?, " +
                    "IS_DONE = ? " +
                    "WHERE ID = ?;" ;
    public static final String GET_ALL_TASKS_BY_START_AND_END_DATE_QUERY =
            "SELECT ID, COMP_DATE, DESCRIPTION, IS_DONE " +
            "FROM tasks " +
            "WHERE comp_date BETWEEN ? AND ? " +
            "ORDER BY ID;";

    public static final String DELETE_TASK_BY_ID_QUERY =
            "DELETE FROM tasks WHERE ID = ?;";
    private DefaultQueries(){}
}
