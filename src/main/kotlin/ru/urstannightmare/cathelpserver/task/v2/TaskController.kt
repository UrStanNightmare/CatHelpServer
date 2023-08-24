package ru.urstannightmare.cathelpserver.task.v2

import jakarta.validation.Valid
import jakarta.validation.constraints.Positive
import org.slf4j.LoggerFactory
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import ru.urstannightmare.cathelpserver.task.v2.dto.PostTaskRequest
import ru.urstannightmare.cathelpserver.task.v2.dto.StatusResponse
import ru.urstannightmare.cathelpserver.task.v2.dto.TasksResponse
import java.time.LocalDate

@Validated
@RequestMapping("/api/v2")
@RestController
class TaskController(val taskService: DefaultTaskService) {
    private val log = LoggerFactory.getLogger(this::class.java)
    @GetMapping(value = ["/health"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun healthCheck(): ResponseEntity<StatusResponse> {
        log.info("Health check.")

        return ResponseEntity(StatusResponse("Alive"), HttpStatus.OK)
    }

    @GetMapping(value = ["/tasks"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getTasks(
        @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam @Valid date:  LocalDate
    ): ResponseEntity<TasksResponse> {
        log.info("GetTasks request for date {}.", date)

        val tasksData = taskService.getTasksByDate(date)

        return ResponseEntity(tasksData, HttpStatus.OK)
    }

    @PostMapping(value = ["/task"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun postTask(
        @RequestBody @Valid task: PostTaskRequest
    ): ResponseEntity<TasksResponse> {
        log.info("Add task request. {}", task.date.toString())

        val result = taskService.addTask(task)

        return ResponseEntity(result, HttpStatus.OK)
    }

    @PostMapping(value = ["/task/{id}/status"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun updateTaskDoneState(
        @PathVariable @Valid @Positive id: Long
    ): ResponseEntity<StatusResponse> {
        log.info("Change task {} done status", id)

        val result = taskService.updateDoneState(id)

        return ResponseEntity(StatusResponse(result.toString()), HttpStatus.OK)
    }

    @DeleteMapping(value = ["/task/{id}/"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun deleteTask(
        @PathVariable @Valid @Positive id: Long
    ): ResponseEntity<StatusResponse> {

        log.info("Delete task {} request.", id)

        val result = taskService.deleteTask(id)

        return ResponseEntity(StatusResponse(result), HttpStatus.OK)
    }
}