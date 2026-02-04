package com.monovoca.server.common.domain.model.vocabulary.vo

import com.monovoca.server.common.domain.model.vocabulary.vo.components.PartOfSpeech
import com.monovoca.server.common.domain.model.vocabulary.vo.components.Spelling

data class SenseVo(
    val definition: String?,
    val engDefinition: String?,
    val britishPhonetic: String?,
    val americanPhonetic: String?,
    val partOfSpeech: PartOfSpeech?,
    val synonyms: List<Spelling> = emptyList(),
    val antonyms: List<Spelling> = emptyList()
)
