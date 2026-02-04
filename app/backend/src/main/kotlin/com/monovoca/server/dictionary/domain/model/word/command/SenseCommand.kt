package com.monovoca.server.dictionary.domain.model.word.command

import com.monovoca.server.common.domain.model.vocabulary.vo.components.Spelling
import com.monovoca.server.common.domain.model.vocabulary.vo.components.PartOfSpeech

data class CreateSenseCommand(
    val definition: String,
    val engDefinition: String? = null,
    val britishPhonetic: String? = null,
    val americanPhonetic: String? = null,
    val partOfSpeech: PartOfSpeech? = null,
    val synonyms: List<Spelling> = emptyList(),
    val antonyms: List<Spelling> = emptyList()
)

data class UpdateSenseCommand(
    val definition: String,
    val engDefinition: String? = null,
    val britishPhonetic: String? = null,
    val americanPhonetic: String? = null,
    val partOfSpeech: PartOfSpeech? = null,
    val synonyms: List<Spelling> = emptyList(),
    val antonyms: List<Spelling> = emptyList(),
)
