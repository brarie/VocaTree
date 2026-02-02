package com.monovoca.server.dictionary.application

import com.monovoca.server.common.domain.model.vo.Spelling
import com.monovoca.server.dictionary.domain.exception.PublicWordAlreadyExistsException
import com.monovoca.server.dictionary.domain.exception.PublicWordNotFoundException
import com.monovoca.server.dictionary.domain.model.word.PublicWord
import com.monovoca.server.dictionary.domain.model.word.command.CreateExampleCommand
import com.monovoca.server.dictionary.domain.model.word.command.CreateSenseCommand
import com.monovoca.server.dictionary.domain.model.word.command.UpdateExampleCommand
import com.monovoca.server.dictionary.domain.model.word.command.UpdateSenseCommand
import com.monovoca.server.dictionary.domain.port.DictionaryWordRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DictionaryWordService(
    private val dictionaryWordRepository: DictionaryWordRepository
) {

    @Transactional
    fun createWord(spelling: String): PublicWord {
        val spellingVo = Spelling(spelling)

        if (dictionaryWordRepository.existsBySpelling(spellingVo))
            throw PublicWordAlreadyExistsException()

        val word = PublicWord(spelling = spellingVo)

        return dictionaryWordRepository.save(word)
    }

    @Transactional
    fun updateWord(wordId: Long, spelling: String): PublicWord {
        val spellingVo = Spelling(spelling)
        val word = dictionaryWordRepository.findPublicWordById(wordId)
            ?: throw PublicWordNotFoundException()

        if (word.spelling != spellingVo && dictionaryWordRepository.existsBySpelling(spellingVo))
            throw PublicWordAlreadyExistsException()

        val updatedWord = word.updateSpelling(spellingVo)

        return dictionaryWordRepository.save(updatedWord)
    }

    @Transactional
    fun deleteWord(wordId: Long) {
        val word = dictionaryWordRepository.findPublicWordById(wordId)
            ?: throw PublicWordNotFoundException()

        dictionaryWordRepository.delete(word)
    }

    @Transactional
    fun createSense(wordId: Long, command: CreateSenseCommand): PublicWord {
        val word = dictionaryWordRepository.findPublicWordById(wordId)
            ?: throw PublicWordNotFoundException()

        val updatedWord = word.addSense(command)

        return dictionaryWordRepository.save(updatedWord)
    }

    @Transactional
    fun updateSense(wordId: Long, senseId: Long, command: UpdateSenseCommand): PublicWord {
        val word = dictionaryWordRepository.findPublicWordById(wordId)
            ?: throw PublicWordNotFoundException()

        val updatedWord = word.updateSense(senseId, command)

        return dictionaryWordRepository.save(updatedWord)
    }

    @Transactional
    fun deleteSense(wordId: Long, senseId: Long): PublicWord {
        val word = dictionaryWordRepository.findPublicWordById(wordId)
            ?: throw PublicWordNotFoundException()

        val updatedWord = word.deleteSense(senseId)

        return dictionaryWordRepository.save(updatedWord)
    }

    @Transactional
    fun reorderSenses(wordId: Long, orders: List<Pair<Long, Int>>): PublicWord {
        val word = dictionaryWordRepository.findPublicWordById(wordId)
            ?: throw PublicWordNotFoundException()

        val updatedWord = word.reorderSenses(orders)

        return dictionaryWordRepository.save(updatedWord)
    }

    @Transactional
    fun createExample(wordId: Long, senseId: Long, command: CreateExampleCommand): PublicWord {
        val word = dictionaryWordRepository.findPublicWordById(wordId)
            ?: throw PublicWordNotFoundException()

        val updatedWord = word.addExample(senseId, command)

        return dictionaryWordRepository.save(updatedWord)
    }

    @Transactional
    fun updateExample(
        wordId: Long,
        senseId: Long,
        exampleId: Long,
        command: UpdateExampleCommand
    ): PublicWord {
        val word = dictionaryWordRepository.findPublicWordById(wordId)
            ?: throw PublicWordNotFoundException()

        val updatedWord = word.updateExample(senseId, exampleId, command)

        return dictionaryWordRepository.save(updatedWord)
    }

    @Transactional
    fun deleteExample(wordId: Long, senseId: Long, exampleId: Long): PublicWord {
        val word = dictionaryWordRepository.findPublicWordById(wordId)
            ?: throw PublicWordNotFoundException()

        val updatedWord = word.deleteExample(senseId, exampleId)

        return dictionaryWordRepository.save(updatedWord)
    }

    @Transactional
    fun reorderExamples(wordId: Long, senseId: Long, orders: List<Pair<Long, Int>>): PublicWord {
        val word = dictionaryWordRepository.findPublicWordById(wordId)
            ?: throw PublicWordNotFoundException()

        val updatedWord = word.reorderExamples(senseId, orders)

        return dictionaryWordRepository.save(updatedWord)
    }
}
