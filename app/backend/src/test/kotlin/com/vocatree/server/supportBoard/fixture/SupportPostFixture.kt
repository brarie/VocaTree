package com.vocatree.server.supportBoard.fixture

import com.vocatree.server.supportBoard.domain.model.SupportCategory
import com.vocatree.server.supportBoard.domain.model.SupportPost
import java.time.LocalDateTime

object SupportPostFixture {

    fun create(
        id: Long? = null,
        title: String = "테스트 게시글 제목",
        content: String = "테스트 게시글 내용입니다.",
        authorId: String = "00000000-0000-0000-0000-000000000000",
        createdAt: LocalDateTime = LocalDateTime.of(2025, 1, 1, 12, 0, 0),
        supportCategory: SupportCategory = SupportCategoryFixture.createSubCategory(),
    ): SupportPost {
        return SupportPost(
            id = id,
            title = title,
            content = content,
            authorId = authorId,
            createdAt = createdAt,
            supportCategory = supportCategory,
        )
    }
}
