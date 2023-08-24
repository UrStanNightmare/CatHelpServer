package ru.urstannightmare.cathelpserver.task.v2.entity

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import org.hibernate.validator.constraints.Length
import java.util.*

@Entity
@Table(name = "tasks", schema = "cathelp")
class Task {
    constructor(){}
    constructor(date: Date?, description: String?, isDone: Boolean) {
        this.date = date
        this.description = description
        this.isDone = isDone
    }
    constructor(id: Long?, date: Date?, description: String?, isDone: Boolean) {
        this.id = id
        this.date = date
        this.description = description
        this.isDone = isDone
    }

    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "task_gen")
    @SequenceGenerator(name = "task_gen", sequenceName = "task_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    var id: Long? = null

    @NotNull
    @Temporal(TemporalType.DATE)
    @JdbcTypeCode(SqlTypes.DATE)
    @Column(name = "comp_date")
    var date: Date? = null

    @Length(min = 1, max = 300)
    @NotNull
    @Column(name = "description", nullable = false, length = 300)
    var description: String? = null

    @NotNull
    @JdbcTypeCode(SqlTypes.BOOLEAN)
    @Column(name = "is_done")
    var isDone: Boolean? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Task

        if (id != other.id) return false
        if (date != other.date) return false
        if (description != other.description) return false
        return isDone == other.isDone
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (date?.hashCode() ?: 0)
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + isDone.hashCode()
        return result
    }

    override fun toString(): String {
        return "Task[$id] date: $date; desc: $description; isDone: $isDone"
    }
}