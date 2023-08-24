package ru.urstannightmare.cathelpserver.user

import ru.urstannightmare.cathelpserver.configuration.security.dto.AccessTokenRequest
import ru.urstannightmare.cathelpserver.configuration.security.dto.SignInRequest
import ru.urstannightmare.cathelpserver.configuration.security.dto.TokenResponse

interface AuthenticationService {
    fun signIn(request: SignInRequest) : TokenResponse
    fun getAccessToken(req: AccessTokenRequest): TokenResponse
}