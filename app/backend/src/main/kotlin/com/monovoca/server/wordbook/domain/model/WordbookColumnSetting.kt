package com.monovoca.server.wordbook.domain.model

import com.monovoca.server.wordbook.domain.model.enumerated.ColumnType

data class WordbookColumnSetting(
    val id: Long? = null,
    val isActive: Boolean,
    val width: Int,
) {

    companion object {
        fun create(columnType: ColumnType, isActive: Boolean) = WordbookColumnSetting(
            isActive = isActive,
            width = columnType.defaultWidth
        )
    }

    fun updateWidth(newWidth: Int): WordbookColumnSetting =
        this.copy(width = newWidth)

    fun toggleActiveStatus(): WordbookColumnSetting =
        this.copy(isActive = !isActive)
}