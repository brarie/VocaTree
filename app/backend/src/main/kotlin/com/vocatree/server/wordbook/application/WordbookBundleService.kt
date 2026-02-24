package com.vocatree.server.wordbook.application

import com.vocatree.server.wordbook.domain.exception.CannotDeleteWordbookBundleWithWordbooks
import com.vocatree.server.wordbook.domain.exception.WordbookBundleNotFoundException
import com.vocatree.server.wordbook.domain.model.WordbookBundle
import com.vocatree.server.wordbook.domain.port.WordbookBundleRepository
import com.vocatree.server.wordbook.domain.port.WordbookRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class WordbookBundleService(
    private val wordbookBundleRepository: WordbookBundleRepository,
    private val wordbookRepository: WordbookRepository
) {
    @Transactional(readOnly = true)
    fun getAllBundles(userId: String) : List<WordbookBundle>
        = wordbookBundleRepository.findAllByOwnerId(userId)


    @Transactional
    fun create(userId: String, title: String): WordbookBundle {
        val bundle = WordbookBundle(ownerId = userId, title = title)
        return wordbookBundleRepository.save(bundle)
    }

    @Transactional
    fun updateTitle(userId: String, bundleId: Long, newTitle: String): WordbookBundle {
        val bundle = wordbookBundleRepository.findById(bundleId)
            ?: throw WordbookBundleNotFoundException()

        bundle.validateOwner(userId)

        val updatedBundle = bundle.updateTitle(newTitle)
        return wordbookBundleRepository.save(updatedBundle)
    }

    @Transactional
    fun delete(userId: String, bundleId: Long) {
        val bundle = wordbookBundleRepository.findById(bundleId)
            ?: throw WordbookBundleNotFoundException()

        bundle.validateOwner(userId)
        if (wordbookRepository.existsByBundleId(bundleId))
            throw CannotDeleteWordbookBundleWithWordbooks()


        wordbookBundleRepository.delete(bundle)
    }

    @Transactional
    fun toggleStarred(userId: String, bundleId: Long): WordbookBundle {
        val bundle = wordbookBundleRepository.findById(bundleId)
            ?: throw WordbookBundleNotFoundException()

        bundle.validateOwner(userId)

        val toggledBundle = bundle.toggleStarred()
        return wordbookBundleRepository.save(toggledBundle)
    }

    @Transactional(readOnly = true)
    fun getBundle(userId: String, bundleId: Long): WordbookBundle {
        val bundle = wordbookBundleRepository.findById(bundleId)
            ?: throw WordbookBundleNotFoundException()

        bundle.validateOwner(userId)
        return bundle
    }
}