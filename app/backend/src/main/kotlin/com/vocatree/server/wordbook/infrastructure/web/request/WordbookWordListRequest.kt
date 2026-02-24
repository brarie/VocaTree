package com.vocatree.server.wordbook.infrastructure.web.request

import com.vocatree.server.common.domain.model.vocabulary.vo.components.PartOfSpeech
import com.vocatree.server.common.domain.model.vocabulary.vo.components.Spelling
import com.vocatree.server.common.domain.model.vocabulary.vo.components.SpellingPolicy
import com.vocatree.server.wordbook.domain.model.command.UpdateExampleCommand
import com.vocatree.server.wordbook.domain.model.command.UpdateSenseCommand
import com.vocatree.server.wordbook.domain.model.command.UpdateWordCommand
import jakarta.validation.constraints.Pattern

data class UpdateWordbookWordRequest(
    @field:Pattern(regexp = SpellingPolicy.regex, message = SpellingPolicy.message)
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

data class SwapWordbookWordsOrderRequest(
    val wordId1: Long,
    val wordId2: Long
)

data class SwapWordbookSensesOrderRequest(
    val senseId1: Long,
    val senseId2: Long
)

data class SwapWordbookExamplesOrderRequest(
    val exampleId1: Long,
    val exampleId2: Long
)