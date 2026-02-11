package com.monovoca.server.wordbook.fixture

import com.monovoca.server.common.domain.model.vocabulary.vo.components.Spelling
import com.monovoca.server.wordbook.domain.model.WordbookSense
import com.monovoca.server.wordbook.domain.model.WordbookWord

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
