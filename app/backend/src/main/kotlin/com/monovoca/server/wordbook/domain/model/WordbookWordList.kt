package com.monovoca.server.wordbook.domain.model

// AGGREGATE //
data class WordbookWordList(
    val wordList: List<WordbookWord> = emptyList()
) {
}