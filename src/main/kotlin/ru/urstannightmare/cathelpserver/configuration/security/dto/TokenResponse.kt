package ru.urstannightmare.cathelpserver.configuration.security.dto

data class TokenResponse(
    val accessToken: String,
    val refreshToken: String,
    val type :String = "Bearer"
)
