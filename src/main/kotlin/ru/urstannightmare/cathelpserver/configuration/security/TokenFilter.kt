package ru.urstannightmare.cathelpserver.configuration.security

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import org.springframework.web.filter.GenericFilterBean

private const val AUTHORIZATION = "Authorization"

@Component
class TokenFilter(
    private val jwtCore: JwtCore,
    private val userDetailsService: UserDetailsService
) : GenericFilterBean() {
    override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain?) {
        val token = getTokenFromRequest(request as HttpServletRequest)
        if (token != null && jwtCore.validateAccessToken(token)
            && SecurityContextHolder.getContext().authentication == null) {
            val claims = jwtCore.getAccessClaims(token)
            val user = userDetailsService.loadUserByUsername(claims.subject)
            val auth = UsernamePasswordAuthenticationToken(user, null)
            SecurityContextHolder.getContext().authentication = auth
        }
        chain?.doFilter(request, response)
    }

    private fun getTokenFromRequest(request: HttpServletRequest): String? {
        val bearer: String? = request.getHeader(AUTHORIZATION)
        if (bearer != null && StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7)
        }
        return null
    }
}