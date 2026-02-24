package com.vocatree.server.wordbook.fixture

import com.vocatree.server.common.domain.model.vocabulary.vo.components.Spelling
import com.vocatree.server.wordbook.domain.model.WordbookSense
import com.vocatree.server.wordbook.domain.model.WordbookWord

object WordbookWordFixture {

    fun create(
        id: Long? = null,
        spelling: Spelling? = null,
        displayOrder: Int = 0,
        senses: List<WordbookSense> = emptyList()
    ): WordbookWord {
        return WordbookWord(
            id = id,
            spelling = spelling,
            displayOrder = displayOrder,
            senses = senses
        )
    }
}
