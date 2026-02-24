package com.vocatree.server.user.infrastructure.persistence.repository

import com.vocatree.server.common.domain.model.vo.Username
import com.vocatree.server.user.domain.model.User
import com.vocatree.server.user.domain.model.vo.Email
import com.vocatree.server.user.domain.port.UserRepository
import com.vocatree.server.user.infrastructure.persistence.entity.UserEntity
import com.vocatree.server.user.infrastructure.persistence.entity.UserTable
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class UserRepositoryImpl : UserRepository {

    override fun save(user: User): User {
        val entity = if (user.id == null) {
            UserEntity.new { applyDomain(user) }
        } else {
            val uuid = UUID.fromString(user.id)
            UserEntity.findById(uuid)?.apply { applyDomain(user) }
                ?: error("User with ID ${user.id} not found")
        }
        return entity.toDomain()
    }

    override fun findById(id: String): User? =
        UserEntity.findById(UUID.fromString(id))
            ?.toDomain()

    override fun existsByUsername(username: Username): Boolean =
        UserTable.select(UserTable.id)
            .where { UserTable.username eq username.value }
            .empty().not()

    override fun existsByEmail(email: Email): Boolean =
        UserTable.select(UserTable.id)
            .where { UserTable.email eq email.value }
            .empty().not()
}