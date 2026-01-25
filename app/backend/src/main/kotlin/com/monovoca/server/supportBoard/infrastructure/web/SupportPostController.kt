package com.monovoca.server.supportBoard.infrastructure.web

import com.monovoca.server.common.infrastructure.UserPrincipal
import com.monovoca.server.supportBoard.application.SupportPostService
import com.monovoca.server.supportBoard.domain.model.query.SupportPostView
import com.monovoca.server.supportBoard.infrastructure.web.request.CreateSupportPostRequest
import com.monovoca.server.supportBoard.infrastructure.web.request.UpdateSupportPostRequest
import com.monovoca.server.supportBoard.infrastructure.web.response.SupportPostResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/support-posts")
class SupportPostController(
    private val supportPostService: SupportPostService
) {

    @GetMapping
    fun getPosts(
        categoryId: Long,
        pageable: Pageable
    ): ResponseEntity<Page<SupportPostView>> {
        val posts = supportPostService.paginateViewsByCategoryTree(categoryId, pageable)
        return ResponseEntity.ok(posts)
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    fun createPost(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @RequestBody request: CreateSupportPostRequest
    ): ResponseEntity<SupportPostResponse> {
        val post = supportPostService.create(
            userId = userPrincipal.id,
            title = request.title,
            content = request.content,
            categoryId = request.categoryId
        )
        return ResponseEntity.ok(SupportPostResponse.from(post))
    }

    @PutMapping("/{postId}")
    @PreAuthorize("hasRole('ADMIN')")
    fun updatePost(
        @PathVariable postId: Long,
        @RequestBody request: UpdateSupportPostRequest
    ): ResponseEntity<SupportPostResponse> {
        val post = supportPostService.update(
            postId = postId,
            toTitle = request.title,
            toContent = request.content,
            toCategoryId = request.categoryId
        )
        return ResponseEntity.ok(SupportPostResponse.from(post))
    }

    @DeleteMapping("/{postId}")
    @PreAuthorize("hasRole('ADMIN')")
    fun deletePost(@PathVariable postId: Long): ResponseEntity<Unit> {
        supportPostService.delete(postId)
        return ResponseEntity.ok().build()
    }
}
