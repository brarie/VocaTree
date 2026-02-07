package com.monovoca.server.wordbook.domain.model

import com.monovoca.server.common.util.isSortedBy
import com.monovoca.server.wordbook.domain.exception.WordbookWordNotFoundException
import com.monovoca.server.wordbook.domain.model.command.UpdateExampleCommand
import com.monovoca.server.wordbook.domain.model.command.UpdateSenseCommand
import com.monovoca.server.wordbook.domain.model.command.UpdateWordCommand

// AGGREGATE //
data class WordbookWordList(
    val wordbookId: Long,
    val wordList: List<WordbookWord> = emptyList()
) {
    init {
        require(wordList.isSortedBy { it.displayOrder }) { "Words must be sorted by displayOrder" }
    }

    fun newWord(): WordbookWordList {
        val newDisplayOrder = (wordList.maxOfOrNull { it.displayOrder } ?: 0) + 1
        val newWord = WordbookWord.create(displayOrder = newDisplayOrder)
        val updatedWordList = wordList + newWord
        return this.copy(wordList = updatedWordList)
    }

    fun updateWord(wordId: Long, command: UpdateWordCommand): WordbookWordList {
        if (wordList.none { it.id == wordId })
            throw WordbookWordNotFoundException()

        val updatedWords = wordList.map { word ->
            if (word.id == wordId) word.update(command) else word
        }

        return this.copy(wordList = updatedWords)
    }

    fun deleteWord(wordId: Long): WordbookWordList {
        if (wordList.none { it.id == wordId })
            throw WordbookWordNotFoundException()

        val updatedWords = wordList.filter { it.id != wordId }
        return this.copy(wordList = updatedWords)
    }

    fun newSense(wordId: Long): WordbookWordList {
        if (wordList.none { it.id == wordId })
            throw WordbookWordNotFoundException()

        val updatedWords = wordList.map { word ->
            if (word.id == wordId) word.newSense() else word
        }
        return this.copy(wordList = updatedWords)
    }

    fun newExample(wordId: Long, senseId: Long): WordbookWordList {
        if (wordList.none { it.id == wordId })
            throw WordbookWordNotFoundException()

        val updatedWords = wordList.map { word ->
            if (word.id == wordId) word.newExample(senseId) else word
        }
        return this.copy(wordList = updatedWords)
    }

    fun updateSense(wordId: Long, senseId: Long, command: UpdateSenseCommand): WordbookWordList {
        if (wordList.none { it.id == wordId })
            throw WordbookWordNotFoundException()

        val updatedWords = wordList.map { word ->
            if (word.id == wordId) word.updateSense(senseId, command) else word
        }

        return this.copy(wordList = updatedWords)
    }

    fun deleteSense(wordId: Long, senseId: Long): WordbookWordList {
        if (wordList.none { it.id == wordId })
            throw WordbookWordNotFoundException()

        val updatedWords = wordList.map { word ->
            if (word.id == wordId) word.deleteSense(senseId) else word
        }
        return this.copy(wordList = updatedWords)
    }

    fun updateExample(wordId: Long, senseId: Long, exampleId: Long, command: UpdateExampleCommand): WordbookWordList {
        if (wordList.none { it.id == wordId })
            throw WordbookWordNotFoundException()

        val updatedWords = wordList.map { word ->
            if (word.id == wordId) word.updateExample(senseId, exampleId, command) else word
        }

        return this.copy(wordList = updatedWords)
    }

    fun deleteExample(wordId: Long, senseId: Long, exampleId: Long): WordbookWordList {
        if (wordList.none { it.id == wordId })
            throw WordbookWordNotFoundException()

        val updatedWords = wordList.map { word ->
            if (word.id == wordId) word.deleteExample(senseId, exampleId) else word
        }

        return this.copy(wordList = updatedWords)
    }
}