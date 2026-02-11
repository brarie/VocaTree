package com.monovoca.server.wordbook.fixture

import com.monovoca.server.common.domain.model.vocabulary.vo.ExampleVo
import com.monovoca.server.wordbook.domain.model.WordbookExample

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
