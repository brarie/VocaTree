package com.vocatree.server.wordbook.fixture

import com.vocatree.server.common.domain.model.vocabulary.vo.ExampleVo
import com.vocatree.server.wordbook.domain.model.WordbookExample

object WordbookExampleFixture {

    fun create(
        id: Long? = null,
        example: ExampleVo = ExampleVo(),
        displayOrder: Int = 0
    ): WordbookExample {
        return WordbookExample(
            id = id,
            example = example,
            displayOrder = displayOrder
        )
    }
}
