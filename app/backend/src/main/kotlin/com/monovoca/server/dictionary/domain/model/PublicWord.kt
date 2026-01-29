package com.monovoca.server.dictionary.domain.model

import com.monovoca.server.common.domain.model.vo.Spelling

data class PublicWord(
    val id: Long?,
    val spelling: Spelling,
    val senses: List<PublicWordSense> = emptyList(),
)
