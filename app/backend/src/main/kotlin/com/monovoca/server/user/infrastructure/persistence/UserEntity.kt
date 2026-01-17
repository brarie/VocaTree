package com.monovoca.server.user.infrastructure.persistence

import com.monovoca.server.user.domain.model.User
import com.monovoca.server.user.domain.model.vo.Email
import com.monovoca.server.user.domain.model.vo.EncryptedPassword
import com.monovoca.server.user.domain.model.vo.Nickname
import com.monovoca.server.user.domain.model.vo.Username
import com.monovoca.server.user.domain.model.vo.UserRole
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable

object UserTable : LongIdTable("user") {
    val username = varchar("username", 20).uniqueIndex()
    val email = varchar("email", 100).uniqueIndex()
    val passwordHash = varchar("password_hash", 255)
    val nickname = varchar("nickname", 50)
    val role = enumerationByName("role", 20, UserRole::class).default(UserRole.USER)
}

class UserEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<UserEntity>(UserTable)

    var username by UserTable.username
    var email by UserTable.email
    var passwordHash by UserTable.passwordHash
    var nickname by UserTable.nickname
    var role by UserTable.role

    fun toDomain(): User = User(
        id = this.id.value,
        username = Username(this.username),
        email = Email(this.email),
        password = EncryptedPassword.fromHash(this.passwordHash),
        nickname = Nickname(this.nickname),
        role = this.role,
    )

    fun applyDomain(user: User) {
        this.username = user.username.value
        this.email = user.email.value
        this.passwordHash = user.password.value
        this.nickname = user.nickname.value
        this.role = user.role
    }
}