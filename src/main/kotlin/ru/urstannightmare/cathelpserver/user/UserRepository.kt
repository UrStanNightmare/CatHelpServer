package ru.urstannightmare.cathelpserver.user;

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun findByUsername(username: String): Optional<User>
    fun existsByUsername(username: String): Boolean

    @Transactional
    @Modifying
    @Query(
        value = """
            INSERT INTO users.rtokens
            VALUES (:id, :refreshToken)
            ON CONFLICT (user_id) DO UPDATE SET refresh_token = EXCLUDED.refresh_token;
        """,
        nativeQuery = true
    )
    fun updateRefreshTokenById(id: Long, refreshToken: String): Int
}