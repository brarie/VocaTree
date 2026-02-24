package com.vocatree.server.auth.infrastructure.persistence.repository

import com.vocatree.server.auth.domain.model.AuthUser
import com.vocatree.server.auth.domain.port.AuthUserRepository
import com.vocatree.server.common.domain.model.vo.EncryptedPassword
import com.vocatree.server.common.domain.model.vo.Username
import com.vocatree.server.user.infrastructure.persistence.entity.UserTable
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class AuthUserRepositoryImpl : AuthUserRepository {

    override fun findAuthUserById(id: String): AuthUser? =
        UserTable.select(UserTable.id, UserTable.role, UserTable.username, UserTable.passwordHash)
            .where { UserTable.id eq UUID.fromString(id) }
            .singleOrNull()
            ?.let {
                AuthUser(
                    id = it[UserTable.id].value.toString(),
                    role = it[UserTable.role],
                    username = Username(it[UserTable.username]),
                    password = EncryptedPassword.fromHash(it[UserTable.passwordHash])
                )
            }

    override fun findAuthUserByUsername(username: Username): AuthUser? =
        UserTable.select(UserTable.id, UserTable.role, UserTable.username, UserTable.passwordHash)
            .where { UserTable.username eq username.value }
            .singleOrNull()
            ?.let {
                AuthUser(
                    id = it[UserTable.id].value.toString(),
                    role = it[UserTable.role],
                    username = Username(it[UserTable.username]),
                    password = EncryptedPassword.fromHash(it[UserTable.passwordHash])
                )
            }
}