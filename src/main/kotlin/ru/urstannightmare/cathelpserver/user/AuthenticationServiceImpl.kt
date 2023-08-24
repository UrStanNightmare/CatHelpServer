package ru.urstannightmare.cathelpserver.user

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import ru.urstannightmare.cathelpserver.configuration.security.AuthException
import ru.urstannightmare.cathelpserver.configuration.security.JwtCore
import ru.urstannightmare.cathelpserver.configuration.security.dto.AccessTokenRequest
import ru.urstannightmare.cathelpserver.configuration.security.dto.SignInRequest
import ru.urstannightmare.cathelpserver.configuration.security.dto.TokenResponse
import ru.urstannightmare.cathelpserver.task.v2.dto.StatusResponse

@Service
class AuthenticationServiceImpl(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val authenticationManager: AuthenticationManager,
    private val jwtCore: JwtCore
) : AuthenticationService {
    private val log = LoggerFactory.getLogger(AuthenticationServiceImpl::class.java)
    override fun signIn(request: SignInRequest): TokenResponse {
        try {
            val auth = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(
                    request.username,
                    request.password
                )
            )
            val up = auth.principal as UserDetailsImpl

            val accessToken = jwtCore.generateAccessToken(auth)
            val refreshToken = jwtCore.generateRefreshToken(auth)


            userRepository.updateRefreshTokenById(up.getId(), refreshToken)

            return TokenResponse(accessToken, refreshToken)
        } catch (e: Exception) {
            log.warn("{}", e.message)
            throw AuthException(e.message)
        }
    }


    override fun getAccessToken(req: AccessTokenRequest): TokenResponse {
        if (jwtCore.validateRefreshToken(req.refreshToken)) {
            val claims = jwtCore.getRefreshClaims(req.refreshToken)
            val username = claims.subject
            val user = userRepository.findByUsername(username).orElseThrow { AuthException("User not found") }
            if ((user.refreshToken != null) && (user.refreshToken!! == req.refreshToken)) {
                val auth = AnonymousAuthenticationToken("refresh", build(user),
                    listOf(SimpleGrantedAuthority("ANONYMOUS")))

                var at = jwtCore.generateAccessToken(auth)
                var rt = jwtCore.generateRefreshToken(auth)

                userRepository.updateRefreshTokenById(user.id!!, rt)

                return TokenResponse(at, rt)
            }else{
                throw AuthException("Bad refresh token")
            }
        }

        throw AuthException("Something bad happened.")
    }

    fun signUp(request: SignInRequest): ResponseEntity<Any> {
        if (userRepository.existsByUsername(request.username)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(StatusResponse("Specify different username!"))
        }

        var user = User(request.username, passwordEncoder.encode(request.password))
        userRepository.save(user)

        return ResponseEntity.ok(StatusResponse("User created"))
    }
}