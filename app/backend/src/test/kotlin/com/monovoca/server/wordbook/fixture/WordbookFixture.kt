package com.monovoca.server.wordbook.fixture

import com.monovoca.server.wordbook.domain.model.Wordbook
import com.monovoca.server.wordbook.domain.model.WordbookColumnSetting
import com.monovoca.server.wordbook.domain.model.enumerated.ColumnType
import java.time.LocalDateTime

object WordbookFixture {

    fun create(
        id: Long? = null,
        title: String = "테스트 단어장",
        ownerId: String = "test-owner-id",
        isStarred: Boolean = false,
        createdAt: LocalDateTime = LocalDateTime.of(2025, 1, 1, 12, 0),
        bundleId: Long? = null,
        columns: Map<ColumnType, WordbookColumnSetting>? = null
    ): Wordbook {
        val defaultColumns = columns ?: ColumnType.entries
            .filter { it.isDefaultEnabled }
            .associateWith {
                WordbookColumnSetting(
                    isActive = true,
                    width = it.defaultWidth
                )
            }

        return Wordbook(
            id = id,
            title = title,
            ownerId = ownerId,
            isStarred = isStarred,
            createdAt = createdAt,
            bundleId = bundleId,
            columns = defaultColumns
        )
    }
}
