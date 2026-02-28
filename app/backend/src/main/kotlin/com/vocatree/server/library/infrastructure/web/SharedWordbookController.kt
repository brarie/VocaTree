package com.vocatree.server.library.infrastructure.web

import com.vocatree.server.common.infrastructure.UserPrincipal
import com.vocatree.server.library.application.SharedWordbookService
import com.vocatree.server.library.domain.model.enumerated.TrustedLevel
import com.vocatree.server.library.domain.model.query.SharedWordbookDetailView
import com.vocatree.server.library.domain.model.query.SharedWordbookListView
import com.vocatree.server.library.domain.model.query.SharedWordbookVersionView
import com.vocatree.server.library.domain.port.options.SharedWordbookSortType
import com.vocatree.server.library.infrastructure.web.request.MakeNewVersionRequest
import com.vocatree.server.library.infrastructure.web.request.ShareMyWordbookRequest
import com.vocatree.server.library.infrastructure.web.request.UpdateSharedWordbookMetadataRequest
import com.vocatree.server.library.infrastructure.web.request.UpdateTrustedLevelRequest
import com.vocatree.server.library.infrastructure.web.response.SharedWordSnapshotResponse
import com.vocatree.server.library.infrastructure.web.response.SharedWordbookResponse
import com.vocatree.server.library.infrastructure.web.response.SharedWordbookRevisionResponse

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/library/shared-wordbooks")
class SharedWordbookController(
    private val sharedWordbookService: SharedWordbookService,
) {

    /**
     * 내가 공유한 단어장 목록 조회
     * 모든 SharedState에 대해 조회됩니다.
     */
    @GetMapping("/my")
    fun getMySharedWordbooks(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        pageable: Pageable,
    ): ResponseEntity<Page<SharedWordbookListView>> {
        val result = sharedWordbookService.getMySharedWordbooks(userPrincipal.id, pageable)
        return ResponseEntity.ok(result)
    }

    /**
     * ADMIN용 단어장 목록 조회
     * 모든 사람들의 모든 SharedState에 대해 조회됩니다.
     */
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    fun getAllSharedWordbooks(
        @RequestParam(required = false) titlePrefix: String?,
        pageable: Pageable,
    ): ResponseEntity<Page<SharedWordbookListView>> {
        val result = sharedWordbookService.paginateAllSharedWordbooks(
            titlePrefix = titlePrefix,
            pageable = pageable,
        )
        return ResponseEntity.ok(result)
    }

    /**
     * 공개된 단어장 목록 조회
     * PUBLIC 상태의 단어장만 조회되며 Library page에 들어갔을 때 쏘는 API 입니다.
     */
    @GetMapping
    fun getSharedWordbooks(
        @RequestParam(required = false) titlePrefix: String?,
        @RequestParam(required = false) trustedLevels: List<TrustedLevel>?,
        @RequestParam(defaultValue = "CREATED_AT_DESC") sortType: SharedWordbookSortType,
        pageable: Pageable,
    ): ResponseEntity<Page<SharedWordbookListView>> {
        val result = sharedWordbookService.paginatePublicSharedWordbooks(
            titlePrefix = titlePrefix,
            trustedLevels = trustedLevels ?: emptyList(),
            sortType = sortType,
            pageable = pageable,
        )
        return ResponseEntity.ok(result)
    }

    /**
     * 단어장에 적힌 Detail을 보는 API 입니다.
     * PUBLIC 상태인 것만 조회됩니다.
     */
    @GetMapping("/{sharedWordbookId}")
    fun getSharedWordbookDetail(
        @PathVariable sharedWordbookId: Long,
    ): ResponseEntity<SharedWordbookDetailView> {
        val detail = sharedWordbookService.getSharedWordbookDetail(sharedWordbookId)
        return ResponseEntity.ok(detail)
    }

    /**
     * 단어장 버전 조회 API 입니다.
     * PUBLIC 상태인 것만 조회됩니다.
     */
    @GetMapping("/{sharedWordbookId}/versions")
    fun getSharedWordbookVersions(
        @PathVariable sharedWordbookId: Long,
        pageable: Pageable,
    ): ResponseEntity<Page<SharedWordbookVersionView>> {
        val result = sharedWordbookService.getSharedWordbookVersions(sharedWordbookId, pageable)
        return ResponseEntity.ok(result)
    }

    /**
     * 단어장 버전별 단어 조회 API 입니다.
     * PUBLIC 상태인 것만 조회됩니다.
     */
    @GetMapping("/{sharedWordbookId}/versions/{version}/words")
    fun getSharedWordsByVersion(
        @PathVariable sharedWordbookId: Long,
        @PathVariable version: Long,
    ): ResponseEntity<List<SharedWordSnapshotResponse>> {
        val words = sharedWordbookService.getSharedWordsByVersion(sharedWordbookId, version)
        return ResponseEntity.ok(words.map { SharedWordSnapshotResponse.from(it) })
    }

    @PostMapping
    fun shareOwnWordbook(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @RequestBody request: ShareMyWordbookRequest,
    ): ResponseEntity<SharedWordbookResponse> {
        val wordbook = sharedWordbookService.shareOwnWordbook(
            userId = userPrincipal.id,
            title = request.title,
            description = request.description,
            myWordbookId = request.myWordbookId,
            versionNote = request.versionNote,
        )
        return ResponseEntity.ok(SharedWordbookResponse.from(wordbook))
    }

    @PatchMapping("/{sharedWordbookId}")
    fun updateSharedWordbookMetadata(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @PathVariable sharedWordbookId: Long,
        @RequestBody request: UpdateSharedWordbookMetadataRequest,
    ): ResponseEntity<SharedWordbookResponse> {
        val wordbook = sharedWordbookService.updateSharedWordbookMetadata(
            userId = userPrincipal.id,
            sharedWordbookId = sharedWordbookId,
            title = request.title,
            description = request.description,
        )
        return ResponseEntity.ok(SharedWordbookResponse.from(wordbook))
    }

    @PostMapping("/{sharedWordbookId}/versions")
    fun makeNewVersion(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @PathVariable sharedWordbookId: Long,
        @RequestBody request: MakeNewVersionRequest,
    ): ResponseEntity<SharedWordbookRevisionResponse> {
        val revision = sharedWordbookService.makeNewVersion(
            userId = userPrincipal.id,
            sharedWordbookId = sharedWordbookId,
            myWordbookId = request.myWordbookId,
            versionNote = request.versionNote,
        )
        return ResponseEntity.ok(SharedWordbookRevisionResponse.from(revision))
    }

    @DeleteMapping("/{sharedWordbookId}")
    fun deleteMySharedWordbook(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @PathVariable sharedWordbookId: Long,
    ): ResponseEntity<SharedWordbookResponse> {
        val wordbook = sharedWordbookService.deleteMySharedWordbook(
            userId = userPrincipal.id,
            sharedWordbookId = sharedWordbookId,
        )
        return ResponseEntity.ok(SharedWordbookResponse.from(wordbook))
    }

    @PatchMapping("/{sharedWordbookId}/ban")
    @PreAuthorize("hasRole('ADMIN')")
    fun banSharedWordbook(
        @PathVariable sharedWordbookId: Long,
    ): ResponseEntity<SharedWordbookResponse> {
        val wordbook = sharedWordbookService.banSharedWordbook(sharedWordbookId)
        return ResponseEntity.ok(SharedWordbookResponse.from(wordbook))
    }

    @PatchMapping("/{sharedWordbookId}/unban")
    @PreAuthorize("hasRole('ADMIN')")
    fun unbanSharedWordbook(
        @PathVariable sharedWordbookId: Long,
    ): ResponseEntity<SharedWordbookResponse> {
        val wordbook = sharedWordbookService.unbanSharedWordbook(sharedWordbookId)
        return ResponseEntity.ok(SharedWordbookResponse.from(wordbook))
    }

    @PatchMapping("/{sharedWordbookId}/trusted-level")
    @PreAuthorize("hasRole('ADMIN')")
    fun updateTrustedLevel(
        @PathVariable sharedWordbookId: Long,
        @RequestBody request: UpdateTrustedLevelRequest,
    ): ResponseEntity<SharedWordbookResponse> {
        val wordbook = sharedWordbookService.updateTrustedLevel(sharedWordbookId, request.trustedLevel)
        return ResponseEntity.ok(SharedWordbookResponse.from(wordbook))
    }
}
