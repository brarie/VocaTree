package com.monovoca.server.dictionary.domain.model.user

import java.time.LocalDateTime

data class UserSearchHistory(
    val id: Long? = null,
    val userId: String,
    val spelling: String,
    val searchedAt: LocalDateTime,
) {

    fun renewSearchedAt(newSearchedAt: LocalDateTime): UserSearchHistory {
        return this.copy(searchedAt = newSearchedAt)
    }
}