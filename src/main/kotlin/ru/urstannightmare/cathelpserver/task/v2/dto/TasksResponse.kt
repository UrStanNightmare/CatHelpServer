package ru.urstannightmare.cathelpserver.task.v2.dto

import java.time.LocalDate

data class TasksResponse(val tasks: List<ru.urstannightmare.cathelpserver.task.v2.entity.Task>){
    var generatedDate: LocalDate = LocalDate.now()
}
