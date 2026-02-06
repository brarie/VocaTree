package com.monovoca.server.wordbook.infrastructure.web

import com.monovoca.server.common.infrastructure.UserPrincipal
import com.monovoca.server.wordbook.application.WordbookService
import com.monovoca.server.wordbook.infrastructure.web.request.CreateWordbookRequest
import com.monovoca.server.wordbook.infrastructure.web.request.UpdateWordbookTitleRequest
import com.monovoca.server.wordbook.infrastructure.web.response.WordbookNodeResponse
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/wordbooks")
class WordbookController(
    private val wordbookService: WordbookService
) {

    @GetMapping("/{wordbookId}")
    fun getWordbook(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @PathVariable wordbookId: Long,
    ): ResponseEntity<WordbookNodeResponse> {
        val wordbook = wordbookService.getWordbook(userPrincipal.id, wordbookId)
        val response = WordbookNodeResponse.from(wordbook)
        return ResponseEntity.ok(response)
    }

    @PostMapping
    fun createWordbook(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @RequestBody request: CreateWordbookRequest
    ): ResponseEntity<WordbookNodeResponse> {
        val wordbook = wordbookService.create(userPrincipal.id, request.title)
        val response = WordbookNodeResponse.from(wordbook)
        return ResponseEntity.ok(response)
    }

    @PatchMapping("/{wordbookId}/title")
    fun updateWordbookTitle(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @PathVariable wordbookId: Long,
        @RequestBody request: UpdateWordbookTitleRequest
    ): ResponseEntity<WordbookNodeResponse> {
        val wordbook = wordbookService.updateTitle(userPrincipal.id, wordbookId, request.title)
        val response = WordbookNodeResponse.from(wordbook)
        return ResponseEntity.ok(response)
    }

    @PatchMapping("/{wordbookId}/star")
    fun toggleStarred(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @PathVariable wordbookId: Long,
    ): ResponseEntity<WordbookNodeResponse> {
        val wordbook = wordbookService.toggleStarred(userPrincipal.id, wordbookId)
        val response = WordbookNodeResponse.from(wordbook)
        return ResponseEntity.ok(response)
    }

    @PatchMapping("/{wordbookId}/move/root")
    fun moveWordbookToRoot(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @PathVariable wordbookId: Long,
    ): ResponseEntity<WordbookNodeResponse> {
        val wordbook = wordbookService.move(userPrincipal.id, wordbookId, null)
        val response = WordbookNodeResponse.from(wordbook)
        return ResponseEntity.ok(response)
    }

    @PatchMapping("/{wordbookId}/move/{bundleId}")
    fun moveWordbookToBundle(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @PathVariable wordbookId: Long,
        @PathVariable bundleId: Long,
    ): ResponseEntity<WordbookNodeResponse> {
        val wordbook = wordbookService.move(userPrincipal.id, wordbookId, bundleId)
        val response = WordbookNodeResponse.from(wordbook)
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/{wordbookId}")
    fun deleteWordbook(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @PathVariable wordbookId: Long,
    ): ResponseEntity<Unit> {
        wordbookService.delete(userPrincipal.id, wordbookId)
        return ResponseEntity.ok().build()
    }
}