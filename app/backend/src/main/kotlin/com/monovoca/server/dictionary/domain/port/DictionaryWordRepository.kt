package com.monovoca.server.dictionary.domain.port

import com.monovoca.server.common.domain.model.vo.Spelling
import com.monovoca.server.dictionary.domain.model.query.PublicWordSearchView
import com.monovoca.server.dictionary.domain.model.word.PublicWord
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface DictionaryWordRepository {
    fun save(word: PublicWord): PublicWord
    fun existsBySpelling(spelling: Spelling): Boolean
    fun existsBySlug(slug: String): Boolean
    fun findById(wordId: Long): PublicWord?
    fun findBySlug(slug: String): PublicWord?
    fun paginateViewsBySpelling(query: String, pageable: Pageable): Page<PublicWordSearchView>
    fun delete(word: PublicWord)
}
