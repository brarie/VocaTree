package com.monovoca.server.wordbook.domain.model

import com.monovoca.server.common.domain.model.vocabulary.vo.SenseVo
import com.monovoca.server.common.util.isSortedBy
import com.monovoca.server.wordbook.domain.exception.WordbookExampleNotFoundException
import com.monovoca.server.wordbook.domain.model.command.UpdateExampleCommand
import com.monovoca.server.wordbook.domain.model.command.UpdateSenseCommand

data class WordbookSense(
    val id: Long? = null,
    val sense: SenseVo = SenseVo(),
    val displayOrder: Int = 0,
    val examples: List<WordbookExample> = emptyList(),
) {
    init {
        require(examples.isSortedBy { it.displayOrder }) { "Examples must be sorted by displayOrder" }
    }

    companion object {
        fun create(displayOrder: Int) = WordbookSense(
            displayOrder = displayOrder
        )
    }

    fun newExample(): WordbookSense {
        val newDisplayOrder = (examples.maxOfOrNull { it.displayOrder } ?: 0) + 1
        val newExample = WordbookExample.create(displayOrder = newDisplayOrder)
        val updatedExamples = examples + newExample
        return this.copy(examples = updatedExamples)
    }

    fun update(command: UpdateSenseCommand): WordbookSense {
        return this.copy(
            sense = SenseVo(
                definition = command.definition,
                partOfSpeech = command.partOfSpeech,
                engDefinition = command.engDefinition,
                britishPhonetic = command.britishPhonetic,
                americanPhonetic = command.americanPhonetic,
                synonyms = command.synonyms,
                antonyms = command.antonyms
            )
        )
    }

    fun updateExample(exampleId: Long, command: UpdateExampleCommand): WordbookSense {
        if (examples.none { it.id == exampleId })
            throw WordbookExampleNotFoundException()

        val updatedExamples = examples.map { example ->
            if (example.id == exampleId) example.update(command) else example
        }
        return this.copy(examples = updatedExamples)
    }

    fun deleteExample(exampleId: Long): WordbookSense {
        if (examples.none { it.id == exampleId })
            throw WordbookExampleNotFoundException()

        val updatedExamples = examples.filter { it.id != exampleId }
        return this.copy(examples = updatedExamples)
    }

    fun swapExamplesOrder(exampleId1: Long, exampleId2: Long): WordbookSense {
        val example1 = examples.find { it.id == exampleId1 } ?: throw WordbookExampleNotFoundException()
        val example2 = examples.find { it.id == exampleId2 } ?: throw WordbookExampleNotFoundException()

        val newExamples = examples.map { example ->
            when (example.id) {
                exampleId1 -> example.copy(displayOrder = example2.displayOrder)
                exampleId2 -> example.copy(displayOrder = example1.displayOrder)
                else -> example
            }
        }.sortedBy { it.displayOrder }

        return this.copy(examples = newExamples)
    }
}
