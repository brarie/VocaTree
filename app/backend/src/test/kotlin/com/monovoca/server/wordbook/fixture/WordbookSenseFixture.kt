package com.monovoca.server.wordbook.fixture

import com.monovoca.server.common.domain.model.vocabulary.vo.SenseVo
import com.monovoca.server.wordbook.domain.model.WordbookExample
import com.monovoca.server.wordbook.domain.model.WordbookSense

object WordbookSenseFixture {

    fun create(
        id: Long? = null,
        sense: SenseVo = SenseVo(),
        displayOrder: Int = 0,
        examples: List<WordbookExample> = emptyList()
    ): WordbookSense {
        return WordbookSense(
            id = id,
            sense = sense,
            displayOrder = displayOrder,
            examples = examples
        )
    }
}
