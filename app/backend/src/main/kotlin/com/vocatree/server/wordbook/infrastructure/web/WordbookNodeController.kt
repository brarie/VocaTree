package com.vocatree.server.wordbook.infrastructure.web

import com.vocatree.server.common.infrastructure.UserPrincipal
import com.vocatree.server.wordbook.application.WordbookNodeService
import com.vocatree.server.wordbook.domain.model.query.NodeResponseWithCountView
import com.vocatree.server.wordbook.domain.port.options.WordbookNodeSortType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/wordbook-nodes")
class WordbookNodeController(
    private val wordbookNodeService: WordbookNodeService
) {

    @GetMapping
    fun paginateNodeResponse(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        pageable: Pageable,
        wordbookNodeSortType: WordbookNodeSortType,
        starOnly: Boolean = false
    ): ResponseEntity<Page<NodeResponseWithCountView>> = ResponseEntity.ok(
        wordbookNodeService.paginateNodesWithoutParent(userPrincipal.id, pageable, wordbookNodeSortType, starOnly)
    )

    @GetMapping("/{bundleId}")
    fun paginateNodeResponseInBundle(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @PathVariable bundleId: Long,
        pageable: Pageable
    ): ResponseEntity<Page<NodeResponseWithCountView>> = ResponseEntity.ok(
        wordbookNodeService.paginateNodesInBundle(userPrincipal.id, bundleId, pageable)
    )
}