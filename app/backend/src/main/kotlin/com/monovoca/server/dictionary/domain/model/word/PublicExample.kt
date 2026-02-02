package com.monovoca.server.dictionary.domain.model.word

import com.monovoca.server.dictionary.domain.model.word.command.CreateExampleCommand
import com.monovoca.server.dictionary.domain.model.word.command.UpdateExampleCommand

data class PublicExample(
    val id: Long? = null,
    val sentence: String,
    val translation: String? = null,
    val isOfficial: Boolean = false,
    val sourceInfo: String? = null,
    val displayOrder: Int = 0,
) {
    companion object {
        fun create(command: CreateExampleCommand, displayOrder: Int): PublicExample {
            return PublicExample(
                sentence = command.sentence,
                translation = command.translation,
                isOfficial = command.isOfficial,
                sourceInfo = command.sourceInfo,
                displayOrder = displayOrder
            )
        }
    }

    fun update(command: UpdateExampleCommand): PublicExample {
        return this.copy(
            sentence = command.sentence,
            translation = command.translation,
            isOfficial = command.isOfficial,
            sourceInfo = command.sourceInfo
        )
    }
}
