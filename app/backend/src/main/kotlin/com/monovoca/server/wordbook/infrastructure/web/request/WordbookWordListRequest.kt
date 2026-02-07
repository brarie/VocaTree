package com.monovoca.server.wordbook.infrastructure.web.request

import com.monovoca.server.common.domain.model.vocabulary.vo.components.PartOfSpeech
import com.monovoca.server.common.domain.model.vocabulary.vo.components.Spelling
import com.monovoca.server.wordbook.domain.model.command.UpdateExampleCommand
import com.monovoca.server.wordbook.domain.model.command.UpdateSenseCommand
import com.monovoca.server.wordbook.domain.model.command.UpdateWordCommand

data class UpdateWordbookWordRequest(
    val spelling: String?,
) {
    fun toCommand(): UpdateWordCommand = UpdateWordCommand(
        spelling = spelling?.let { Spelling(it) }
    )
}

data class UpdateWordbookSenseRequest(
    val definition: String?,
    val engDefinition: String?,
    val britishPhonetic: String?,
    val americanPhonetic: String?,
    val partOfSpeech: PartOfSpeech?,
    val synonyms: List<String>?,
    val antonyms: List<String>?,
) {
    fun toCommand(): UpdateSenseCommand = UpdateSenseCommand(
        definition = definition,
        engDefinition = engDefinition,
        britishPhonetic = britishPhonetic,
        americanPhonetic = americanPhonetic,
        partOfSpeech = partOfSpeech,
        synonyms = synonyms?.map { Spelling(it) } ?: emptyList(),
        antonyms = antonyms?.map { Spelling(it) } ?: emptyList(),
    )
}

data class UpdateWordbookExampleRequest(
    val sentence: String?,
    val translation: String?,
) {
    fun toCommand(): UpdateExampleCommand = UpdateExampleCommand(
        sentence = sentence,
        translation = translation,
    )
}