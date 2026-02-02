package com.monovoca.server.dictionary.infrastructure.persistence.entity

import com.monovoca.server.dictionary.domain.model.word.PublicExample
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object PublicExampleTable : LongIdTable("public_examples") {
    val senseId = reference("sense_id", PublicSenseTable, onDelete = ReferenceOption.CASCADE)
    val sentence = text("sentence")
    val translation = text("translation").nullable()
    val isOfficial = bool("is_official").default(false)
    val sourceInfo = varchar("source_info", 200).nullable()
    val displayOrder = integer("display_order").default(0)
}

class PublicExampleEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<PublicExampleEntity>(PublicExampleTable)

    var senseId by PublicExampleTable.senseId

    var sentence by PublicExampleTable.sentence
    var translation by PublicExampleTable.translation
    var isOfficial by PublicExampleTable.isOfficial
    var sourceInfo by PublicExampleTable.sourceInfo
    var displayOrder by PublicExampleTable.displayOrder

    fun toDomain(): PublicExample = PublicExample(
        id = this.id.value,
        sentence = this.sentence,
        translation = this.translation,
        isOfficial = this.isOfficial,
        sourceInfo = this.sourceInfo,
        displayOrder = this.displayOrder
    )

    fun applyDomain(publicExample: PublicExample, senseId: Long) {
        this.senseId = EntityID(senseId, PublicSenseTable)
        this.sentence = publicExample.sentence
        this.translation = publicExample.translation
        this.isOfficial = publicExample.isOfficial
        this.sourceInfo = publicExample.sourceInfo
        this.displayOrder = publicExample.displayOrder
    }
}
