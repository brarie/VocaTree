package com.monovoca.server.dictionary.infrastructure.web.request

import com.monovoca.server.common.domain.model.vo.Spelling
import com.monovoca.server.common.domain.model.vo.SpellingPolicy
import com.monovoca.server.common.domain.model.vo.enumerated.PartOfSpeech
import com.monovoca.server.dictionary.domain.model.word.command.CreateExampleCommand
import com.monovoca.server.dictionary.domain.model.word.command.CreateSenseCommand
import com.monovoca.server.dictionary.domain.model.word.command.UpdateExampleCommand
import com.monovoca.server.dictionary.domain.model.word.command.UpdateSenseCommand
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
    val synonyms: List<String>,
    val antonyms: List<String>
) {
    fun toCommand(): CreateSenseCommand = CreateSenseCommand(
        definition = definition,
        engDefinition = engDefinition,
        britishPhonetic = britishPhonetic,
        americanPhonetic = americanPhonetic,
        partOfSpeech = partOfSpeech,
        synonyms = synonyms.map { Spelling(it) },
        antonyms = antonyms.map { Spelling(it) }
    )
}

data class UpdateSenseRequest(
    val definition: String,
    val engDefinition: String?,
    val britishPhonetic: String?,
    val americanPhonetic: String?,
    val partOfSpeech: PartOfSpeech?,
    val synonyms: List<String>,
    val antonyms: List<String>
) {
    fun toCommand(): UpdateSenseCommand = UpdateSenseCommand(
        definition = definition,
        engDefinition = engDefinition,
        britishPhonetic = britishPhonetic,
        americanPhonetic = americanPhonetic,
        partOfSpeech = partOfSpeech,
        synonyms = synonyms.map { Spelling(it) },
        antonyms = antonyms.map { Spelling(it) }
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