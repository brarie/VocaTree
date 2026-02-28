package com.vocatree.server.library.infrastructure.web

import com.vocatree.server.common.infrastructure.UserPrincipal
import com.vocatree.server.library.application.SharedWordbookBundleService
import com.vocatree.server.library.domain.model.enumerated.TrustedLevel
import com.vocatree.server.library.domain.model.query.SharedWordbookBundleDetailView
import com.vocatree.server.library.domain.model.query.SharedWordbookBundleListView
import com.vocatree.server.library.domain.model.query.SharedWordbookListView
import com.vocatree.server.library.domain.port.options.SharedWordbookBundleSortType
import com.vocatree.server.library.infrastructure.web.request.MakeSharedBundleRequest
import com.vocatree.server.library.infrastructure.web.request.UpdateBundleTrustedLevelRequest
import com.vocatree.server.library.infrastructure.web.request.UpdateSharedBundleMetadataRequest
import com.vocatree.server.library.infrastructure.web.response.SharedWordbookBundleResponse
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
@RequestMapping("/api/library/shared-wordbook-bundles")
class SharedWordbookBundleController(
    private val sharedWordbookBundleService: SharedWordbookBundleService,
) {

    /**
     * 자신의 공유 번들을 다 볼 수 있음.
     * SharedState가 무엇이든 간에 자신의 번들은 다 볼 수 있습니다.
     */
    @GetMapping("/my")
    fun getMySharedBundles(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        pageable: Pageable,
    ): ResponseEntity<Page<SharedWordbookBundleListView>> {
        val result = sharedWordbookBundleService.getMySharedBundles(
            userId = userPrincipal.id,
            pageable = pageable,
        )
        return ResponseEntity.ok(result)
    }

    /**
     * ADMIN은 상태에 관계 없이 모든 사람들의 공유 번들을 다 볼 수 있음.
     */
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    fun getAllSharedBundles(
        @RequestParam(required = false) titlePrefix: String?,
        pageable: Pageable,
    ): ResponseEntity<Page<SharedWordbookBundleListView>> {
        val result = sharedWordbookBundleService.paginateAllSharedBundles(
            titlePrefix = titlePrefix,
            pageable = pageable,
        )
        return ResponseEntity.ok(result)
    }

    /**
     * 공개된 번들 조회 API
     * PUBLIC 상태의 번들만 조회되며 Library page에 들어갔을 때 쏘는 API 입니다.
     */
    @GetMapping
    fun getSharedBundles(
        @RequestParam(required = false) titlePrefix: String?,
        @RequestParam(required = false) trustedLevels: List<TrustedLevel>?,
        @RequestParam(defaultValue = "CREATED_AT_DESC") sortType: SharedWordbookBundleSortType,
        pageable: Pageable,
    ): ResponseEntity<Page<SharedWordbookBundleListView>> {
        val result = sharedWordbookBundleService.paginatePublicSharedBundles(
            titlePrefix = titlePrefix,
            trustedLevels = trustedLevels ?: emptyList(),
            sortType = sortType,
            pageable = pageable,
        )
        return ResponseEntity.ok(result)
    }

    /**
     * 단어장 번들의 Detail을 보는 API 입니다.
     * PUBLIC 상태인 것만 조회됩니다.
     */
    @GetMapping("/{bundleId}")
    fun getSharedBundleDetail(
        @PathVariable bundleId: Long,
    ): ResponseEntity<SharedWordbookBundleDetailView> {
        val detail = sharedWordbookBundleService.getSharedWordbookBundleDetail(bundleId)
        return ResponseEntity.ok(detail)
    }

    /**
     * 단어장 번들에 속한 단어장들을 조회하는 API 입니다.
     * PUBLIC 상태인 번들만 조회됩니다.
     * 단, 여기서는 모든 상태의 단어장들이 조회됩니다. 사용자의 편의를 위해 그렇게 구성되었습니다.
     */
    @GetMapping("/{bundleId}/wordbooks")
    fun getWordbooksInBundle(
        @PathVariable bundleId: Long,
        pageable: Pageable,
    ): ResponseEntity<Page<SharedWordbookListView>> {
        val result = sharedWordbookBundleService.paginatePublicSharedWordbooksInBundle(bundleId, pageable)
        return ResponseEntity.ok(result)
    }

    @PostMapping
    fun makeSharedBundle(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @RequestBody request: MakeSharedBundleRequest,
    ): ResponseEntity<SharedWordbookBundleResponse> {
        val bundle = sharedWordbookBundleService.makeSharedBundle(
            userId = userPrincipal.id,
            title = request.title,
            description = request.description,
        )
        return ResponseEntity.ok(SharedWordbookBundleResponse.from(bundle))
    }

    @PatchMapping("/{bundleId}")
    fun updateSharedBundleMetadata(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @PathVariable bundleId: Long,
        @RequestBody request: UpdateSharedBundleMetadataRequest,
    ): ResponseEntity<SharedWordbookBundleResponse> {
        val bundle = sharedWordbookBundleService.updateSharedBundleMetadata(
            userId = userPrincipal.id,
            bundleId = bundleId,
            title = request.title,
            description = request.description,
        )
        return ResponseEntity.ok(SharedWordbookBundleResponse.from(bundle))
    }

    @DeleteMapping("/{bundleId}")
    fun deleteSharedBundle(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @PathVariable bundleId: Long,
    ): ResponseEntity<SharedWordbookBundleResponse> {
        val bundle = sharedWordbookBundleService.deleteSharedBundle(
            userId = userPrincipal.id,
            bundleId = bundleId,
        )
        return ResponseEntity.ok(SharedWordbookBundleResponse.from(bundle))
    }

    @PostMapping("/{bundleId}/wordbooks/{sharedWordbookId}")
    fun addWordbookToBundle(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @PathVariable bundleId: Long,
        @PathVariable sharedWordbookId: Long,
    ): ResponseEntity<Unit> {
        sharedWordbookBundleService.addSharedWordbookToBundle(
            userId = userPrincipal.id,
            bundleId = bundleId,
            sharedWordbookId = sharedWordbookId,
        )
        return ResponseEntity.ok().build()
    }

    @DeleteMapping("/{bundleId}/wordbooks/{sharedWordbookId}")
    fun removeWordbookFromBundle(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @PathVariable bundleId: Long,
        @PathVariable sharedWordbookId: Long,
    ): ResponseEntity<Unit> {
        sharedWordbookBundleService.removeSharedWordbookFromBundle(
            userId = userPrincipal.id,
            bundleId = bundleId,
            sharedWordbookId = sharedWordbookId,
        )
        return ResponseEntity.ok().build()
    }

    @PatchMapping("/{bundleId}/ban")
    @PreAuthorize("hasRole('ADMIN')")
    fun banSharedBundle(
        @PathVariable bundleId: Long,
    ): ResponseEntity<SharedWordbookBundleResponse> {
        val bundle = sharedWordbookBundleService.banSharedBundle(bundleId)
        return ResponseEntity.ok(SharedWordbookBundleResponse.from(bundle))
    }

    @PatchMapping("/{bundleId}/unban")
    @PreAuthorize("hasRole('ADMIN')")
    fun unbanSharedBundle(
        @PathVariable bundleId: Long,
    ): ResponseEntity<SharedWordbookBundleResponse> {
        val bundle = sharedWordbookBundleService.unbanSharedBundle(bundleId)
        return ResponseEntity.ok(SharedWordbookBundleResponse.from(bundle))
    }

    @PatchMapping("/{bundleId}/trusted-level")
    @PreAuthorize("hasRole('ADMIN')")
    fun updateTrustedLevel(
        @PathVariable bundleId: Long,
        @RequestBody request: UpdateBundleTrustedLevelRequest,
    ): ResponseEntity<SharedWordbookBundleResponse> {
        val bundle = sharedWordbookBundleService.updateTrustedLevel(bundleId, request.trustedLevel)
        return ResponseEntity.ok(SharedWordbookBundleResponse.from(bundle))
    }
}
