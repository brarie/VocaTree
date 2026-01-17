package com.monovoca.server.user.infrastructure.persistence

import com.monovoca.server.user.domain.exception.UserNotFoundException
import com.monovoca.server.user.domain.model.User
import com.monovoca.server.user.domain.repository.UserCommandRepository
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Repository

@Repository
class UserCommandRepositoryImpl: UserCommandRepository {

    override fun save(user: User): User = transaction {
        val entity = if (user.id == null) {
            UserEntity.new { applyDomain(user) }
        } else {
            UserEntity.findById(user.id)?.apply { applyDomain(user) }
                ?: throw UserNotFoundException()
        }
        entity.toDomain()
    }
}