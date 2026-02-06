package com.monovoca.server.wordbook.infrastructure.persistence.repository

import com.monovoca.server.wordbook.domain.model.WordbookBundle
import com.monovoca.server.wordbook.domain.model.enumerated.NodeType
import com.monovoca.server.wordbook.domain.port.WordbookBundleRepository
import com.monovoca.server.wordbook.infrastructure.persistence.entity.WordbookNodeEntity
import com.monovoca.server.wordbook.infrastructure.persistence.entity.WordbookNodeTable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class WordbookBundleRepositoryImpl : WordbookBundleRepository {

    override fun findAllByOwnerId(userId: String): List<WordbookBundle> = transaction {
        WordbookNodeEntity.find { WordbookNodeTable.ownerId eq UUID.fromString(userId) and (WordbookNodeTable.nodeType eq NodeType.BUNDLE) }
            .map { it.toDomain() as WordbookBundle }
    }

    override fun findById(id: Long): WordbookBundle? = transaction {
        WordbookNodeEntity.findById(id)?.let {
            it.toDomain() as WordbookBundle
        }
    }

    override fun save(wordbookBundle: WordbookBundle): WordbookBundle = transaction {
        val wordbookBundleEntity = if (wordbookBundle.id == null) {
            WordbookNodeEntity.new { applyDomain(wordbookBundle) }
        } else {
            WordbookNodeEntity.findById(wordbookBundle.id)?.apply { applyDomain(wordbookBundle) }
                ?: error("WordbookBundleEntity with ID ${wordbookBundle.id} not found")
        }

        wordbookBundleEntity.toDomain() as WordbookBundle
    }

    override fun delete(wordbookBundle: WordbookBundle): Unit = transaction {
        WordbookNodeEntity.findById(wordbookBundle.id!!)?.delete()
    }
}