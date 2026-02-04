package com.monovoca.server.dictionary.application

import com.monovoca.server.common.domain.model.vocabulary.vo.components.Spelling
import com.monovoca.server.dictionary.domain.exception.PublicWordAlreadyExistsException
import com.monovoca.server.dictionary.domain.exception.PublicWordNotFoundException
import com.monovoca.server.dictionary.domain.model.query.DictionaryCountView
import com.monovoca.server.dictionary.domain.model.query.PublicWordSimpleView
import com.monovoca.server.dictionary.domain.model.word.PublicWord
import com.monovoca.server.dictionary.domain.model.word.command.CreateExampleCommand
import com.monovoca.server.dictionary.domain.model.word.command.CreateSenseCommand
import com.monovoca.server.dictionary.domain.model.word.command.UpdateExampleCommand
import com.monovoca.server.dictionary.domain.model.word.command.UpdateSenseCommand
import com.monovoca.server.dictionary.domain.port.DictionaryWordRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DictionaryWordService(
    private val dictionaryWordRepository: DictionaryWordRepository,
    private val userSearchHistoryService: UserSearchHistoryService
) {

    @Transactional(readOnly = true)
    fun getDictionaryCounts(): DictionaryCountView =
        dictionaryWordRepository.getDictionaryCounts()

    @Transactional(readOnly = true)
    fun paginateAllWords(pageable: Pageable) =
        dictionaryWordRepository.paginateViewsBySpelling("", pageable)

    @Transactional(readOnly = true)
    fun autocomplete(query: String, limit: Int): List<PublicWordSimpleView> {
        val pageable = PageRequest.of(0, limit)
        /* 그냥 만들기 귀찮아서 pagination 재활용 성능 이슈 생기면 그때 수정 */
        return dictionaryWordRepository.paginateViewsBySpelling(query, pageable).content
    }

    @Transactional
    fun paginateViewsBySpelling(query: String, pageable: Pageable, userId: String?): Page<PublicWordSimpleView> {
        userId?.let {
            userSearchHistoryService.recordSearch(it, query)
        }
        return dictionaryWordRepository.paginateViewsBySpelling(query, pageable)
    }

    @Transactional(readOnly = true)
    fun findBySlug(slug: String): PublicWord =
        dictionaryWordRepository.findBySlug(slug) ?: throw PublicWordNotFoundException()

    @Transactional
    fun createWord(spelling: String): PublicWord {
        val spellingVo = Spelling(spelling)

        if (dictionaryWordRepository.existsBySpelling(spellingVo))
            throw PublicWordAlreadyExistsException()

        val word = PublicWord(spelling = spellingVo)
        val savedWord = dictionaryWordRepository.save(word)

        val baseSlug = PublicWord.normalizeToSlug(spelling)
        val slug = if (dictionaryWordRepository.existsBySlug(baseSlug)) {
            PublicWord.generateSlugWithId(spelling, savedWord.id!!)
        } else {
            baseSlug
        }
        val wordWithSlug = savedWord.withSlug(slug)

        return dictionaryWordRepository.save(wordWithSlug)
    }

    @Transactional
    fun updateWord(wordId: Long, spelling: String): PublicWord {
        val spellingVo = Spelling(spelling)
        val word = dictionaryWordRepository.findById(wordId)
            ?: throw PublicWordNotFoundException()

        if (word.spelling != spellingVo && dictionaryWordRepository.existsBySpelling(spellingVo))
            throw PublicWordAlreadyExistsException()

        val updatedWord = if (word.spelling != spellingVo) {
            val baseSlug = PublicWord.normalizeToSlug(spelling)
            val newSlug = if (dictionaryWordRepository.existsBySlug(baseSlug)) {
                PublicWord.generateSlugWithId(spelling, wordId)
            } else {
                baseSlug
            }
            word.updateSpelling(spellingVo).withSlug(newSlug)
        } else {
            word.updateSpelling(spellingVo)
        }

        return dictionaryWordRepository.save(updatedWord)
    }

    @Transactional
    fun deleteWord(wordId: Long) {
        val word = dictionaryWordRepository.findById(wordId)
            ?: throw PublicWordNotFoundException()

        dictionaryWordRepository.delete(word)
    }

    @Transactional
    fun createSense(wordId: Long, command: CreateSenseCommand): PublicWord {
        val word = dictionaryWordRepository.findById(wordId)
            ?: throw PublicWordNotFoundException()

        val updatedWord = word.addSense(command)

        return dictionaryWordRepository.save(updatedWord)
    }

    @Transactional
    fun updateSense(wordId: Long, senseId: Long, command: UpdateSenseCommand): PublicWord {
        val word = dictionaryWordRepository.findById(wordId)
            ?: throw PublicWordNotFoundException()

        val updatedWord = word.updateSense(senseId, command)

        return dictionaryWordRepository.save(updatedWord)
    }

    @Transactional
    fun deleteSense(wordId: Long, senseId: Long): PublicWord {
        val word = dictionaryWordRepository.findById(wordId)
            ?: throw PublicWordNotFoundException()

        val updatedWord = word.deleteSense(senseId)

        return dictionaryWordRepository.save(updatedWord)
    }

    @Transactional
    fun reorderSenses(wordId: Long, orders: List<Pair<Long, Int>>): PublicWord {
        val word = dictionaryWordRepository.findById(wordId)
            ?: throw PublicWordNotFoundException()

        val updatedWord = word.reorderSenses(orders)

        return dictionaryWordRepository.save(updatedWord)
    }

    @Transactional
    fun createExample(wordId: Long, senseId: Long, command: CreateExampleCommand): PublicWord {
        val word = dictionaryWordRepository.findById(wordId)
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
        val word = dictionaryWordRepository.findById(wordId)
            ?: throw PublicWordNotFoundException()

        val updatedWord = word.updateExample(senseId, exampleId, command)

        return dictionaryWordRepository.save(updatedWord)
    }

    @Transactional
    fun deleteExample(wordId: Long, senseId: Long, exampleId: Long): PublicWord {
        val word = dictionaryWordRepository.findById(wordId)
            ?: throw PublicWordNotFoundException()

        val updatedWord = word.deleteExample(senseId, exampleId)

        return dictionaryWordRepository.save(updatedWord)
    }

    @Transactional
    fun reorderExamples(wordId: Long, senseId: Long, orders: List<Pair<Long, Int>>): PublicWord {
        val word = dictionaryWordRepository.findById(wordId)
            ?: throw PublicWordNotFoundException()

        val updatedWord = word.reorderExamples(senseId, orders)

        return dictionaryWordRepository.save(updatedWord)
    }
}
