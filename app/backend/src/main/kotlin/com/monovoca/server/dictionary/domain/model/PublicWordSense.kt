package com.monovoca.server.dictionary.domain.model

import com.monovoca.server.common.domain.model.vo.enumerated.PartOfSpeech
import com.monovoca.server.common.domain.model.vo.Spelling

data class PublicWordSense(
    val id: Long?,
    val definition: String,
    val engDefinition: String?,
    val phonetic: String?,
    val synonyms: List<Spelling> = emptyList(),
    val antonyms: List<Spelling> = emptyList(),
    val partOfSpeech: PartOfSpeech?,
    val examples: List<PublicWordExample> = emptyList(),
)
