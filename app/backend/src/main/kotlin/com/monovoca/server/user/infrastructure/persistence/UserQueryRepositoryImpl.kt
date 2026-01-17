package com.monovoca.server.user.infrastructure.persistence

import com.monovoca.server.user.domain.model.vo.Email
import com.monovoca.server.user.domain.model.vo.Username
import com.monovoca.server.user.domain.repository.UserQueryRepository
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Repository

@Repository
class UserQueryRepositoryImpl: UserQueryRepository {

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