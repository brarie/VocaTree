package com.monovoca.server.dictionary.infrastructure.persistence.repository

import com.monovoca.server.common.domain.model.vo.Spelling
import com.monovoca.server.dictionary.domain.model.word.PublicSense
import com.monovoca.server.dictionary.domain.model.word.PublicWord
import com.monovoca.server.dictionary.domain.port.DictionaryWordRepository
import com.monovoca.server.dictionary.infrastructure.persistence.entity.PublicExampleEntity
import com.monovoca.server.dictionary.infrastructure.persistence.entity.PublicSenseEntity
import com.monovoca.server.dictionary.infrastructure.persistence.entity.PublicWordEntity
import com.monovoca.server.dictionary.infrastructure.persistence.entity.PublicWordTable
import org.jetbrains.exposed.dao.load
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Repository

@Repository
class DictionaryWordRepositoryImpl : DictionaryWordRepository {

    override fun save(word: PublicWord): PublicWord = transaction {
        val wordEntity = if (word.id == null) {
            PublicWordEntity.new { applyDomain(word) }
        } else {
            PublicWordEntity.findById(word.id)?.apply { applyDomain(word) }
                ?: error("PublicWord with ID ${word.id} not found")
        }
        syncSenses(wordEntity, word)

        wordEntity.toDomain()
    }

    private fun syncSenses(wordEntity: PublicWordEntity, word: PublicWord) {
        val existingSenses = wordEntity.senses.associateBy { it.id.value }
        val incomingSenses = word.senses.associateBy { it.id }

        existingSenses.keys.subtract(incomingSenses.keys.filterNotNull().toSet()).forEach { id ->
            existingSenses[id]?.delete()
        }

        incomingSenses.forEach { (id, sense) ->
            if (id != null && id in existingSenses) {
                val entity = existingSenses[id]!!
                entity.applyDomain(sense, wordEntity.id.value)
                syncExamples(entity, sense)
            }
        }

        word.senses.filter { it.id == null || it.id !in existingSenses.keys }.forEach { sense ->
            val senseEntity = PublicSenseEntity.new {
                applyDomain(sense, wordEntity.id.value)
            }
            sense.examples.forEach { example ->
                PublicExampleEntity.new {
                    applyDomain(example, senseEntity.id.value)
                }
            }
        }
    }

    private fun syncExamples(senseEntity: PublicSenseEntity, sense: PublicSense) {
        val existingExamples = senseEntity.examples.associateBy { it.id.value }
        val incomingExamples = sense.examples.associateBy { it.id }

        existingExamples.keys.subtract(incomingExamples.keys.filterNotNull().toSet()).forEach { id ->
            existingExamples[id]?.delete()
        }

        incomingExamples.forEach { (id, example) ->
            if (id != null && id in existingExamples) {
                existingExamples[id]!!.applyDomain(example, senseEntity.id.value)
            }
        }

        sense.examples.filter { it.id == null || it.id !in existingExamples.keys }.forEach { example ->
            PublicExampleEntity.new {
                applyDomain(example, senseEntity.id.value)
            }
        }
    }

    override fun existsBySpelling(spelling: Spelling): Boolean = transaction {
        PublicWordTable.select(PublicWordTable.id)
            .where { PublicWordTable.spelling eq spelling.value }
            .empty().not()
    }

    override fun findPublicWordById(wordId: Long): PublicWord? = transaction {
        PublicWordEntity.findById(wordId)
            ?.load(PublicWordEntity::senses, PublicSenseEntity::examples)
            ?.toDomain()
    }

    override fun delete(word: PublicWord) = transaction {
        PublicWordEntity.findById(word.id!!)!!.delete()
    }
}

