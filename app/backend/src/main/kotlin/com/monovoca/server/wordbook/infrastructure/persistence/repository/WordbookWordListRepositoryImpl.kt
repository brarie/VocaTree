package com.monovoca.server.wordbook.infrastructure.persistence.repository

import com.monovoca.server.wordbook.domain.model.WordbookSense
import com.monovoca.server.wordbook.domain.model.WordbookWord
import com.monovoca.server.wordbook.domain.model.WordbookWordList
import com.monovoca.server.wordbook.domain.port.WordbookWordListRepository
import com.monovoca.server.wordbook.infrastructure.persistence.entity.WordbookExampleEntity
import com.monovoca.server.wordbook.infrastructure.persistence.entity.WordbookSenseEntity
import com.monovoca.server.wordbook.infrastructure.persistence.entity.WordbookWordEntity
import com.monovoca.server.wordbook.infrastructure.persistence.entity.WordbookWordTable
import org.jetbrains.exposed.dao.with
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Repository

@Repository
class WordbookWordListRepositoryImpl : WordbookWordListRepository {

    fun toWordbookWordList(wordbookId: Long, wordList: List<WordbookWord>): WordbookWordList {
        return WordbookWordList(
            wordbookId = wordbookId,
            wordList = wordList.sortedBy { it.displayOrder }
        )
    }

    override fun findById(wordbookId: Long): WordbookWordList = transaction {
        val wordList = WordbookWordEntity.find { WordbookWordTable.wordbookId eq wordbookId }
            .with(WordbookWordEntity::senses, WordbookSenseEntity::examples)
            .map { it.toDomain() }

        toWordbookWordList(
            wordbookId = wordbookId,
            wordList = wordList
        )
    }

    override fun save(wordbookWordList: WordbookWordList): WordbookWordList = transaction {
        val existingWords = WordbookWordEntity.find { WordbookWordTable.wordbookId eq wordbookWordList.wordbookId }
            .with(WordbookWordEntity::senses, WordbookSenseEntity::examples)
            .associateBy { it.id.value }

        val incomingWords = wordbookWordList.wordList.associateBy { it.id }

        existingWords.keys.subtract(incomingWords.keys.filterNotNull().toSet()).forEach { id ->
            existingWords[id]?.delete()
        }

        wordbookWordList.wordList.forEach { word ->
            val wordEntity = if (word.id != null && word.id in existingWords) {
                val entity = existingWords[word.id]!!
                entity.applyDomain(word, wordbookWordList.wordbookId)
                entity
            } else {
                WordbookWordEntity.new {
                    applyDomain(word, wordbookWordList.wordbookId)
                }
            }
            syncSenses(wordEntity, word)
        }

        val savedWords = WordbookWordEntity.find { WordbookWordTable.wordbookId eq wordbookWordList.wordbookId }
            .with(WordbookWordEntity::senses, WordbookSenseEntity::examples)
            .map { it.toDomain() }

        toWordbookWordList(wordbookWordList.wordbookId, savedWords)
    }

    private fun syncSenses(wordEntity: WordbookWordEntity, word: WordbookWord) {
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
            val senseEntity = WordbookSenseEntity.new {
                applyDomain(sense, wordEntity.id.value)
            }
            sense.examples.forEach { example ->
                WordbookExampleEntity.new {
                    applyDomain(example, senseEntity.id.value)
                }
            }
        }
    }

    private fun syncExamples(senseEntity: WordbookSenseEntity, sense: WordbookSense) {
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
            WordbookExampleEntity.new {
                applyDomain(example, senseEntity.id.value)
            }
        }
    }
}