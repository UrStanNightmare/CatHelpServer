package ru.urstannightmare.cathelpserver.user

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import org.hibernate.validator.constraints.Length

@Entity
@Table(name = "user", schema = "users")
@SecondaryTable(name = "rtokens", schema = "users", pkJoinColumns = [PrimaryKeyJoinColumn(name = "user_id")])
class User {
    constructor()
    constructor(username: String, password: String){
        this.username = username
        this.password = password
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_gen")
    @SequenceGenerator(name = "user_gen", sequenceName = "user_seq", allocationSize = 1, schema = "users")
    @Column(name = "id", nullable = false)
    var id: Long? = null

    @Length(min = 1, max = 20)
    @NotNull
    @Column(name = "username", unique = true, length = 20)
    var username: String? = null

    @NotNull
    @Column(name = "password")
    var password: String? = null

    @Column(name = "refresh_token", table = "rtokens")
    var refreshToken: String? = null
}