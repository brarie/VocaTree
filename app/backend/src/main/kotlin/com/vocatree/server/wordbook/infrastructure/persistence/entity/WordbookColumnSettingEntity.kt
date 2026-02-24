package com.vocatree.server.wordbook.infrastructure.persistence.entity

import com.vocatree.server.wordbook.domain.model.WordbookColumnSetting
import com.vocatree.server.wordbook.domain.model.enumerated.ColumnType
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption.CASCADE

object WordbookColumnSettingTable : LongIdTable(name = "wordbook_column_setting") {
    val wordbookId = reference("wordbook_id", WordbookNodeTable, onDelete = CASCADE)
    val columnType = enumerationByName("column_type", 50, ColumnType::class)
    val isActive = bool("is_active")
    val width = integer("width")
}

class WordbookColumnSettingEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<WordbookColumnSettingEntity>(WordbookColumnSettingTable)

    var columnType by WordbookColumnSettingTable.columnType
    var wordbookId by WordbookColumnSettingTable.wordbookId
    var isActive by WordbookColumnSettingTable.isActive
    var width by WordbookColumnSettingTable.width

    fun toDomain(): WordbookColumnSetting = WordbookColumnSetting(
        id = this.id.value,
        isActive = this.isActive,
        width = this.width
    )

    fun applyDomain(columnType: ColumnType, wordbookColumnSetting: WordbookColumnSetting, wordbookId: Long) {
        this.wordbookId = EntityID(wordbookId, WordbookNodeTable)
        this.columnType = columnType
        this.isActive = wordbookColumnSetting.isActive
        this.width = wordbookColumnSetting.width
    }
}