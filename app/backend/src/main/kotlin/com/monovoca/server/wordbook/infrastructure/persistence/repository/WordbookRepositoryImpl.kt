package com.monovoca.server.wordbook.infrastructure.persistence.repository

import com.monovoca.server.wordbook.domain.model.Wordbook
import com.monovoca.server.wordbook.domain.port.WordbookRepository
import com.monovoca.server.wordbook.infrastructure.persistence.entity.WordbookColumnSettingEntity
import com.monovoca.server.wordbook.infrastructure.persistence.entity.WordbookNodeEntity
import com.monovoca.server.wordbook.infrastructure.persistence.entity.WordbookNodeTable
import org.springframework.stereotype.Repository

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
}