package com.vocatree.server.dictionary.fixture

import com.vocatree.server.dictionary.domain.model.user.UserSearchHistory
import java.time.LocalDateTime

object UserSearchHistoryFixture {

    fun create(
        id: Long? = null,
        userId: String = "test-user-id",
        spelling: String = "test",
        searchedAt: LocalDateTime = LocalDateTime.of(2025, 1, 15, 10, 0, 0)
    ): UserSearchHistory {
        return UserSearchHistory(
            id = id,
            userId = userId,
            spelling = spelling,
            searchedAt = searchedAt
        )
    }
}
