package ru.urstannightmare.cathelpserver.task.v2

import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import ru.urstannightmare.cathelpserver.task.v2.entity.Task
import java.util.*

@Repository
interface TaskRepository : JpaRepository<Task, Long> {
    @Query("select t from Task t where t.id = ?1")
    override fun findById(id: Long): Optional<Task>

    @Query(
        value = """
            select id, comp_date, description, is_done 
            from cathelp.tasks 
            where comp_date between :startDate and :endDate
            order by id
            """,
        nativeQuery = true
    )
    fun getTasksByStartAndEndDate(
        @Param("startDate") startDate: Date,
        @Param("endDate") endDate: Date
    ): Optional<List<Task>>

    @Modifying
    @Transactional
    @Query("update Task t set t.isDone = ?1 where t.id = ?2")
    fun updateIsDoneById(isDone: Boolean, id: Long): Int

    @Modifying
    @Transactional
    @Query("delete from Task t where t.id = ?1")
    fun deleteByIdWithResult(id: Long): Int
}