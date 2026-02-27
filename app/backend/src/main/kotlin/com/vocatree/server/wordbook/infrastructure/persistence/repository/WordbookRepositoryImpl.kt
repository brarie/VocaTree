package com.vocatree.server.wordbook.infrastructure.persistence.repository

import com.vocatree.server.wordbook.domain.model.Wordbook
import com.vocatree.server.wordbook.domain.model.enumerated.NodeType
import com.vocatree.server.wordbook.domain.model.query.SimpleWordbookView
import com.vocatree.server.wordbook.domain.port.WordbookRepository
import com.vocatree.server.wordbook.infrastructure.persistence.entity.WordbookColumnSettingEntity
import com.vocatree.server.wordbook.infrastructure.persistence.entity.WordbookNodeEntity
import com.vocatree.server.wordbook.infrastructure.persistence.entity.WordbookNodeTable
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.selectAll
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class WordbookRepositoryImpl : WordbookRepository {

    override fun findById(id: Long): Wordbook? =
        WordbookNodeEntity.findById(id)?.let {
            it.toDomain() as Wordbook
        }

    override fun save(wordbook: Wordbook): Wordbook {
        val wordbookEntity = if (wordbook.id == null) {
            WordbookNodeEntity.new { applyDomain(wordbook) }
        } else {
            WordbookNodeEntity.findById(wordbook.id)?.apply { applyDomain(wordbook) }
                ?: error("WordbookEntity with ID ${wordbook.id} not found")
        }

        syncWordbookColumnSettings(wordbookEntity, wordbook)

        return wordbookEntity.toDomain() as Wordbook
    }

    private fun syncWordbookColumnSettings(
        wordbookEntity: WordbookNodeEntity,
        wordbook: Wordbook
    ) {
        val existingSettings = wordbookEntity.settings.associateBy { it.columnType }
        val wordbookId = wordbookEntity.id.value

        for ((columnType, setting) in wordbook.columns) {
            val existingSetting = existingSettings[columnType]
            if (existingSetting != null) {
                existingSetting.applyDomain(columnType, setting, wordbookId)
            } else {
                WordbookColumnSettingEntity.new {
                    applyDomain(columnType, setting, wordbookId)
                }
            }
        }

        for ((columnType, existingSetting) in existingSettings) {
            if (!wordbook.columns.containsKey(columnType)) {
                existingSetting.delete()
            }
        }
    }

    override fun delete(wordbook: Wordbook) {
        WordbookNodeEntity.findById(wordbook.id!!)?.delete()
    }

    override fun existsByBundleId(bundleId: Long): Boolean =
        WordbookNodeTable.select(WordbookNodeTable.id)
            .where { WordbookNodeTable.parentId eq bundleId }
            .empty().not()

    override fun findAllSimpleByOwnerId(ownerId: String): List<SimpleWordbookView> =
        WordbookNodeTable
            .selectAll()
            .where {
                (WordbookNodeTable.ownerId eq UUID.fromString(ownerId)) and
                (WordbookNodeTable.nodeType eq NodeType.SINGLE)
            }
            .orderBy(WordbookNodeTable.title to SortOrder.ASC)
            .map { row ->
                SimpleWordbookView(
                    id = row[WordbookNodeTable.id].value,
                    title = row[WordbookNodeTable.title],
                )
            }
}