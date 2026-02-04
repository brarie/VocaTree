package com.monovoca.server.dictionary.domain.model.word

import com.monovoca.server.common.domain.model.vocabulary.vo.ExampleVo
import com.monovoca.server.dictionary.domain.model.word.command.CreateExampleCommand
import com.monovoca.server.dictionary.domain.model.word.command.UpdateExampleCommand

data class PublicExample(
    val id: Long? = null,
    val example: ExampleVo,
    val isOfficial: Boolean = false,
    val sourceInfo: String? = null,
    val displayOrder: Int = 0,
) {
    companion object {
        fun create(command: CreateExampleCommand, displayOrder: Int): PublicExample {
            return PublicExample(
                example = ExampleVo(
                    sentence = command.sentence,
                    translation = command.translation
                ),
                isOfficial = command.isOfficial,
                sourceInfo = command.sourceInfo,
                displayOrder = displayOrder
            )
        }
    }

    fun update(command: UpdateExampleCommand): PublicExample {
        return this.copy(
            example = ExampleVo(
                sentence = command.sentence,
                translation = command.translation
            ),
            isOfficial = command.isOfficial,
            sourceInfo = command.sourceInfo
        )
    }
}
