package ru.urstannightmare.cathelpserver.user

import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.urstannightmare.cathelpserver.configuration.security.dto.AccessTokenRequest
import ru.urstannightmare.cathelpserver.configuration.security.dto.SignInRequest
import ru.urstannightmare.cathelpserver.configuration.security.dto.TokenResponse

@RestController
@RequestMapping("/auth")
class SecurityController(val authService: AuthenticationService) {
    private val log = LoggerFactory.getLogger(SecurityController::class.java)

    @PostMapping("/signin")
    fun signIn(
        @RequestBody @Valid request: SignInRequest
    ): ResponseEntity<TokenResponse> {
        log.info("SignIn request")
        val tokenDto = authService.signIn(request)

        return ResponseEntity.ok().body(tokenDto)
    }

    @PostMapping("/token")
    fun recreateTokens(
        @RequestBody @Valid request: AccessTokenRequest
    ): ResponseEntity<TokenResponse> {
        log.info("Recreate tokens request")
        val tokenDto = authService.getAccessToken(request)

        return ResponseEntity.ok().body(tokenDto)
    }
}