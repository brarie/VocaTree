package com.vocatree.server.wordbook.infrastructure.web

import com.vocatree.server.common.infrastructure.UserPrincipal
import com.vocatree.server.wordbook.application.WordbookService
import com.vocatree.server.wordbook.domain.model.enumerated.ColumnType
import com.vocatree.server.wordbook.infrastructure.web.request.CreateWordbookRequest
import com.vocatree.server.wordbook.infrastructure.web.request.UpdateColumnWidthRequest
import com.vocatree.server.wordbook.infrastructure.web.request.UpdateWordbookTitleRequest
import com.vocatree.server.wordbook.infrastructure.web.response.WordbookColumnResponse
import com.vocatree.server.wordbook.infrastructure.web.response.WordbookNodeResponse
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

    @PostMapping("/wordbook-bundle/{bundleId}")
    fun createWordbookInBundle(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @PathVariable bundleId: Long,
        @RequestBody request: CreateWordbookRequest
    ): ResponseEntity<WordbookNodeResponse> {
        val wordbook = wordbookService.createInBundle(userPrincipal.id, bundleId, request.title)
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

    @GetMapping("/{wordbookId}/columns")
    fun getWordbookColumns(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @PathVariable wordbookId: Long,
    ): ResponseEntity<WordbookColumnResponse> {
        val columns = wordbookService.getWordbookColumns(userPrincipal.id, wordbookId)
        val response = WordbookColumnResponse.from(columns)
        return ResponseEntity.ok(response)
    }

    @PatchMapping("/{wordbookId}/columns/{columnType}/width")
    fun updateWordbookColumnWidth(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @PathVariable wordbookId: Long,
        @PathVariable columnType: ColumnType,
        @RequestBody request: UpdateColumnWidthRequest
    ): ResponseEntity<WordbookColumnResponse> {
        val updatedColumns = wordbookService.updateColumnWidth(
            userPrincipal.id,
            wordbookId,
            columnType,
            request.width
        )
        val response = WordbookColumnResponse.from(updatedColumns)
        return ResponseEntity.ok(response)
    }

    @PatchMapping("/{wordbookId}/columns/{columnType}/toggle")
    fun updateWordbookColumnActiveStatus(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @PathVariable wordbookId: Long,
        @PathVariable columnType: ColumnType,
    ): ResponseEntity<WordbookColumnResponse> {
        val updatedColumns = wordbookService.toggleColumnActiveStatus(
            userPrincipal.id,
            wordbookId,
            columnType
        )
        val response = WordbookColumnResponse.from(updatedColumns)
        return ResponseEntity.ok(response)
    }
}