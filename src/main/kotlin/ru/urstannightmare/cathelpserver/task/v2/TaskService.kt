package ru.urstannightmare.cathelpserver.task.v2

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.urstannightmare.cathelpserver.task.TaskNotFoundException
import ru.urstannightmare.cathelpserver.task.v2.dto.PostTaskRequest
import ru.urstannightmare.cathelpserver.task.v2.dto.TasksResponse
import ru.urstannightmare.cathelpserver.task.v2.entity.Task
import ru.urstannightmare.cathelpserver.utils.countDateIntervalForSpecifiedDate
import java.time.LocalDate

@Service
class TaskService(val taskRepository: TaskRepository) : DefaultTaskService {
    val log = LoggerFactory.getLogger(this::class.java)
    override fun getTasksByDate(date: LocalDate): TasksResponse {
        val dateInterval = countDateIntervalForSpecifiedDate(date)

        val tasksByStartAndEndDate = taskRepository
            .getTasksByStartAndEndDate(dateInterval.startDate, dateInterval.endDate)

        return if (tasksByStartAndEndDate.isPresent) {
            TasksResponse(tasksByStartAndEndDate.get())
        } else {
            TasksResponse(listOf())
        }
    }

    override fun addTask(taskDto: PostTaskRequest): TasksResponse {
        var newTask = Task(taskDto.date, taskDto.description, taskDto.isDone)
        try {
            newTask = taskRepository.save(newTask)
        } catch (e: Exception) {
            log.error("Error happened {}", e.message, e)
        }


        return TasksResponse(listOf(newTask))
    }

    override fun updateDoneState(id: Long): Int {
        val taskOptional = taskRepository.findById(id)
        if (taskOptional.isEmpty) {
            throw TaskNotFoundException("Can't find task with id $id")
        }

        val task = taskOptional.get()

        return taskRepository.updateIsDoneById(!task.isDone!!, task.id!!)
    }

    override fun deleteTask(id: Long): String {
        val result = taskRepository.deleteByIdWithResult(id);
        return if (result > 0) {
            log.info("Deleted")
            "Deleted"
        } else {
            log.info("Not deleted")
            "Nothing"
        }
    }
}