package com.monovoca.server.user.infrastructure.persistence.repository

import com.monovoca.server.common.domain.model.vo.Username
import com.monovoca.server.user.domain.model.User
import com.monovoca.server.user.domain.model.vo.Email
import com.monovoca.server.user.domain.port.UserRepository
import com.monovoca.server.user.infrastructure.persistence.entity.UserEntity
import com.monovoca.server.user.infrastructure.persistence.entity.UserTable
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class UserRepositoryImpl : UserRepository {

    override fun save(user: User): User = transaction {
        val entity = if (user.id == null) {
            UserEntity.new { applyDomain(user) }
        } else {
            val uuid = UUID.fromString(user.id)
            UserEntity.findById(uuid)?.apply { applyDomain(user) }
                ?: error("User with ID ${user.id} not found")
        }
        entity.toDomain()
    }

    override fun findById(id: String): User? = transaction {
        UserEntity.findById(UUID.fromString(id))
            ?.toDomain()
    }

    override fun existsByUsername(username: Username): Boolean = transaction {
        UserTable.select(UserTable.id)
            .where { UserTable.username eq username.value }
            .empty().not()
    }

    override fun existsByEmail(email: Email): Boolean = transaction {
        UserTable.select(UserTable.id)
            .where { UserTable.email eq email.value }
            .empty().not()
    }
}