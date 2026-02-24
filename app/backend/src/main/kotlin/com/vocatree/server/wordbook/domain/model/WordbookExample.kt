package com.vocatree.server.wordbook.domain.model

import com.vocatree.server.common.domain.model.vocabulary.vo.ExampleVo
import com.vocatree.server.wordbook.domain.model.command.UpdateExampleCommand

data class WordbookExample(
    val id: Long? = null,
    val example: ExampleVo = ExampleVo(),
    val displayOrder: Int = 0,
) {
    companion object {
        fun create(displayOrder: Int) = WordbookExample(
            displayOrder = displayOrder
        )
    }

    fun update(command: UpdateExampleCommand): WordbookExample {
        return this.copy(
            example = ExampleVo(
                sentence = command.sentence,
                translation = command.translation,
            )
        )
    }
}
