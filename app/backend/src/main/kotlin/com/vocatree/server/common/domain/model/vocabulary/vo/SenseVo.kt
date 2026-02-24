package com.vocatree.server.common.domain.model.vocabulary.vo

import com.vocatree.server.common.domain.model.vocabulary.vo.components.PartOfSpeech
import com.vocatree.server.common.domain.model.vocabulary.vo.components.Spelling

data class SenseVo(
    val definition: String? = null,
    val engDefinition: String? = null,
    val britishPhonetic: String? = null,
    val americanPhonetic: String? = null,
    val partOfSpeech: PartOfSpeech? = null,
    val synonyms: List<Spelling> = emptyList(),
    val antonyms: List<Spelling> = emptyList()
) {
    fun isBlank(): Boolean {
        return definition.isNullOrBlank()
            && engDefinition.isNullOrBlank()
            && britishPhonetic.isNullOrBlank()
            && americanPhonetic.isNullOrBlank()
            && partOfSpeech == null
            && synonyms.isEmpty()
            && antonyms.isEmpty()
    }
}