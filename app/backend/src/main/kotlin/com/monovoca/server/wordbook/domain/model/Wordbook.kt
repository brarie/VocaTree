package com.monovoca.server.wordbook.domain.model

import com.monovoca.server.wordbook.domain.exception.CannotDeactivateSpellingColumnException
import com.monovoca.server.wordbook.domain.exception.MissingParentDependencyColumnException
import com.monovoca.server.wordbook.domain.exception.ParentColumnDeactivationRestrictedException
import com.monovoca.server.wordbook.domain.exception.WordbookNotFoundException
import com.monovoca.server.wordbook.domain.model.enumerated.ColumnType
import com.monovoca.server.wordbook.domain.model.enumerated.NodeType
import java.time.LocalDateTime

// AGGREGATE //
data class Wordbook(
    override val id: Long? = null,
    override val title: String,
    override val ownerId: String,
    override val isStarred: Boolean = false,
    override val createdAt: LocalDateTime = LocalDateTime.now(),
    val bundleId: Long? = null,
    val columns: Map<ColumnType, WordbookColumnSetting>
) : WordbookNode() {

    companion object {
        fun create(ownerId: String, title: String): Wordbook {
            val defaultColumns = ColumnType.entries
                .filter { it.isDefaultEnabled }
                .associateWith {
                    WordbookColumnSetting(
                        isActive = true,
                        width = it.defaultWidth
                    )
                }

            return Wordbook(
                title = title,
                ownerId = ownerId,
                columns = defaultColumns
            )
        }
    }

    override val nodeType: NodeType = NodeType.SINGLE

    override fun updateTitle(newTitle: String): Wordbook =
        this.copy(title = newTitle)

    override fun toggleStarred(): Wordbook =
        this.copy(isStarred = !isStarred)

    override fun validateOwner(userId: String) {
        if (ownerId != userId)
            throw WordbookNotFoundException()
    }

    fun moveToBundle(newBundleId: Long?): Wordbook =
        this.copy(bundleId = newBundleId)

    fun updateColumnWidth(columnType: ColumnType, newWidth: Int): Wordbook {
        val updatedColumns = columns.toMutableMap()
        val updatedSetting = updatedColumns[columnType]?.updateWidth(newWidth)
            ?: WordbookColumnSetting.create(columnType = columnType, isActive = true).updateWidth(newWidth)

        updatedColumns[columnType] = updatedSetting

        return this.copy(columns = updatedColumns)
    }

    fun toggleColumnActiveStatus(columnType: ColumnType): Wordbook {
        /* Spelling 칼럼을 지우려고 하는 경우 */
        if (columnType == ColumnType.SPELLING)
            throw CannotDeactivateSpellingColumnException()

        val targetActiveStatus = columns[columnType]?.isActive ?: columnType.isDefaultEnabled

        /* Level 2가 전부 비활성화 된 상태에서 Level 3를 키려고 하는 경우 */
        val hasActiveLevel2 = ColumnType.entries.filter { it.level == 2 }
            .any { colType -> columns[colType]?.isActive ?: colType.isDefaultEnabled }
        if (columnType.level == 3 && !targetActiveStatus && !hasActiveLevel2)
            throw MissingParentDependencyColumnException()

        /* Level 3가 활성화 되어 있는 경우에서 모든 Level 2를 비활성화 하려고 하는 경우 */
        val hasActiveLevel3 = ColumnType.entries.filter { it.level == 3 }
            .any { colType -> columns[colType]?.isActive ?: colType.isDefaultEnabled }
        val remainingLevel2Count = ColumnType.entries.filter { it.level == 2 && it != columnType }
            .count { colType -> columns[colType]?.isActive ?: colType.isDefaultEnabled }
        if (columnType.level == 2 && targetActiveStatus && hasActiveLevel3 && remainingLevel2Count == 0)
            throw ParentColumnDeactivationRestrictedException()

        val updatedColumns = columns.toMutableMap()
        val updatedSetting = updatedColumns[columnType]?.toggleActiveStatus()
            ?: WordbookColumnSetting.create(columnType = columnType, isActive = true)

        updatedColumns[columnType] = updatedSetting

        return this.copy(columns = updatedColumns)
    }
}