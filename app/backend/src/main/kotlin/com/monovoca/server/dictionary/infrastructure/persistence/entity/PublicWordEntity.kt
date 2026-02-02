package com.monovoca.server.dictionary.infrastructure.persistence.entity


import com.monovoca.server.common.domain.model.vo.Spelling
import com.monovoca.server.dictionary.domain.model.query.PublicWordSearchView
import com.monovoca.server.dictionary.domain.model.word.PublicWord
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable

object PublicWordTable : LongIdTable("public_words") {
    val spelling = varchar("spelling", 100).uniqueIndex()
    val slug = varchar("slug", 200).uniqueIndex().nullable()
}

class PublicWordEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<PublicWordEntity>(PublicWordTable)

    var spelling by PublicWordTable.spelling
    var slug by PublicWordTable.slug

    val senses by PublicSenseEntity referrersOn PublicSenseTable.wordId

    fun toDomain(): PublicWord = PublicWord(
        id = this.id.value,
        spelling = Spelling(this.spelling),
        slug = this.slug,
        senses = this.senses.sortedBy { it.displayOrder }.map { it.toDomain() }
    )

    fun applyDomain(publicWord: PublicWord) {
        spelling = publicWord.spelling.value
        slug = publicWord.slug
    }
}

fun PublicWordEntity.toPublicWordSearchView() = PublicWordSearchView(
    id = this.id.value,
    spelling = this.spelling,
    slug = this.slug!!,
    topDefinitions = this.senses
        .sortedBy { it.displayOrder }
        .take(3)
        .map { it.definition }
)
