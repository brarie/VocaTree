package com.vocatree.server.dictionary.application

import com.vocatree.server.common.domain.port.holder.LocalDateTimeHolder
import com.vocatree.server.dictionary.domain.exception.UserSearchHistoryNotFoundException
import com.vocatree.server.dictionary.domain.model.user.UserSearchHistory
import com.vocatree.server.dictionary.domain.port.UserSearchHistoryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserSearchHistoryService(
    private val userSearchHistoryRepository: UserSearchHistoryRepository,
    private val localDateTimeHolder: LocalDateTimeHolder
) {

    @Transactional(readOnly = true)
    fun getMySearchHistory(userId: String, limit: Int): List<UserSearchHistory> =
        userSearchHistoryRepository.findByUserIdOrderBySearchedAtDesc(userId, limit)

    @Transactional
    fun deleteAllMySearchHistory(userId: String) {
        userSearchHistoryRepository.deleteAllByUserId(userId)
    }

    @Transactional
    fun deleteMySearchHistory(userId: String, historyId: Long) {
        val history = userSearchHistoryRepository.findById(historyId)
            ?: throw UserSearchHistoryNotFoundException()

        if (history.userId != userId)
            throw UserSearchHistoryNotFoundException()

        userSearchHistoryRepository.delete(history)
    }

    @Transactional
    fun recordSearch(userId: String, spelling: String) {
        val existing = userSearchHistoryRepository.findByUserIdAndSpelling(userId, spelling)

        if (existing != null) {
            val updated = existing.renewSearchedAt(localDateTimeHolder.now())
            userSearchHistoryRepository.save(updated)
        } else {
            val newHistory = UserSearchHistory(
                userId = userId,
                spelling = spelling,
                searchedAt = localDateTimeHolder.now()
            )
            userSearchHistoryRepository.save(newHistory)
        }
    }
}
