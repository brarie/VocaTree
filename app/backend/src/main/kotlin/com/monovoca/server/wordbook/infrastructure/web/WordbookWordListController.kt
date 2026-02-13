package com.monovoca.server.wordbook.infrastructure.web

import com.monovoca.server.common.infrastructure.UserPrincipal
import com.monovoca.server.wordbook.application.WordbookWordListService
import com.monovoca.server.wordbook.infrastructure.web.request.*
import com.monovoca.server.wordbook.infrastructure.web.response.WordbookWordListResponse
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

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

    @PostMapping("/series")
    fun createWordSeries(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @PathVariable wordbookId: Long,
    ): ResponseEntity<WordbookWordListResponse> {
        val wordbookWordList = wordbookWordListService.createWordSeries(userPrincipal.id, wordbookId)
        val response = WordbookWordListResponse.from(wordbookWordList)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/{wordId}/senses/series")
    fun createSenseSeries(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @PathVariable wordbookId: Long,
        @PathVariable wordId: Long,
    ): ResponseEntity<WordbookWordListResponse> {
        val wordbookWordList = wordbookWordListService.createSenseSeries(userPrincipal.id, wordbookId, wordId)
        val response = WordbookWordListResponse.from(wordbookWordList)
        return ResponseEntity.ok(response)
    }

    @PostMapping
    @Deprecated(message = "사용자의 UX 개선을 위해 createWordSeries로 대체되었습니다.")
    fun createWord(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @PathVariable wordbookId: Long,
    ): ResponseEntity<WordbookWordListResponse> {
        val wordbookWordList = wordbookWordListService.createWord(userPrincipal.id, wordbookId)
        val response = WordbookWordListResponse.from(wordbookWordList)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/{wordId}/senses")
    @Deprecated(message = "사용자의 UX 개선을 위해 createSenseSeries로 대체되었습니다.")
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
        @RequestBody @Valid request: UpdateWordbookWordRequest,
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

    @PostMapping("/swap-order")
    fun swapWordsOrder(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @PathVariable wordbookId: Long,
        @RequestBody request: SwapWordbookWordsOrderRequest,
    ): ResponseEntity<WordbookWordListResponse> {
        val wordbookWordList = wordbookWordListService.swapWordsOrder(
            userPrincipal.id,
            wordbookId,
            request.wordId1,
            request.wordId2
        )
        val response = WordbookWordListResponse.from(wordbookWordList)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/{wordId}/senses/swap-order")
    fun swapSensesOrder(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @PathVariable wordbookId: Long,
        @PathVariable wordId: Long,
        @RequestBody request: SwapWordbookSensesOrderRequest,
    ): ResponseEntity<WordbookWordListResponse> {
        val wordbookWordList = wordbookWordListService.swapSensesOrder(
            userPrincipal.id,
            wordbookId,
            wordId,
            request.senseId1,
            request.senseId2
        )
        val response = WordbookWordListResponse.from(wordbookWordList)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/{wordId}/senses/{senseId}/examples/swap-order")
    fun swapExamplesOrder(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @PathVariable wordbookId: Long,
        @PathVariable wordId: Long,
        @PathVariable senseId: Long,
        @RequestBody request: SwapWordbookExamplesOrderRequest,
    ): ResponseEntity<WordbookWordListResponse> {
        val wordbookWordList = wordbookWordListService.swapExamplesOrder(
            userPrincipal.id,
            wordbookId,
            wordId,
            senseId,
            request.exampleId1,
            request.exampleId2
        )
        val response = WordbookWordListResponse.from(wordbookWordList)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/{wordId}/auto-complete-from-dictionary")
    fun autoCompleteFromDictionary(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @PathVariable wordbookId: Long,
        @PathVariable wordId: Long,
        @RequestBody request: AutoCompleteFromDictionaryRequest,
    ): ResponseEntity<WordbookWordListResponse> {
        val wordbookWordList = wordbookWordListService.autoCompleteFromDictionary(
            userPrincipal.id,
            wordbookId,
            wordId,
            request.toRequestCommand()
        )
        val response = WordbookWordListResponse.from(wordbookWordList)
        return ResponseEntity.ok(response)
    }
}