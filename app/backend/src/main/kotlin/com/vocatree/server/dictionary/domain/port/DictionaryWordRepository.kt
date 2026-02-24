package com.vocatree.server.dictionary.domain.port

import com.vocatree.server.common.domain.model.vocabulary.vo.components.Spelling
import com.vocatree.server.dictionary.domain.model.query.DictionaryCountView
import com.vocatree.server.dictionary.domain.model.query.PublicWordSimpleView
import com.vocatree.server.dictionary.domain.model.word.PublicWord
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface DictionaryWordRepository {
    fun save(word: PublicWord): PublicWord
    fun existsBySpelling(spelling: Spelling): Boolean
    fun existsBySlug(slug: String): Boolean
    fun findById(wordId: Long): PublicWord?
    fun findBySlug(slug: String): PublicWord?
    fun findBySpelling(spelling: Spelling): PublicWord?
    fun paginateViewsBySpelling(query: String, pageable: Pageable): Page<PublicWordSimpleView>
    fun delete(word: PublicWord)
    fun getDictionaryCounts(): DictionaryCountView
}
