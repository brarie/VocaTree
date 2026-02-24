package com.vocatree.server.dictionary.domain.model.word

import com.vocatree.server.common.domain.model.vocabulary.vo.SenseVo
import com.vocatree.server.common.util.isSortedBy
import com.vocatree.server.dictionary.domain.exception.PublicExampleNotFoundException
import com.vocatree.server.dictionary.domain.model.word.command.CreateExampleCommand
import com.vocatree.server.dictionary.domain.model.word.command.CreateSenseCommand
import com.vocatree.server.dictionary.domain.model.word.command.UpdateExampleCommand
import com.vocatree.server.dictionary.domain.model.word.command.UpdateSenseCommand

data class PublicSense(
    val id: Long? = null,
    val sense: SenseVo,
    val displayOrder: Int = 0,
    val examples: List<PublicExample> = emptyList(),
) {
    init {
        require(examples.isSortedBy { it.displayOrder }) { "Examples must be sorted by displayOrder" }
    }

    companion object {
        fun create(command: CreateSenseCommand, displayOrder: Int): PublicSense {
            return PublicSense(
                sense = SenseVo(
                    definition = command.definition,
                    engDefinition = command.engDefinition,
                    britishPhonetic = command.britishPhonetic,
                    americanPhonetic = command.americanPhonetic,
                    partOfSpeech = command.partOfSpeech,
                    synonyms = command.synonyms,
                    antonyms = command.antonyms
                ),
                displayOrder = displayOrder
            )
        }
    }

    fun update(command: UpdateSenseCommand): PublicSense {
        return this.copy(
            sense = SenseVo(
                definition = command.definition,
                engDefinition = command.engDefinition,
                britishPhonetic = command.britishPhonetic,
                americanPhonetic = command.americanPhonetic,
                partOfSpeech = command.partOfSpeech,
                synonyms = command.synonyms,
                antonyms = command.antonyms
            )
        )
    }

    fun addExample(command: CreateExampleCommand): PublicSense {
        val newDisplayOrder = (examples.maxOfOrNull { it.displayOrder } ?: -1) + 1
        val newExample = PublicExample.create(command, newDisplayOrder)
        return this.copy(examples = examples + newExample)
    }

    fun updateExample(exampleId: Long, command: UpdateExampleCommand): PublicSense {
        if (examples.none { it.id == exampleId })
            throw PublicExampleNotFoundException()

        val newExamples = examples.map { example ->
            if (example.id == exampleId) example.update(command) else example
        }
        return this.copy(examples = newExamples)
    }

    fun deleteExample(exampleId: Long): PublicSense {
        val newExamples = examples.filter { it.id != exampleId }
        if (newExamples.size == examples.size)
            throw PublicExampleNotFoundException()

        return this.copy(examples = newExamples)
    }

    fun reorderExamples(orders: List<Pair<Long, Int>>): PublicSense {
        val orderMap = orders.toMap()
        val newExamples = examples.map { example ->
            orderMap[example.id]?.let { newOrder ->
                example.copy(displayOrder = newOrder)
            } ?: example
        }.sortedBy { it.displayOrder }
        return this.copy(examples = newExamples)
    }
}
