package com.vocatree.server.wordbook.domain.port

import com.vocatree.server.wordbook.domain.model.WordbookWordList

interface WordbookWordListRepository {

    fun findById(wordbookId: Long): WordbookWordList
    fun save(wordbookWordList: WordbookWordList): WordbookWordList
}