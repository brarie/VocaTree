package com.vocatree.server.dictionary.infrastructure.persistence.entity

import com.vocatree.server.dictionary.domain.model.user.UserSearchHistory
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.datetime

object UserSearchHistoryTable : LongIdTable("user_search_history") {
    val userId = varchar("user_id", 50)
    val spelling = varchar("spelling", 100)
    val searchedAt = datetime("searched_at")
    val isDeleted = bool("is_deleted").default(false)
}

class UserSearchHistoryEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<UserSearchHistoryEntity>(UserSearchHistoryTable)

    var userId by UserSearchHistoryTable.userId
    var spelling by UserSearchHistoryTable.spelling
    var searchedAt by UserSearchHistoryTable.searchedAt
    var isDeleted by UserSearchHistoryTable.isDeleted

    fun toDomain(): UserSearchHistory = UserSearchHistory(
        id = this.id.value,
        userId = this.userId,
        spelling = this.spelling,
        searchedAt = this.searchedAt
    )

    fun applyDomain(history: UserSearchHistory) {
        this.userId = history.userId
        this.spelling = history.spelling
        this.searchedAt = history.searchedAt
    }
}

