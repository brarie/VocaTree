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

/**
 * todo
 * 지금 전체적으로 권한 체킹이 부족함
 * SharedState 에 따라서 지금 권한 체킹이 되어야 함.
 * PUBLIC의 경우 모두가 볼 수 있고,
 * DELETED_BY_OWN의 경우 그거를 구독하고 있는 사람과 공유한 이만 볼 수 있고,
 * BANNED의 경우에는 아무도 볼 수 없게 해야 함.
 *
 * 결국 구독 테이블을 만들어야 가능한거라서 일단 보류.
 */
@RestController
@RequestMapping("/api/library/shared-wordbooks")
class SharedWordbookController(
    private val sharedWordbookService: SharedWordbookService,
) {

    @GetMapping("/my")
    fun getMySharedWordbooks(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        pageable: Pageable,
    ): ResponseEntity<Page<SharedWordbookListView>> {
        val result = sharedWordbookService.getMySharedWordbooks(userPrincipal.id, pageable)
        return ResponseEntity.ok(result)
    }

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

    @GetMapping("/{sharedWordbookId}")
    fun getSharedWordbookDetail(
        @PathVariable sharedWordbookId: Long,
    ): ResponseEntity<SharedWordbookDetailView> {
        val detail = sharedWordbookService.getSharedWordbookDetail(sharedWordbookId)
        return ResponseEntity.ok(detail)
    }

    @GetMapping("/{sharedWordbookId}/versions")
    fun getSharedWordbookVersions(
        @PathVariable sharedWordbookId: Long,
        pageable: Pageable,
    ): ResponseEntity<Page<SharedWordbookVersionView>> {
        val result = sharedWordbookService.getSharedWordbookVersions(sharedWordbookId, pageable)
        return ResponseEntity.ok(result)
    }

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
