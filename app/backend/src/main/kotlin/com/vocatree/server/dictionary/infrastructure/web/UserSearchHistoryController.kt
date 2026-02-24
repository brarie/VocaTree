package com.vocatree.server.dictionary.infrastructure.web

import com.vocatree.server.common.infrastructure.UserPrincipal
import com.vocatree.server.dictionary.application.UserSearchHistoryService
import com.vocatree.server.dictionary.infrastructure.web.response.UserSearchHistoryResponse
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/dictionary/my/search-history")
class UserSearchHistoryController(
    private val userSearchHistoryService: UserSearchHistoryService
) {

    @GetMapping
    fun getMySearchHistory(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @RequestParam(defaultValue = "10") limit: Int
    ): ResponseEntity<List<UserSearchHistoryResponse>> {
        val histories = userSearchHistoryService.getMySearchHistory(
            userId = userPrincipal.id,
            limit = limit
        )
        return ResponseEntity.ok(histories.map { UserSearchHistoryResponse.from(it) })
    }

    @DeleteMapping
    fun deleteAllMySearchHistory(
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<Unit> {
        userSearchHistoryService.deleteAllMySearchHistory(userPrincipal.id)
        return ResponseEntity.ok().build()
    }

    @DeleteMapping("/{historyId}")
    fun deleteMySearchHistory(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @PathVariable historyId: Long
    ): ResponseEntity<Unit> {
        userSearchHistoryService.deleteMySearchHistory(
            userId = userPrincipal.id,
            historyId = historyId
        )
        return ResponseEntity.ok().build()
    }
}
