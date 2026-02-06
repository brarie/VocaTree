package com.monovoca.server.wordbook.fixture

import com.monovoca.server.wordbook.domain.model.Wordbook
import java.time.LocalDateTime

object WordbookFixture {

    fun create(
        id: Long? = null,
        title: String = "테스트 단어장",
        ownerId: String = "test-owner-id",
        isStarred: Boolean = false,
        createdAt: LocalDateTime = LocalDateTime.of(2025, 1, 1, 12, 0),
        bundleId: Long? = null,
    ): Wordbook {
        return Wordbook(
            id = id,
            title = title,
            ownerId = ownerId,
            isStarred = isStarred,
            createdAt = createdAt,
            bundleId = bundleId,
        )
    }
}
