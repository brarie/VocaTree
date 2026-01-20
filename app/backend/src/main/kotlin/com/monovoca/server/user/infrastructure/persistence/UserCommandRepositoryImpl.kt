package com.monovoca.server.user.infrastructure.persistence

import com.monovoca.server.user.domain.model.User
import com.monovoca.server.user.domain.repository.UserCommandRepository
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class UserCommandRepositoryImpl: UserCommandRepository {

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
}