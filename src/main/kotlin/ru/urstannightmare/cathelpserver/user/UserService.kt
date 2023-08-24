package ru.urstannightmare.cathelpserver.user

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val userInDb: User = userRepository.findByUsername(username)
            .orElseThrow { UserNotFoundException("User $username not found.") }

        return build(userInDb)
    }

}