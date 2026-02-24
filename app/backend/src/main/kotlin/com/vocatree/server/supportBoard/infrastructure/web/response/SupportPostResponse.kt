package com.vocatree.server.supportBoard.infrastructure.web.response

import com.vocatree.server.supportBoard.domain.model.SupportPost
import java.time.LocalDateTime

data class SupportPostResponse(
    val id: Long,
    val title: String,
    val content: String,
    val authorId: String,
    val createdAt: LocalDateTime,
    val categoryId: Long,
    val categoryName: String,
    val parentCategoryId: Long?,
    val parentCategoryName: String?
) {
    companion object {
        fun from(post: SupportPost) = SupportPostResponse(
            id = post.id!!,
            title = post.title,
            content = post.content,
            authorId = post.authorId,
            createdAt = post.createdAt,
            categoryId = post.supportCategory.id!!,
            categoryName = post.supportCategory.categoryName,
            parentCategoryId = post.supportCategory.parentCategory?.id,
            parentCategoryName = post.supportCategory.parentCategory?.categoryName
        )
    }
}
