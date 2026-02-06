package com.monovoca.server.wordbook.infrastructure.persistence.entity

import com.monovoca.server.common.domain.model.vocabulary.vo.ExampleVo
import com.monovoca.server.wordbook.domain.model.WordbookExample
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object WordbookExampleTable : LongIdTable("word_examples") {
    val senseId = reference("sense_id", WordbookSenseTable, onDelete = ReferenceOption.CASCADE)
    val sentence = text("sentence").nullable()
    val translation = text("translation").nullable()
    val displayOrder = integer("display_order").default(0)
}

class WordbookExampleEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<WordbookExampleEntity>(WordbookExampleTable)

    var senseId by WordbookExampleTable.senseId
    var sentence by WordbookExampleTable.sentence
    var translation by WordbookExampleTable.translation
    var displayOrder by WordbookExampleTable.displayOrder

    fun toDomain(): WordbookExample = WordbookExample(
        id = this.id.value,
        senseId = this.senseId.value,
        example = ExampleVo(
            sentence = this.sentence,
            translation = this.translation
        ),
        displayOrder = this.displayOrder
    )

    fun applyDomain(wordbookExample: WordbookExample, senseId: Long) {
        this.senseId = EntityID(senseId, WordbookSenseTable)
        this.sentence = wordbookExample.example.sentence
        this.translation = wordbookExample.example.translation
        this.displayOrder = wordbookExample.displayOrder
    }
}
