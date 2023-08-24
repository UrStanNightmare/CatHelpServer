package ru.urstannightmare.cathelpserver.configuration.security

import io.jsonwebtoken.*
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import ru.urstannightmare.cathelpserver.user.UserDetailsImpl
import java.security.Key
import java.security.SignatureException
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import javax.crypto.SecretKey


@Component
class JwtCore {
    private final val log = LoggerFactory.getLogger(JwtCore::class.java)

    final val jwtAccessSecret: SecretKey
    final val jwtRefreshSecret: SecretKey
    final val accessExpirationTime: Long
    final val refreshExpirationTime: Long?
    constructor(
        @Value("\${spring.security.secret.access}") jwtAccessSecret: String,
        @Value("\${spring.security.secret.refresh}") jwtRefreshSecret: String,
        @Value("\${spring.security.expiration.access}") accessExpirationTime: Long,
        @Value("\${spring.security.expiration.refresh}") refreshExpirationTime: String,
    ) {
        this.jwtAccessSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtAccessSecret))
        this.jwtRefreshSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtRefreshSecret))
        this.accessExpirationTime = accessExpirationTime
        this.refreshExpirationTime = if ("null" != refreshExpirationTime) {
            refreshExpirationTime.toLong()
        } else {
            null
        }
    }

    fun generateAccessToken(authentication: Authentication): String {
        val userDetails = authentication.principal as UserDetailsImpl
        var accessExpirationInstant: Instant = LocalDateTime.now().plusSeconds(accessExpirationTime)
            .atZone(ZoneId.systemDefault()).toInstant()
        var accessExpiration = Date.from(accessExpirationInstant)
        return Jwts.builder()
            .setSubject(userDetails.username)
            .setExpiration(accessExpiration)
            .signWith(jwtAccessSecret)
            .setIssuedAt(Date())
            .compact()
    }

    fun generateRefreshToken(authentication: Authentication): String {
        val userDetails = authentication.principal as UserDetailsImpl

        var refreshExpiration: Date? = if (refreshExpirationTime != null) {
            Date.from(
                LocalDateTime.now().plusSeconds(refreshExpirationTime)
                    .atZone(ZoneId.systemDefault()).toInstant()
            )
        } else {
            null
        }
        return Jwts.builder()
            .setSubject(userDetails.username)
            .setExpiration(refreshExpiration)
            .signWith(jwtRefreshSecret)
            .setIssuedAt(Date())
            .compact()
    }

    fun validateAccessToken(accessToken: String): Boolean {
        return validateToken(accessToken, jwtAccessSecret)
    }

    fun validateRefreshToken(refreshToken: String): Boolean {
        return validateToken(refreshToken, jwtRefreshSecret)
    }

    private fun validateToken(token: String, secret: Key): Boolean {
        try {
            Jwts.parserBuilder()
                .setSigningKey(secret)
                .build()
                .parseClaimsJws(token)
            return true
        } catch (expEx: ExpiredJwtException) {
            log.error("Token expired")
        } catch (unsEx: UnsupportedJwtException) {
            log.error("Unsupported jwt")
        } catch (mjEx: MalformedJwtException) {
            log.error("Malformed jwt")
        } catch (sEx: SignatureException) {
            log.error("Invalid signature")
        } catch (e: Exception) {
            log.error("invalid token")
        }
        return false
    }

    fun getAccessClaims(token: String): Claims {
        return getClaims(token, jwtAccessSecret)
    }

    fun getRefreshClaims(token: String): Claims {
        return getClaims(token, jwtRefreshSecret)
    }

    private fun getClaims(token: String, secret: Key): Claims {
        return Jwts.parserBuilder()
            .setSigningKey(secret)
            .build()
            .parseClaimsJws(token)
            .body
    }


//    fun getNameFromJwt(jwt: String): String {
//        return Jwts.parser().setSigningKey(jwtAccessSecret).parseClaimsJws(jwt).body.subject
//    }
}