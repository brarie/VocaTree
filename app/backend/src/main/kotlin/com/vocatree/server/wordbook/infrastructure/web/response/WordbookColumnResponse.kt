package com.vocatree.server.wordbook.infrastructure.web.response

import com.vocatree.server.wordbook.domain.model.WordbookColumnSetting
import com.vocatree.server.wordbook.domain.model.enumerated.ColumnType

data class WordbookColumnResponse(
    val columns: Map<ColumnType, WordbookColumnSettingResponse>
) {

    data class WordbookColumnSettingResponse(
        val isActive: Boolean,
        val width: Int,
    ) {
        companion object {
            fun from(wordbookColumnSetting: WordbookColumnSetting): WordbookColumnSettingResponse =
                WordbookColumnSettingResponse(
                    isActive = wordbookColumnSetting.isActive,
                    width = wordbookColumnSetting.width
                )
        }
    }

    companion object {
        fun from(columns: Map<ColumnType, WordbookColumnSetting>): WordbookColumnResponse = WordbookColumnResponse(
            ColumnType.entries.associateWith { type ->
                val setting = columns[type]
                WordbookColumnSettingResponse(
                    isActive = setting?.isActive ?: false,
                    width = setting?.width ?: type.defaultWidth
                )
            }
        )
    }
}