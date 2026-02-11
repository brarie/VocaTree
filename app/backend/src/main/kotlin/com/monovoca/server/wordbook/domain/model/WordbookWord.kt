package com.monovoca.server.wordbook.domain.model

import com.monovoca.server.common.domain.model.vocabulary.vo.components.Spelling
import com.monovoca.server.common.util.isSortedBy
import com.monovoca.server.wordbook.domain.exception.WordbookSenseNotFoundException
import com.monovoca.server.wordbook.domain.model.command.UpdateExampleCommand
import com.monovoca.server.wordbook.domain.model.command.UpdateSenseCommand
import com.monovoca.server.wordbook.domain.model.command.UpdateWordCommand

data class WordbookWord(
    val id: Long? = null,
    val spelling: Spelling? = null,
    val displayOrder: Int = 0,
    val senses: List<WordbookSense> = emptyList(),
) {
    init {
        require(senses.isSortedBy { it.displayOrder }) { "Senses must be sorted by displayOrder" }
    }

    companion object {
        fun create(displayOrder: Int): WordbookWord =
            WordbookWord(displayOrder = displayOrder)
    }

    fun update(command: UpdateWordCommand): WordbookWord {
        return this.copy(
            spelling = command.spelling
        )
    }

    fun updateSense(senseId: Long, command: UpdateSenseCommand): WordbookWord {
        if (senses.none { it.id == senseId })
            throw WordbookSenseNotFoundException()

        val updatedSenses = senses.map { sense ->
            if (sense.id == senseId) sense.update(command) else sense
        }
        return this.copy(senses = updatedSenses)
    }

    fun newSense(): WordbookWord {
        val newDisplayOrder = (senses.maxOfOrNull { it.displayOrder } ?: 0) + 1
        val newSense = WordbookSense.create(displayOrder = newDisplayOrder)
        val updatedSenses = senses + newSense
        return this.copy(senses = updatedSenses)
    }

    fun newExample(senseId: Long): WordbookWord {
        if (senses.none { it.id == senseId })
            throw WordbookSenseNotFoundException()

        val updatedSenses = senses.map { sense ->
            if (sense.id == senseId) sense.newExample() else sense
        }
        return this.copy(senses = updatedSenses)
    }

    fun updateExample(senseId: Long, exampleId: Long, command: UpdateExampleCommand): WordbookWord {
        if (senses.none { it.id == senseId })
            throw WordbookSenseNotFoundException()

        val updatedSenses = senses.map { sense ->
            if (sense.id == senseId) sense.updateExample(exampleId, command) else sense
        }
        return this.copy(senses = updatedSenses)
    }

    fun deleteSense(senseId: Long): WordbookWord {
        if (senses.none { it.id == senseId })
            throw WordbookSenseNotFoundException()

        val updatedSenses = senses.filter { it.id != senseId }
        return this.copy(senses = updatedSenses)
    }

    fun deleteExample(senseId: Long, exampleId: Long): WordbookWord {
        if (senses.none { it.id == senseId })
            throw WordbookSenseNotFoundException()

        val updatedSenses = senses.map { sense ->
            if (sense.id == senseId) sense.deleteExample(exampleId) else sense
        }
        return this.copy(senses = updatedSenses)
    }

    fun swapSensesOrder(senseId1: Long, senseId2: Long): WordbookWord {
        val sense1 = senses.find { it.id == senseId1 } ?: throw WordbookSenseNotFoundException()
        val sense2 = senses.find { it.id == senseId2 } ?: throw WordbookSenseNotFoundException()

        val newSenses = senses.map { sense ->
            when (sense.id) {
                senseId1 -> sense.copy(displayOrder = sense2.displayOrder)
                senseId2 -> sense.copy(displayOrder = sense1.displayOrder)
                else -> sense
            }
        }.sortedBy { it.displayOrder }

        return this.copy(senses = newSenses)
    }

    fun swapExamplesOrder(senseId: Long, exampleId1: Long, exampleId2: Long): WordbookWord {
        if (senses.none { it.id == senseId })
            throw WordbookSenseNotFoundException()

        val updatedSenses = senses.map { sense ->
            if (sense.id == senseId) sense.swapExamplesOrder(exampleId1, exampleId2) else sense
        }
        return this.copy(senses = updatedSenses)
    }
}
