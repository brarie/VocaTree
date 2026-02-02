package com.monovoca.server.dictionary.infrastructure.persistence.entity

import com.monovoca.server.common.domain.model.vo.Spelling
import com.monovoca.server.common.domain.model.vo.enumerated.PartOfSpeech
import com.monovoca.server.dictionary.domain.model.word.PublicSense
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.json.json

object PublicSenseTable : LongIdTable("public_senses") {
    val wordId = reference("word_id", PublicWordTable, onDelete = ReferenceOption.CASCADE)
    val synonyms = json<List<String>>("synonyms", Json.Default).nullable()
    val antonyms = json<List<String>>("antonyms", Json.Default).nullable()
    val definition = text("definition")
    val engDefinition = text("eng_definition").nullable()
    val partOfSpeech = enumerationByName("part_of_speech", 20, PartOfSpeech::class).nullable()
    val britishPhonetic = varchar("british_phonetic", 100).nullable()
    val americanPhonetic = varchar("american_phonetic", 100).nullable()
    val displayOrder = integer("display_order").default(0)
}

class PublicSenseEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<PublicSenseEntity>(PublicSenseTable)

    var wordId by PublicSenseTable.wordId

    var synonymsList by PublicSenseTable.synonyms
    var antonymsList by PublicSenseTable.antonyms
    var definition by PublicSenseTable.definition
    var engDefinition by PublicSenseTable.engDefinition
    var partOfSpeech by PublicSenseTable.partOfSpeech
    var britishPhonetic by PublicSenseTable.britishPhonetic
    var americanPhonetic by PublicSenseTable.americanPhonetic
    var displayOrder by PublicSenseTable.displayOrder

    val examples by PublicExampleEntity referrersOn PublicExampleTable.senseId

    fun toDomain(): PublicSense = PublicSense(
        id = this.id.value,
        definition = this.definition,
        engDefinition = this.engDefinition,
        britishPhonetic = this.britishPhonetic,
        americanPhonetic = this.americanPhonetic,
        synonyms = synonymsList?.map { Spelling(it) } ?: emptyList(),
        antonyms = antonymsList?.map { Spelling(it) } ?: emptyList(),
        partOfSpeech = this.partOfSpeech,
        displayOrder = this.displayOrder,
        examples = this.examples.sortedBy { it.displayOrder }.map { it.toDomain() }
    )

    fun applyDomain(publicSense: PublicSense, wordId: Long) {
        this.wordId = EntityID(wordId, PublicWordTable)
        this.definition = publicSense.definition
        this.engDefinition = publicSense.engDefinition
        this.britishPhonetic = publicSense.britishPhonetic
        this.americanPhonetic = publicSense.americanPhonetic
        this.partOfSpeech = publicSense.partOfSpeech
        this.displayOrder = publicSense.displayOrder

        this.synonymsList = publicSense.synonyms
            .map { it.value }
            .takeIf { it.isNotEmpty() }

        this.antonymsList = publicSense.antonyms
            .map { it.value }
            .takeIf { it.isNotEmpty() }
    }
}

