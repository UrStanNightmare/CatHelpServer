package ru.urstannightmare.cathelpserver.user

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

fun build(user: User): UserDetailsImpl {
    return UserDetailsImpl(
        user.id,
        user.username,
        user.password,
        user.refreshToken
    )
}

class UserDetailsImpl(
    private val id: Long?,
    private val username: String?,
    private val password: String?,
    private val refreshToken: String?
) : UserDetails {
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return mutableListOf(SimpleGrantedAuthority("USER"))
    }

    fun getId(): Long {
        return id!!
    }

    fun getRefreshToken(): String?{
        return refreshToken
    }

    override fun getPassword(): String {
        return password!!
    }

    override fun getUsername(): String {
        return username!!
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }
}