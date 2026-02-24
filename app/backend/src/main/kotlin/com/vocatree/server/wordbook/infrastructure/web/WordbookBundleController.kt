package com.vocatree.server.wordbook.infrastructure.web

import com.vocatree.server.common.infrastructure.UserPrincipal
import com.vocatree.server.wordbook.application.WordbookBundleService
import com.vocatree.server.wordbook.infrastructure.web.request.CreateBundleRequest
import com.vocatree.server.wordbook.infrastructure.web.request.UpdateBundleTitleRequest
import com.vocatree.server.wordbook.infrastructure.web.response.WordbookNodeResponse
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/wordbook-bundles")
class WordbookBundleController(
    private val wordbookBundleService: WordbookBundleService
) {

    @GetMapping("/{bundleId}")
    fun getBundle(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @PathVariable bundleId: Long,
    ): ResponseEntity<WordbookNodeResponse> {
        val bundle = wordbookBundleService.getBundle(userPrincipal.id, bundleId)
        val response = WordbookNodeResponse.from(bundle)
        return ResponseEntity.ok(response)
    }

    @GetMapping
    fun getAllBundles(@AuthenticationPrincipal userPrincipal: UserPrincipal) : ResponseEntity<List<WordbookNodeResponse>> {
        val bundles = wordbookBundleService.getAllBundles(userPrincipal.id)
        val responses = bundles.map { WordbookNodeResponse.from(it) }
        return ResponseEntity.ok(responses)
    }

    @PostMapping
    fun createBundle(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @RequestBody request: CreateBundleRequest
    ): ResponseEntity<WordbookNodeResponse> {
        val bundle = wordbookBundleService.create(userPrincipal.id, request.title)
        val response = WordbookNodeResponse.from(bundle)
        return ResponseEntity.ok(response)
    }

    @PatchMapping("/{bundleId}/title")
    fun updateBundleTitle(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @PathVariable bundleId: Long,
        @RequestBody request: UpdateBundleTitleRequest
    ): ResponseEntity<WordbookNodeResponse> {
        val bundle = wordbookBundleService.updateTitle(userPrincipal.id, bundleId, request.title)
        val response = WordbookNodeResponse.from(bundle)
        return ResponseEntity.ok(response)
    }

    @PatchMapping("/{bundleId}/star")
    fun toggleStarred(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @PathVariable bundleId: Long,
    ): ResponseEntity<WordbookNodeResponse> {
        val bundle = wordbookBundleService.toggleStarred(userPrincipal.id, bundleId)
        val response = WordbookNodeResponse.from(bundle)
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/{bundleId}")
    fun deleteBundle(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @PathVariable bundleId: Long,
    ): ResponseEntity<Unit> {
        wordbookBundleService.delete(userPrincipal.id, bundleId)
        return ResponseEntity.ok().build()
    }
}