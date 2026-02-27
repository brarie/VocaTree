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
@RequestMapping("/api/library/shared-wordbook-bundles")
class SharedWordbookBundleController(
    private val sharedWordbookBundleService: SharedWordbookBundleService,
) {

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

    @GetMapping("/{bundleId}")
    fun getSharedBundleDetail(
        @PathVariable bundleId: Long,
    ): ResponseEntity<SharedWordbookBundleDetailView> {
        val detail = sharedWordbookBundleService.getSharedWordbookBundleDetail(bundleId)
        return ResponseEntity.ok(detail)
    }

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
