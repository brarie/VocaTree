package com.monovoca.server.auth.infrastructure.persistence

import com.monovoca.server.auth.domain.model.AuthUser
import com.monovoca.server.auth.domain.port.AuthUserQueryRepository
import com.monovoca.server.common.domain.vo.EncryptedPassword
import com.monovoca.server.common.domain.vo.Username
import com.monovoca.server.user.infrastructure.persistence.UserTable
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class AuthUserQueryRepositoryImpl : AuthUserQueryRepository {

    override fun findAuthUserById(id: String): AuthUser? = transaction {
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
    }

    override fun findAuthUserByUsername(username: Username): AuthUser? = transaction {
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
}