package com.monovoca.server.dictionary.domain.port

import com.monovoca.server.common.domain.model.vo.Spelling
import com.monovoca.server.dictionary.domain.model.word.PublicWord

interface DictionaryWordRepository {
    fun save(word: PublicWord): PublicWord
    fun existsBySpelling(spelling: Spelling): Boolean
    fun findPublicWordById(wordId: Long): PublicWord?
    fun delete(word: PublicWord)
}
