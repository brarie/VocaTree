package com.monovoca.server.wordbook.infrastructure.web

import com.monovoca.server.common.infrastructure.UserPrincipal
import com.monovoca.server.wordbook.application.WordbookWordListService
import com.monovoca.server.wordbook.infrastructure.web.request.UpdateWordbookExampleRequest
import com.monovoca.server.wordbook.infrastructure.web.request.UpdateWordbookSenseRequest
import com.monovoca.server.wordbook.infrastructure.web.request.UpdateWordbookWordRequest
import com.monovoca.server.wordbook.infrastructure.web.response.WordbookWordListResponse
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/wordbooks/{wordbookId}/words")
class WordbookWordListController(
    private val wordbookWordListService: WordbookWordListService
) {

    @GetMapping
    fun getWordList(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @PathVariable wordbookId: Long,
    ): ResponseEntity<WordbookWordListResponse> {
        val wordbookWordList = wordbookWordListService.getWordList(userPrincipal.id, wordbookId)
        val response = WordbookWordListResponse.from(wordbookWordList)
        return ResponseEntity.ok(response)
    }

    @PostMapping
    fun createWord(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @PathVariable wordbookId: Long,
    ): ResponseEntity<WordbookWordListResponse> {
        val wordbookWordList = wordbookWordListService.createWord(userPrincipal.id, wordbookId)
        val response = WordbookWordListResponse.from(wordbookWordList)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/{wordId}/senses")
    fun createSense(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @PathVariable wordbookId: Long,
        @PathVariable wordId: Long,
    ): ResponseEntity<WordbookWordListResponse> {
        val wordbookWordList = wordbookWordListService.createSense(userPrincipal.id, wordbookId, wordId)
        val response = WordbookWordListResponse.from(wordbookWordList)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/{wordId}/senses/{senseId}/examples")
    fun createExample(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @PathVariable wordbookId: Long,
        @PathVariable wordId: Long,
        @PathVariable senseId: Long,
    ): ResponseEntity<WordbookWordListResponse> {
        val wordbookWordList = wordbookWordListService.createExample(userPrincipal.id, wordbookId, wordId, senseId)
        val response = WordbookWordListResponse.from(wordbookWordList)
        return ResponseEntity.ok(response)
    }


    @DeleteMapping("/{wordId}")
    fun deleteWord(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @PathVariable wordbookId: Long,
        @PathVariable wordId: Long,
    ): ResponseEntity<WordbookWordListResponse> {
        val wordbookWordList = wordbookWordListService.deleteWord(userPrincipal.id, wordbookId, wordId)
        val response = WordbookWordListResponse.from(wordbookWordList)
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/{wordId}/senses/{senseId}")
    fun deleteSense(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @PathVariable wordbookId: Long,
        @PathVariable wordId: Long,
        @PathVariable senseId: Long,
    ): ResponseEntity<WordbookWordListResponse> {
        val wordbookWordList = wordbookWordListService.deleteSense(userPrincipal.id, wordbookId, wordId, senseId)
        val response = WordbookWordListResponse.from(wordbookWordList)
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/{wordId}/senses/{senseId}/examples/{exampleId}")
    fun deleteExample(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @PathVariable wordbookId: Long,
        @PathVariable wordId: Long,
        @PathVariable senseId: Long,
        @PathVariable exampleId: Long,
    ): ResponseEntity<WordbookWordListResponse> {
        val wordbookWordList =
            wordbookWordListService.deleteExample(userPrincipal.id, wordbookId, wordId, senseId, exampleId)
        val response = WordbookWordListResponse.from(wordbookWordList)
        return ResponseEntity.ok(response)
    }

    @PutMapping("/{wordId}")
    fun updateWord(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @PathVariable wordbookId: Long,
        @PathVariable wordId: Long,
        @RequestBody request: UpdateWordbookWordRequest,
    ): ResponseEntity<WordbookWordListResponse> {
        val wordbookWordList = wordbookWordListService.updateWord(
            userPrincipal.id,
            wordbookId,
            wordId,
            request.toCommand()
        )
        val response = WordbookWordListResponse.from(wordbookWordList)
        return ResponseEntity.ok(response)
    }

    @PutMapping("/{wordId}/senses/{senseId}")
    fun updateSense(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @PathVariable wordbookId: Long,
        @PathVariable wordId: Long,
        @PathVariable senseId: Long,
        @RequestBody request: UpdateWordbookSenseRequest,
    ): ResponseEntity<WordbookWordListResponse> {
        val wordbookWordList = wordbookWordListService.updateSense(
            userPrincipal.id,
            wordbookId,
            wordId,
            senseId,
            request.toCommand()
        )
        val response = WordbookWordListResponse.from(wordbookWordList)
        return ResponseEntity.ok(response)
    }

    @PutMapping("/{wordId}/senses/{senseId}/examples/{exampleId}")
    fun updateExample(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @PathVariable wordbookId: Long,
        @PathVariable wordId: Long,
        @PathVariable senseId: Long,
        @PathVariable exampleId: Long,
        @RequestBody request: UpdateWordbookExampleRequest,
    ): ResponseEntity<WordbookWordListResponse> {
        val wordbookWordList = wordbookWordListService.updateExample(
            userPrincipal.id,
            wordbookId,
            wordId,
            senseId,
            exampleId,
            request.toCommand()
        )
        val response = WordbookWordListResponse.from(wordbookWordList)
        return ResponseEntity.ok(response)
    }
}