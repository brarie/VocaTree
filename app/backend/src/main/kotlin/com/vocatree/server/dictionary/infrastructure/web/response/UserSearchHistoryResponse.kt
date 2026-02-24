package com.vocatree.server.dictionary.infrastructure.web.response

import com.vocatree.server.dictionary.domain.model.user.UserSearchHistory
import java.time.LocalDateTime

data class UserSearchHistoryResponse(
    val id: Long,
    val spelling: String,
    val searchedAt: LocalDateTime
) {
    companion object {
        fun from(history: UserSearchHistory) = UserSearchHistoryResponse(
            id = history.id!!,
            spelling = history.spelling,
            searchedAt = history.searchedAt
        )
    }
}
