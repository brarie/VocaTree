package com.monovoca.server.wordbook.domain.port

import com.monovoca.server.wordbook.domain.model.WordbookWordList

interface WordbookWordListRepository {

    fun findById(wordbookId: Long): WordbookWordList
    fun save(wordbookWordList: WordbookWordList): WordbookWordList
}