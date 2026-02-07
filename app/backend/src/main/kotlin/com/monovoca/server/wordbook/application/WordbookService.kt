package com.monovoca.server.wordbook.application

import com.monovoca.server.wordbook.domain.exception.WordbookBundleNotFoundException
import com.monovoca.server.wordbook.domain.exception.WordbookNotFoundException
import com.monovoca.server.wordbook.domain.model.Wordbook
import com.monovoca.server.wordbook.domain.model.WordbookColumnSetting
import com.monovoca.server.wordbook.domain.model.enumerated.ColumnType
import com.monovoca.server.wordbook.domain.port.WordbookBundleRepository
import com.monovoca.server.wordbook.domain.port.WordbookRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class WordbookService(
    private val wordbookRepository: WordbookRepository,
    private val wordbookBundleRepository: WordbookBundleRepository
) {

    @Transactional
    fun create(userId: String, title: String): Wordbook {
        val wordbook = Wordbook.create(ownerId = userId, title = title)
        return wordbookRepository.save(wordbook)
    }

    @Transactional
    fun updateTitle(userId: String, wordbookId: Long, newTitle: String): Wordbook {
        val wordbook = wordbookRepository.findById(wordbookId)
            ?: throw WordbookNotFoundException()

        wordbook.validateOwner(userId)

        val updatedWordbook = wordbook.updateTitle(newTitle)
        return wordbookRepository.save(updatedWordbook)
    }

    @Transactional
    fun delete(userId: String, wordbookId: Long) {
        val wordbook = wordbookRepository.findById(wordbookId)
            ?: throw WordbookNotFoundException()

        wordbook.validateOwner(userId)

        wordbookRepository.delete(wordbook)
    }

    @Transactional
    fun toggleStarred(userId: String, wordbookId: Long): Wordbook {
        val wordbook = wordbookRepository.findById(wordbookId)
            ?: throw WordbookNotFoundException()

        wordbook.validateOwner(userId)

        val toggledWordbook = wordbook.toggleStarred()
        return wordbookRepository.save(toggledWordbook)
    }

    @Transactional
    fun move(userId: String, wordbookId: Long, bundleId: Long?) : Wordbook {
        val wordbook = wordbookRepository.findById(wordbookId)
            ?: throw WordbookNotFoundException()

        wordbook.validateOwner(userId)

        if (bundleId != null) {
            val bundle = wordbookBundleRepository.findById(bundleId)
                ?: throw WordbookBundleNotFoundException()

            bundle.validateOwner(userId)
        }

        val movedWordbook = wordbook.moveToBundle(bundleId)
        return wordbookRepository.save(movedWordbook)
    }

    @Transactional(readOnly = true)
    fun getWordbook(userId: String, wordbookId: Long): Wordbook {
        val wordbook = wordbookRepository.findById(wordbookId)
            ?: throw WordbookNotFoundException()

        wordbook.validateOwner(userId)
        return wordbook
    }

    @Transactional(readOnly = true)
    fun getWordbookColumns(userId: String, wordbookId: Long): Map<ColumnType, WordbookColumnSetting> {
        val wordbook = wordbookRepository.findById(wordbookId)
            ?: throw WordbookNotFoundException()

        wordbook.validateOwner(userId)

        return wordbook.columns
    }

    @Transactional
    fun updateColumnWidth(
        userId: String,
        wordbookId: Long,
        columnType: ColumnType,
        newWidth: Int
    ): Map<ColumnType, WordbookColumnSetting> {
        val wordbook = wordbookRepository.findById(wordbookId)
            ?: throw WordbookNotFoundException()

        wordbook.validateOwner(userId)

        val updatedWordbook = wordbook.updateColumnWidth(columnType, newWidth)
        val saved = wordbookRepository.save(updatedWordbook)

        return saved.columns
    }

    @Transactional
    fun toggleColumnActiveStatus(
        userId: String,
        wordbookId: Long,
        columnType: ColumnType,
    ): Map<ColumnType, WordbookColumnSetting> {
        val wordbook = wordbookRepository.findById(wordbookId)
            ?: throw WordbookNotFoundException()

        wordbook.validateOwner(userId)

        val updatedWordbook = wordbook.toggleColumnActiveStatus(columnType)
        val saved = wordbookRepository.save(updatedWordbook)

        return saved.columns
    }
}