package com.monovoca.server.wordbook.infrastructure.persistence.entity

import com.monovoca.server.common.domain.model.vocabulary.vo.SenseVo
import com.monovoca.server.common.domain.model.vocabulary.vo.components.PartOfSpeech
import com.monovoca.server.common.domain.model.vocabulary.vo.components.Spelling
import com.monovoca.server.wordbook.domain.model.WordbookSense
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.json.json

object WordbookSenseTable : LongIdTable("word_senses") {
    val wordId = reference("word_id", WordbookWordTable, onDelete = ReferenceOption.CASCADE)
    val definition = text("definition").nullable()
    val engDefinition = text("eng_definition").nullable()
    val britishPhonetic = varchar("british_phonetic", 100).nullable()
    val americanPhonetic = varchar("american_phonetic", 100).nullable()
    val partOfSpeech = enumerationByName("part_of_speech", 20, PartOfSpeech::class).nullable()
    val synonyms = json<List<String>>("synonyms", Json.Default).nullable()
    val antonyms = json<List<String>>("antonyms", Json.Default).nullable()
    val displayOrder = integer("display_order").default(0)
}

class WordbookSenseEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<WordbookSenseEntity>(WordbookSenseTable)

    var wordId by WordbookSenseTable.wordId
    var definition by WordbookSenseTable.definition
    var engDefinition by WordbookSenseTable.engDefinition
    var britishPhonetic by WordbookSenseTable.britishPhonetic
    var americanPhonetic by WordbookSenseTable.americanPhonetic
    var partOfSpeech by WordbookSenseTable.partOfSpeech
    var synonymsList by WordbookSenseTable.synonyms
    var antonymsList by WordbookSenseTable.antonyms
    var displayOrder by WordbookSenseTable.displayOrder

    val examples by WordbookExampleEntity referrersOn WordbookExampleTable.senseId

    fun toDomain(): WordbookSense = WordbookSense(
        id = this.id.value,
        wordId = this.wordId.value,
        sense = SenseVo(
            definition = this.definition,
            engDefinition = this.engDefinition,
            britishPhonetic = this.britishPhonetic,
            americanPhonetic = this.americanPhonetic,
            partOfSpeech = this.partOfSpeech,
            synonyms = synonymsList?.map { Spelling(it) } ?: emptyList(),
            antonyms = antonymsList?.map { Spelling(it) } ?: emptyList()
        ),
        displayOrder = this.displayOrder,
        examples = this.examples.sortedBy { it.displayOrder }.map { it.toDomain() }
    )

    fun applyDomain(wordbookSense: WordbookSense, wordId: Long) {
        this.wordId = EntityID(wordId, WordbookWordTable)
        this.definition = wordbookSense.sense.definition
        this.engDefinition = wordbookSense.sense.engDefinition
        this.britishPhonetic = wordbookSense.sense.britishPhonetic
        this.americanPhonetic = wordbookSense.sense.americanPhonetic
        this.partOfSpeech = wordbookSense.sense.partOfSpeech
        this.displayOrder = wordbookSense.displayOrder

        this.synonymsList = wordbookSense.sense.synonyms
            .map { it.value }
            .takeIf { it.isNotEmpty() }

        this.antonymsList = wordbookSense.sense.antonyms
            .map { it.value }
            .takeIf { it.isNotEmpty() }
    }
}
