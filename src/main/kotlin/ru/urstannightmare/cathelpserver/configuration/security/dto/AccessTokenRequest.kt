package ru.urstannightmare.cathelpserver.configuration.security.dto

import jakarta.validation.constraints.NotNull

data class AccessTokenRequest(@NotNull val refreshToken: String)
