package com.vocatree.server.wordbook.domain.model.command

data class AutoCompleteDataRequestCommand(
    val dictionaryWordId: Long,
    val selectedSenses: List<SenseSelection>
) {
    data class SenseSelection(
        val senseId: Long,
        val exampleIds: List<Long>
    )
}
