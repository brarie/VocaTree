package com.vocatree.server.dictionary.domain.port

import com.vocatree.server.dictionary.domain.model.user.UserSearchHistory

interface UserSearchHistoryRepository {
    fun save(history: UserSearchHistory): UserSearchHistory
    fun delete(history: UserSearchHistory)
    fun findById(historyId: Long): UserSearchHistory?
    fun findByUserIdOrderBySearchedAtDesc(userId: String, limit: Int): List<UserSearchHistory>
    fun deleteAllByUserId(userId: String)
    fun findByUserIdAndSpelling(userId: String, spelling: String): UserSearchHistory?
}
