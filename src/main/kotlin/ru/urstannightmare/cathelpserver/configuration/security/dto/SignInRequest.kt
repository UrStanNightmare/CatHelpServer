package ru.urstannightmare.cathelpserver.configuration.security.dto

data class SignInRequest(
    val username: String,
    val password: String
)
