package com.monovoca.server.dictionary.infrastructure.web

import com.monovoca.server.common.infrastructure.UserPrincipal
import com.monovoca.server.dictionary.application.DictionaryWordService
import com.monovoca.server.dictionary.domain.model.query.PublicWordSearchView
import com.monovoca.server.dictionary.infrastructure.web.request.*
import com.monovoca.server.dictionary.infrastructure.web.response.PublicWordResponse
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/dictionary")
class DictionaryWordController(
    private val dictionaryWordService: DictionaryWordService
) {

    @GetMapping("/search/autocomplete")
    fun autocomplete(
        @RequestParam query: String,
        @RequestParam(defaultValue = "10") limit: Int
    ): ResponseEntity<List<PublicWordSearchView>> {
        val views = dictionaryWordService.autocomplete(query, limit)
        return ResponseEntity.ok(views)
    }

    @GetMapping("/search")
    fun searchWords(
        @RequestParam query: String,
        pageable: Pageable,
        @AuthenticationPrincipal userPrincipal: UserPrincipal?
    ): ResponseEntity<Page<PublicWordSearchView>> {
        val userId = userPrincipal?.id
        val views = dictionaryWordService.paginateViewsBySpelling(query, pageable, userId)
        return ResponseEntity.ok(views)
    }

    @GetMapping("/words/slug/{slug}")
    fun getWordBySlug(@PathVariable slug: String): ResponseEntity<PublicWordResponse> {
        val word = dictionaryWordService.findBySlug(slug)
        return ResponseEntity.ok(PublicWordResponse.from(word))
    }

    @PostMapping("/words")
    @PreAuthorize("hasRole('ADMIN')")
    fun createWord(@RequestBody @Valid request: CreatePublicWordRequest): ResponseEntity<PublicWordResponse> {
        val word = dictionaryWordService.createWord(request.spelling)
        return ResponseEntity.ok(PublicWordResponse.from(word))
    }

    @PatchMapping("/words/{wordId}")
    @PreAuthorize("hasRole('ADMIN')")
    fun updateWord(
        @PathVariable wordId: Long,
        @RequestBody @Valid request: UpdatePublicWordRequest
    ): ResponseEntity<PublicWordResponse> {
        val word = dictionaryWordService.updateWord(wordId, request.spelling)
        return ResponseEntity.ok(PublicWordResponse.from(word))
    }

    @DeleteMapping("/words/{wordId}")
    @PreAuthorize("hasRole('ADMIN')")
    fun deleteWord(@PathVariable wordId: Long): ResponseEntity<Unit> {
        dictionaryWordService.deleteWord(wordId)
        return ResponseEntity.ok().build()
    }

    @PostMapping("/words/{wordId}/senses")
    @PreAuthorize("hasRole('ADMIN')")
    fun createSense(
        @PathVariable wordId: Long,
        @RequestBody @Valid request: CreateSenseRequest
    ): ResponseEntity<PublicWordResponse> {
        val word = dictionaryWordService.createSense(wordId, request.toCommand())
        return ResponseEntity.ok(PublicWordResponse.from(word))
    }

    @PutMapping("/words/{wordId}/senses/{senseId}")
    @PreAuthorize("hasRole('ADMIN')")
    fun updateSense(
        @PathVariable wordId: Long,
        @PathVariable senseId: Long,
        @RequestBody @Valid request: UpdateSenseRequest
    ): ResponseEntity<PublicWordResponse> {
        val word = dictionaryWordService.updateSense(wordId, senseId, request.toCommand())
        return ResponseEntity.ok(PublicWordResponse.from(word))
    }

    @DeleteMapping("/words/{wordId}/senses/{senseId}")
    @PreAuthorize("hasRole('ADMIN')")
    fun deleteSense(
        @PathVariable wordId: Long,
        @PathVariable senseId: Long
    ): ResponseEntity<Unit> {
        dictionaryWordService.deleteSense(wordId, senseId)
        return ResponseEntity.ok().build()
    }

    @PostMapping("/words/{wordId}/senses/reorder")
    @PreAuthorize("hasRole('ADMIN')")
    fun reorderSenses(
        @PathVariable wordId: Long,
        @RequestBody @Valid request: ReorderSensesRequest
    ): ResponseEntity<PublicWordResponse> {
        val word = dictionaryWordService.reorderSenses(wordId, request.toOrderList())
        return ResponseEntity.ok(PublicWordResponse.from(word))
    }

    @PostMapping("/words/{wordId}/senses/{senseId}/examples")
    @PreAuthorize("hasRole('ADMIN')")
    fun createExample(
        @PathVariable wordId: Long,
        @PathVariable senseId: Long,
        @RequestBody @Valid request: CreateExampleRequest
    ): ResponseEntity<PublicWordResponse> {
        val word = dictionaryWordService.createExample(wordId, senseId, request.toCommand())
        return ResponseEntity.ok(PublicWordResponse.from(word))
    }

    @PutMapping("/words/{wordId}/senses/{senseId}/examples/{exampleId}")
    @PreAuthorize("hasRole('ADMIN')")
    fun updateExample(
        @PathVariable wordId: Long,
        @PathVariable senseId: Long,
        @PathVariable exampleId: Long,
        @RequestBody @Valid request: UpdateExampleRequest
    ): ResponseEntity<PublicWordResponse> {
        val word = dictionaryWordService.updateExample(wordId, senseId, exampleId, request.toCommand())
        return ResponseEntity.ok(PublicWordResponse.from(word))
    }

    @DeleteMapping("/words/{wordId}/senses/{senseId}/examples/{exampleId}")
    @PreAuthorize("hasRole('ADMIN')")
    fun deleteExample(
        @PathVariable wordId: Long,
        @PathVariable senseId: Long,
        @PathVariable exampleId: Long
    ): ResponseEntity<Unit> {
        dictionaryWordService.deleteExample(wordId, senseId, exampleId)
        return ResponseEntity.ok().build()
    }

    @PostMapping("/words/{wordId}/senses/{senseId}/examples/reorder")
    @PreAuthorize("hasRole('ADMIN')")
    fun reorderExamples(
        @PathVariable wordId: Long,
        @PathVariable senseId: Long,
        @RequestBody @Valid request: ReorderExamplesRequest
    ): ResponseEntity<PublicWordResponse> {
        val word = dictionaryWordService.reorderExamples(wordId, senseId, request.toOrderList())
        return ResponseEntity.ok(PublicWordResponse.from(word))
    }
}