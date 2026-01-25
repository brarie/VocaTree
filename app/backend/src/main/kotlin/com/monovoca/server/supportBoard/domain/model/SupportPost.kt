package com.monovoca.server.supportBoard.domain.model

import com.monovoca.server.common.domain.port.holder.LocalDateTimeHolder
import java.time.LocalDateTime

data class SupportPost(
    val id: Long? = null,
    val title: String,
    val content: String,
    val authorId: String,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val supportCategory: SupportCategory,
) {

    init {
        require(supportCategory.isSubCategory()) { "지원 게시글의 카테고리는 항상 중분류에 속해야 합니다." }
    }

    companion object {
        fun create(
            title: String,
            content: String,
            authorId: String,
            supportCategory: SupportCategory,
            localDateTimeHolder: LocalDateTimeHolder
        ): SupportPost = SupportPost(
            title = title,
            content = content,
            authorId = authorId,
            supportCategory = supportCategory,
            createdAt = localDateTimeHolder.now()
        )
    }

    fun update(toTitle: String, toContent: String, supportCategory: SupportCategory): SupportPost {
        return this.copy(
            title = toTitle,
            content = toContent,
            supportCategory = supportCategory
        )
    }
}