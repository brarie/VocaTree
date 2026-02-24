package com.vocatree.server.wordbook.infrastructure.persistence.repository

import com.vocatree.server.wordbook.domain.model.WordbookBundle
import com.vocatree.server.wordbook.domain.model.enumerated.NodeType
import com.vocatree.server.wordbook.domain.port.WordbookBundleRepository
import com.vocatree.server.wordbook.infrastructure.persistence.entity.WordbookNodeEntity
import com.vocatree.server.wordbook.infrastructure.persistence.entity.WordbookNodeTable
import org.jetbrains.exposed.sql.and
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class WordbookBundleRepositoryImpl : WordbookBundleRepository {

    override fun findAllByOwnerId(userId: String): List<WordbookBundle> =
        WordbookNodeEntity.find { WordbookNodeTable.ownerId eq UUID.fromString(userId) and (WordbookNodeTable.nodeType eq NodeType.BUNDLE) }
            .map { it.toDomain() as WordbookBundle }

    override fun findById(id: Long): WordbookBundle? =
        WordbookNodeEntity.findById(id)?.let {
            it.toDomain() as WordbookBundle
        }

    override fun save(wordbookBundle: WordbookBundle): WordbookBundle {
        val wordbookBundleEntity = if (wordbookBundle.id == null) {
            WordbookNodeEntity.new { applyDomain(wordbookBundle) }
        } else {
            WordbookNodeEntity.findById(wordbookBundle.id)?.apply { applyDomain(wordbookBundle) }
                ?: error("WordbookBundleEntity with ID ${wordbookBundle.id} not found")
        }

        return wordbookBundleEntity.toDomain() as WordbookBundle
    }

    override fun delete(wordbookBundle: WordbookBundle) {
        WordbookNodeEntity.findById(wordbookBundle.id!!)?.delete()
    }
}