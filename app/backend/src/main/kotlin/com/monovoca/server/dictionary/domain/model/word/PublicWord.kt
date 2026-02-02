package com.monovoca.server.dictionary.domain.model.word

import com.monovoca.server.common.domain.model.vo.Spelling
import com.monovoca.server.common.util.isSortedBy
import com.monovoca.server.dictionary.domain.exception.PublicSenseNotFoundException
import com.monovoca.server.dictionary.domain.model.word.command.CreateExampleCommand
import com.monovoca.server.dictionary.domain.model.word.command.CreateSenseCommand
import com.monovoca.server.dictionary.domain.model.word.command.UpdateExampleCommand
import com.monovoca.server.dictionary.domain.model.word.command.UpdateSenseCommand

data class PublicWord(
    val id: Long? = null,
    val spelling: Spelling,
    val senses: List<PublicSense> = emptyList(),
) {
    init {
        require(senses.isSortedBy { it.displayOrder }) { "Senses must be sorted by displayOrder" }
    }

    fun updateSpelling(newSpelling: Spelling): PublicWord {
        return this.copy(spelling = newSpelling)
    }

    fun addSense(command: CreateSenseCommand): PublicWord {
        val newDisplayOrder = (senses.maxOfOrNull { it.displayOrder } ?: -1) + 1
        val newSense = PublicSense.create(command, newDisplayOrder)
        return this.copy(senses = senses + newSense)
    }

    fun updateSense(senseId: Long, command: UpdateSenseCommand): PublicWord {
        if (senses.none { it.id == senseId })
            throw PublicSenseNotFoundException()

        val newSenses = senses.map { sense ->
            if (sense.id == senseId) sense.update(command) else sense
        }
        return this.copy(senses = newSenses)
    }

    fun deleteSense(senseId: Long): PublicWord {
        val newSenses = senses.filter { it.id != senseId }
        if (newSenses.size == senses.size)
            throw PublicSenseNotFoundException()

        return this.copy(senses = newSenses)
    }

    fun reorderSenses(orders: List<Pair<Long, Int>>): PublicWord {
        val orderMap = orders.toMap()
        val newSenses = senses.map { sense ->
            orderMap[sense.id]?.let { newOrder ->
                sense.copy(displayOrder = newOrder)
            } ?: sense
        }.sortedBy { it.displayOrder }
        return this.copy(senses = newSenses)
    }

    fun addExample(senseId: Long, command: CreateExampleCommand): PublicWord {
        if (senses.none { it.id == senseId })
            throw PublicSenseNotFoundException()

        val newSenses = senses.map { sense ->
            if (sense.id == senseId) sense.addExample(command) else sense
        }
        return this.copy(senses = newSenses)
    }

    fun updateExample(senseId: Long, exampleId: Long, command: UpdateExampleCommand): PublicWord {
        if (senses.none { it.id == senseId })
            throw PublicSenseNotFoundException()

        val newSenses = senses.map { sense ->
            if (sense.id == senseId) sense.updateExample(exampleId, command) else sense
        }
        return this.copy(senses = newSenses)
    }

    fun deleteExample(senseId: Long, exampleId: Long): PublicWord {
        if (senses.none { it.id == senseId })
            throw PublicSenseNotFoundException()

        val newSenses = senses.map { sense ->
            if (sense.id == senseId) sense.deleteExample(exampleId) else sense
        }
        return this.copy(senses = newSenses)
    }

    fun reorderExamples(senseId: Long, orders: List<Pair<Long, Int>>): PublicWord {
        if (senses.none { it.id == senseId })
            throw PublicSenseNotFoundException()

        val newSenses = senses.map { sense ->
            if (sense.id == senseId) sense.reorderExamples(orders) else sense
        }
        return this.copy(senses = newSenses)
    }
}