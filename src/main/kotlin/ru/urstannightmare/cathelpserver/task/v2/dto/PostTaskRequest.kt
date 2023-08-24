package ru.urstannightmare.cathelpserver.task.v2.dto

import jakarta.validation.constraints.NotNull
import java.util.*

data class PostTaskRequest(@NotNull val date: Date, @NotNull val description: String, val isDone: Boolean = false)
