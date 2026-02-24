package com.vocatree.server.dictionary.infrastructure.web.request

import com.vocatree.server.common.domain.model.vocabulary.vo.components.Spelling
import com.vocatree.server.common.domain.model.vocabulary.vo.components.SpellingPolicy
import com.vocatree.server.common.domain.model.vocabulary.vo.components.PartOfSpeech
import com.vocatree.server.dictionary.domain.model.word.command.CreateExampleCommand
import com.vocatree.server.dictionary.domain.model.word.command.CreateSenseCommand
import com.vocatree.server.dictionary.domain.model.word.command.UpdateExampleCommand
import com.vocatree.server.dictionary.domain.model.word.command.UpdateSenseCommand
import jakarta.validation.constraints.Pattern

data class CreatePublicWordRequest(
    @field:Pattern(regexp = SpellingPolicy.regex, message = SpellingPolicy.message)
    val spelling: String,
)

data class UpdatePublicWordRequest(
    @field:Pattern(regexp = SpellingPolicy.regex, message = SpellingPolicy.message)
    val spelling: String,
)

data class CreateSenseRequest(
    val definition: String,
    val engDefinition: String?,
    val britishPhonetic: String?,
    val americanPhonetic: String?,
    val partOfSpeech: PartOfSpeech?,
    val synonyms: List<String>?,
    val antonyms: List<String>?,
) {
    fun toCommand(): CreateSenseCommand = CreateSenseCommand(
        definition = definition,
        engDefinition = engDefinition,
        britishPhonetic = britishPhonetic,
        americanPhonetic = americanPhonetic,
        partOfSpeech = partOfSpeech,
        synonyms = synonyms?.map { Spelling(it) } ?: emptyList(),
        antonyms = antonyms?.map { Spelling(it) } ?: emptyList(),
    )
}

data class UpdateSenseRequest(
    val definition: String,
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

data class ReorderSensesRequest(
    val orders: List<SenseOrder>
) {
    data class SenseOrder(
        val id: Long,
        val displayOrder: Int
    )

    fun toOrderList(): List<Pair<Long, Int>> = orders.map { it.id to it.displayOrder }
}

data class CreateExampleRequest(
    val sentence: String,
    val translation: String?,
    val isOfficial: Boolean,
    val sourceInfo: String?
) {
    fun toCommand(): CreateExampleCommand = CreateExampleCommand(
        sentence = sentence,
        translation = translation,
        isOfficial = isOfficial,
        sourceInfo = sourceInfo
    )
}

data class UpdateExampleRequest(
    val sentence: String,
    val translation: String?,
    val isOfficial: Boolean,
    val sourceInfo: String?
) {
    fun toCommand(): UpdateExampleCommand = UpdateExampleCommand(
        sentence = sentence,
        translation = translation,
        isOfficial = isOfficial,
        sourceInfo = sourceInfo
    )
}

data class ReorderExamplesRequest(
    val orders: List<ExampleOrder>
) {
    data class ExampleOrder(
        val id: Long,
        val displayOrder: Int
    )

    fun toOrderList(): List<Pair<Long, Int>> = orders.map { it.id to it.displayOrder }
}