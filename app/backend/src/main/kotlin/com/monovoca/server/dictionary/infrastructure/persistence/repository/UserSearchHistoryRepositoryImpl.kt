package com.monovoca.server.dictionary.infrastructure.persistence.repository

import com.monovoca.server.dictionary.domain.model.user.UserSearchHistory
import com.monovoca.server.dictionary.domain.port.UserSearchHistoryRepository
import com.monovoca.server.dictionary.infrastructure.persistence.entity.UserSearchHistoryEntity
import com.monovoca.server.dictionary.infrastructure.persistence.entity.UserSearchHistoryTable
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.update
import org.springframework.stereotype.Repository

@Repository
class UserSearchHistoryRepositoryImpl : UserSearchHistoryRepository {

    override fun save(history: UserSearchHistory): UserSearchHistory {
        val entity = if (history.id == null) {
            UserSearchHistoryEntity.new {
                applyDomain(history)
                isDeleted = false
            }
        } else {
            UserSearchHistoryEntity.findById(history.id)?.apply { applyDomain(history) }
                ?: error("UserSearchHistory with ID ${history.id} not found")
        }
        return entity.toDomain()
    }

    override fun delete(history: UserSearchHistory) {
        UserSearchHistoryEntity.findById(history.id!!)!!.isDeleted = true
    }

    override fun findById(historyId: Long): UserSearchHistory? =
        UserSearchHistoryEntity
            .find { (UserSearchHistoryTable.id eq historyId) and (UserSearchHistoryTable.isDeleted eq false) }
            .firstOrNull()
            ?.toDomain()

    override fun findByUserIdOrderBySearchedAtDesc(userId: String, limit: Int): List<UserSearchHistory> =
        UserSearchHistoryEntity
            .find { (UserSearchHistoryTable.userId eq userId) and (UserSearchHistoryTable.isDeleted eq false) }
            .orderBy(UserSearchHistoryTable.searchedAt to SortOrder.DESC)
            .limit(limit)
            .map { it.toDomain() }

    override fun deleteAllByUserId(userId: String) {
        UserSearchHistoryTable.update({ (UserSearchHistoryTable.userId eq userId) and (UserSearchHistoryTable.isDeleted eq false) }) {
            it[isDeleted] = true
        }
    }

    override fun findByUserIdAndSpelling(userId: String, spelling: String): UserSearchHistory? =
        UserSearchHistoryEntity
            .find {
                (UserSearchHistoryTable.userId eq userId) and
                        (UserSearchHistoryTable.spelling eq spelling) and
                        (UserSearchHistoryTable.isDeleted eq false)
            }
            .firstOrNull()
            ?.toDomain()
}
