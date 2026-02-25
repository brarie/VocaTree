package com.vocatree.server.library.infrastructure.persistence.provider

import com.vocatree.server.library.domain.model.SharedExampleSnapshot
import com.vocatree.server.library.domain.model.SharedSenseSnapshot
import com.vocatree.server.library.domain.model.SharedWordSnapshot
import com.vocatree.server.library.domain.port.WordbookSnapshotProvider
import com.vocatree.server.wordbook.domain.exception.WordbookNotFoundException
import com.vocatree.server.wordbook.domain.model.WordbookSense
import com.vocatree.server.wordbook.domain.model.WordbookWord
import com.vocatree.server.wordbook.domain.port.WordbookRepository
import com.vocatree.server.wordbook.domain.port.WordbookWordListRepository
import org.springframework.stereotype.Component

@Component
class WordbookSnapshotProviderImpl(
    private val wordbookRepository: WordbookRepository,
    private val wordbookWordListRepository: WordbookWordListRepository,
) : WordbookSnapshotProvider {

    override fun provide(wordbookId: Long, userId: String): List<SharedWordSnapshot> {
        val wordbook = wordbookRepository.findById(wordbookId)
            ?: throw WordbookNotFoundException()

        wordbook.validateOwner(userId)

        val wordList = wordbookWordListRepository.findById(wordbookId)
        return wordList.wordList.map { word -> toWordSnapshot(word) }
    }

    private fun toWordSnapshot(word: WordbookWord): SharedWordSnapshot = SharedWordSnapshot(
        spelling = word.spelling,
        displayOrder = word.displayOrder,
        senses = word.senses.map { sense -> toSenseSnapshot(sense) },
    )

    private fun toSenseSnapshot(sense: WordbookSense): SharedSenseSnapshot = SharedSenseSnapshot(
        sense = sense.sense,
        displayOrder = sense.displayOrder,
        examples = sense.examples.map { example ->
            SharedExampleSnapshot(
                example = example.example,
                displayOrder = example.displayOrder,
            )
        },
    )
}
