package ru.urstannightmare.cathelpserver.task.v2

import ru.urstannightmare.cathelpserver.task.v2.dto.PostTaskRequest
import ru.urstannightmare.cathelpserver.task.v2.dto.TasksResponse
import java.time.LocalDate

interface DefaultTaskService {
    fun getTasksByDate(date: LocalDate): TasksResponse
    fun addTask(taskDto: PostTaskRequest): TasksResponse
    fun updateDoneState(id: Long): Int
    fun deleteTask(id: Long): String
}