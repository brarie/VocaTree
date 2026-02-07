package com.monovoca.server.wordbook.infrastructure.persistence.entity

import com.monovoca.server.common.domain.model.vocabulary.vo.components.Spelling
import com.monovoca.server.wordbook.domain.model.WordbookWord
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object WordbookWordTable : LongIdTable("words") {
    val wordbookId = reference("wordbook_id", WordbookNodeTable, onDelete = ReferenceOption.CASCADE)
    val spelling = varchar("spelling", 100).nullable()
    val displayOrder = integer("display_order").default(0)
}

class WordbookWordEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<WordbookWordEntity>(WordbookWordTable)

    var wordbookId by WordbookWordTable.wordbookId
    var spelling by WordbookWordTable.spelling
    var displayOrder by WordbookWordTable.displayOrder

    val senses by WordbookSenseEntity referrersOn WordbookSenseTable.wordId

    fun toDomain(): WordbookWord = WordbookWord(
        id = this.id.value,
        spelling = this.spelling?.let { Spelling(it) },
        displayOrder = this.displayOrder,
        senses = this.senses.sortedBy { it.displayOrder }.map { it.toDomain() }
    )

    fun applyDomain(wordbookWord: WordbookWord, wordbookId: Long) {
        this.wordbookId = EntityID(wordbookId, WordbookNodeTable)
        this.spelling = wordbookWord.spelling?.value
        this.displayOrder = wordbookWord.displayOrder
    }
}
