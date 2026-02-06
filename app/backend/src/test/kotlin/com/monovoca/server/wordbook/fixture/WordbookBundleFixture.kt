package com.monovoca.server.wordbook.fixture

import com.monovoca.server.wordbook.domain.model.WordbookBundle
import java.time.LocalDateTime

object WordbookBundleFixture {

    fun create(
        id: Long? = null,
        title: String = "테스트 묶음",
        ownerId: String = "test-owner-id",
        isStarred: Boolean = false,
        createdAt: LocalDateTime = LocalDateTime.of(2025, 1, 1, 12, 0),
    ): WordbookBundle {
        return WordbookBundle(
            id = id,
            title = title,
            ownerId = ownerId,
            isStarred = isStarred,
            createdAt = createdAt,
        )
    }
}
