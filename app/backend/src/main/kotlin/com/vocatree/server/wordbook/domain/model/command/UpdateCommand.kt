package com.vocatree.server.wordbook.domain.model.command

import com.vocatree.server.common.domain.model.vocabulary.vo.components.PartOfSpeech
import com.vocatree.server.common.domain.model.vocabulary.vo.components.Spelling

class UpdateWordCommand(
    val spelling: Spelling? = null,
)

class UpdateSenseCommand(
    val definition: String? = null,
    val engDefinition: String? = null,
    val britishPhonetic: String? = null,
    val americanPhonetic: String? = null,
    val partOfSpeech: PartOfSpeech? = null,
    val synonyms: List<Spelling> = emptyList(),
    val antonyms: List<Spelling> = emptyList(),
)

class UpdateExampleCommand(
    val sentence: String? = null,
    val translation: String? = null,
)