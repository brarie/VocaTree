package com.monovoca.server.wordbook.application

import com.monovoca.server.wordbook.domain.exception.WordbookNotFoundException
import com.monovoca.server.wordbook.domain.model.WordbookWordList
import com.monovoca.server.wordbook.domain.model.command.UpdateExampleCommand
import com.monovoca.server.wordbook.domain.model.command.UpdateSenseCommand
import com.monovoca.server.wordbook.domain.model.command.UpdateWordCommand
import com.monovoca.server.wordbook.domain.port.WordbookRepository
import com.monovoca.server.wordbook.domain.port.WordbookWordListRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class WordbookWordListService(
    private val wordbookRepository: WordbookRepository,
    private val wordbookWordListRepository: WordbookWordListRepository
) {

    @Transactional(readOnly = true)
    fun getWordList(userId: String, wordbookId: Long): WordbookWordList {
        val wordbook = wordbookRepository.findById(wordbookId)
            ?: throw WordbookNotFoundException()

        wordbook.validateOwner(userId)

        return wordbookWordListRepository.findById(wordbookId)
    }

    @Transactional
    fun createWord(userId: String, wordbookId: Long): WordbookWordList {
        val wordbook = wordbookRepository.findById(wordbookId)
            ?: throw WordbookNotFoundException()

        wordbook.validateOwner(userId)

        val wordList = wordbookWordListRepository.findById(wordbookId)

        val updatedWordList = wordList.newWord()
        return wordbookWordListRepository.save(updatedWordList)
    }

    @Transactional
    fun updateWord(userId: String, wordbookId: Long, wordId: Long, command: UpdateWordCommand): WordbookWordList {
        val wordbook = wordbookRepository.findById(wordbookId)
            ?: throw WordbookNotFoundException()

        wordbook.validateOwner(userId)

        val wordList = wordbookWordListRepository.findById(wordbookId)

        val updatedWordList = wordList.updateWord(wordId, command)
        return wordbookWordListRepository.save(updatedWordList)
    }

    @Transactional
    fun deleteWord(userId: String, wordbookId: Long, wordId: Long): WordbookWordList {
        val wordbook = wordbookRepository.findById(wordbookId)
            ?: throw WordbookNotFoundException()

        wordbook.validateOwner(userId)

        val wordList = wordbookWordListRepository.findById(wordbookId)

        val updatedWordList = wordList.deleteWord(wordId)
        return wordbookWordListRepository.save(updatedWordList)
    }

    @Transactional
    fun createSense(userId: String, wordbookId: Long, wordId: Long): WordbookWordList {
        val wordbook = wordbookRepository.findById(wordbookId)
            ?: throw WordbookNotFoundException()

        wordbook.validateOwner(userId)

        println("wordList 가져오는 쿼리")
        val wordList = wordbookWordListRepository.findById(wordbookId)

        val updatedWordList = wordList.newSense(wordId)
        
        println("wordList 저장하는 쿼리")
        return wordbookWordListRepository.save(updatedWordList)
    }

    @Transactional
    fun updateSense(
        userId: String,
        wordbookId: Long,
        wordId: Long,
        senseId: Long,
        command: UpdateSenseCommand
    ): WordbookWordList {
        val wordbook = wordbookRepository.findById(wordbookId)
            ?: throw WordbookNotFoundException()

        wordbook.validateOwner(userId)

        val wordList = wordbookWordListRepository.findById(wordbookId)

        val updatedWordList = wordList.updateSense(wordId, senseId, command)
        return wordbookWordListRepository.save(updatedWordList)
    }

    @Transactional
    fun deleteSense(userId: String, wordbookId: Long, wordId: Long, senseId: Long): WordbookWordList {
        val wordbook = wordbookRepository.findById(wordbookId)
            ?: throw WordbookNotFoundException()

        wordbook.validateOwner(userId)

        val wordList = wordbookWordListRepository.findById(wordbookId)

        val updatedWordList = wordList.deleteSense(wordId, senseId)
        return wordbookWordListRepository.save(updatedWordList)
    }

    @Transactional
    fun createExample(userId: String, wordbookId: Long, wordId: Long, senseId: Long): WordbookWordList {
        val wordbook = wordbookRepository.findById(wordbookId)
            ?: throw WordbookNotFoundException()

        wordbook.validateOwner(userId)

        val wordList = wordbookWordListRepository.findById(wordbookId)

        val updatedWordList = wordList.newExample(wordId, senseId)
        return wordbookWordListRepository.save(updatedWordList)
    }

    @Transactional
    fun updateExample(
        userId: String,
        wordbookId: Long,
        wordId: Long,
        senseId: Long,
        exampleId: Long,
        command: UpdateExampleCommand
    ): WordbookWordList {
        val wordbook = wordbookRepository.findById(wordbookId)
            ?: throw WordbookNotFoundException()

        wordbook.validateOwner(userId)

        val wordList = wordbookWordListRepository.findById(wordbookId)

        val updatedWordList = wordList.updateExample(wordId, senseId, exampleId, command)
        return wordbookWordListRepository.save(updatedWordList)
    }

    @Transactional
    fun deleteExample(userId: String, wordbookId: Long, wordId: Long, senseId: Long, exampleId: Long): WordbookWordList {
        val wordbook = wordbookRepository.findById(wordbookId)
            ?: throw WordbookNotFoundException()

        wordbook.validateOwner(userId)

        val wordList = wordbookWordListRepository.findById(wordbookId)

        val updatedWordList = wordList.deleteExample(wordId, senseId, exampleId)
        return wordbookWordListRepository.save(updatedWordList)
    }

    @Transactional
    fun swapWordsOrder(userId: String, wordbookId: Long, wordId1: Long, wordId2: Long): WordbookWordList {
        val wordbook = wordbookRepository.findById(wordbookId)
            ?: throw WordbookNotFoundException()

        wordbook.validateOwner(userId)

        val wordList = wordbookWordListRepository.findById(wordbookId)

        val updatedWordList = wordList.swapWordsOrder(wordId1, wordId2)
        return wordbookWordListRepository.save(updatedWordList)
    }

    @Transactional
    fun swapSensesOrder(
        userId: String,
        wordbookId: Long,
        wordId: Long,
        senseId1: Long,
        senseId2: Long
    ): WordbookWordList {
        val wordbook = wordbookRepository.findById(wordbookId)
            ?: throw WordbookNotFoundException()

        wordbook.validateOwner(userId)

        val wordList = wordbookWordListRepository.findById(wordbookId)

        val updatedWordList = wordList.swapSensesOrder(wordId, senseId1, senseId2)
        return wordbookWordListRepository.save(updatedWordList)
    }

    @Transactional
    fun swapExamplesOrder(
        userId: String,
        wordbookId: Long,
        wordId: Long,
        senseId: Long,
        exampleId1: Long,
        exampleId2: Long
    ): WordbookWordList {
        val wordbook = wordbookRepository.findById(wordbookId)
            ?: throw WordbookNotFoundException()

        wordbook.validateOwner(userId)

        val wordList = wordbookWordListRepository.findById(wordbookId)

        val updatedWordList = wordList.swapExamplesOrder(wordId, senseId, exampleId1, exampleId2)
        return wordbookWordListRepository.save(updatedWordList)
    }
}