package ru.urstannightmare.cathelpserver.task.v2.dto

data class ErrorResponse(val code: Int, val details: String, val path: String = "")