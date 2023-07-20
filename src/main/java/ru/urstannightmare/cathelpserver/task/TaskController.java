package ru.urstannightmare.cathelpserver.task;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.urstannightmare.cathelpserver.task.dto.PostTaskRequest;
import ru.urstannightmare.cathelpserver.task.dto.TasksResponse;
import ru.urstannightmare.cathelpserver.task.model.Task;

import java.time.LocalDate;

@RestController
@Validated
@RequestMapping("/api/v1")
public class TaskController {
    private static final Logger log = LoggerFactory.getLogger(TaskController.class);

    private final DefaultTaskService taskService;

    @Autowired
    public TaskController(DefaultTaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping(value = "/health", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> healthCheck() {
        log.info("Health check.");
        return new ResponseEntity<>("Alive", HttpStatus.OK);
    }

    @GetMapping(value = "/tasks", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TasksResponse> getTasks(
            @Valid
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            @RequestParam
            LocalDate date) {
        log.info("GetTasks request for date {}.", date);

        var tasksData = taskService.getTasksByDate(date);

        return new ResponseEntity<>(tasksData, HttpStatus.OK);
    }

    @PostMapping(value = "/task", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Task> postTask(
            @Valid
            @RequestBody PostTaskRequest task) {
        log.info("Add task request. {}", task.getDate().toString());
        var result = taskService.addTask(task);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping(value = "/task/{id}/changeDoneState", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Task> updateTaskDoneState(
            @Valid
            @Positive
            @PathVariable long id
    ) {
        log.info("Change task {} done status", id);

        var result = taskService.updateDoneState(id);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @DeleteMapping(value = "/task/{id}/delete", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> deleteTask(
            @Valid
            @Positive
            @PathVariable long id
    ) {
        log.info("Delete task {} request.", id);

        var result = taskService.deleteTask(id);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
