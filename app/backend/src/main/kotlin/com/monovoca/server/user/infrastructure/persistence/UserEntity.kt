package com.monovoca.server.user.infrastructure.persistence

import com.monovoca.server.common.domain.model.vo.EncryptedPassword
import com.monovoca.server.common.domain.model.vo.UserRole
import com.monovoca.server.common.domain.model.vo.Username
import com.monovoca.server.user.domain.model.User
import com.monovoca.server.user.domain.model.vo.Email
import com.monovoca.server.user.domain.model.vo.Nickname
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.*

object UserTable : UUIDTable("user") {
    val username = varchar("username", 20).uniqueIndex()
    val email = varchar("email", 100).uniqueIndex()
    val passwordHash = varchar("password_hash", 255)
    val nickname = varchar("nickname", 50)
    val role = enumerationByName("role", 20, UserRole::class).default(UserRole.USER)
}

class UserEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<UserEntity>(UserTable)

    var username by UserTable.username
    var email by UserTable.email
    var passwordHash by UserTable.passwordHash
    var nickname by UserTable.nickname
    var role by UserTable.role

    fun toDomain(): User = User(
        id = this.id.value.toString(),
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