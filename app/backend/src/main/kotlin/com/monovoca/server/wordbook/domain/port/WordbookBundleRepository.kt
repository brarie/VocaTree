package com.monovoca.server.wordbook.domain.port

import com.monovoca.server.wordbook.domain.model.WordbookBundle

interface WordbookBundleRepository {

    fun findAllByOwnerId(userId: String): List<WordbookBundle>
    fun findById(id: Long): WordbookBundle?
    fun save(wordbookBundle: WordbookBundle): WordbookBundle
    fun delete(wordbookBundle: WordbookBundle)
}