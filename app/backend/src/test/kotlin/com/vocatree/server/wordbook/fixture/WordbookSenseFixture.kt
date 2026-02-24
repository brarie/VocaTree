package com.vocatree.server.wordbook.fixture

import com.vocatree.server.common.domain.model.vocabulary.vo.SenseVo
import com.vocatree.server.wordbook.domain.model.WordbookExample
import com.vocatree.server.wordbook.domain.model.WordbookSense

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
