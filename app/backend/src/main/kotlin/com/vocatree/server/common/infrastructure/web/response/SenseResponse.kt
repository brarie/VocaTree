package com.vocatree.server.common.infrastructure.web.response

import com.vocatree.server.common.domain.model.vocabulary.vo.SenseVo

data class SenseResponse(
    val definition: String?,
    val engDefinition: String?,
    val britishPhonetic: String?,
    val americanPhonetic: String?,
    val partOfSpeech: String?,
    val synonyms: List<String> = emptyList(),
    val antonyms: List<String> = emptyList()
) {
    companion object {
        fun from(senseVo: SenseVo): SenseResponse {
            return SenseResponse(
                definition = senseVo.definition,
                engDefinition = senseVo.engDefinition,
                britishPhonetic = senseVo.britishPhonetic,
                americanPhonetic = senseVo.americanPhonetic,
                partOfSpeech = senseVo.partOfSpeech?.name,
                synonyms = senseVo.synonyms.map { it.value },
                antonyms = senseVo.antonyms.map { it.value }
            )
        }
    }
}
