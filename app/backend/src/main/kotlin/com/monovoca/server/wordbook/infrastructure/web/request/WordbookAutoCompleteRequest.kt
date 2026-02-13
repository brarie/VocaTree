package com.monovoca.server.wordbook.infrastructure.web.request

import com.monovoca.server.wordbook.domain.model.command.AutoCompleteDataRequestCommand

data class AutoCompleteFromDictionaryRequest(
    val dictionaryWordId: Long,
    val selectedSenses: List<SenseSelection>
) {
    data class SenseSelection(
        val dictionarySenseId: Long,
        val dictionaryExampleIds: List<Long>
    )

    fun toRequestCommand(): AutoCompleteDataRequestCommand {
        return AutoCompleteDataRequestCommand(
            dictionaryWordId = dictionaryWordId,
            selectedSenses = selectedSenses.map {
                AutoCompleteDataRequestCommand.SenseSelection(
                    senseId = it.dictionarySenseId,
                    exampleIds = it.dictionaryExampleIds
                )
            }
        )
    }
}