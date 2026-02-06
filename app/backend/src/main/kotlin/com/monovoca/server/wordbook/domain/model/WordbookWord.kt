package com.monovoca.server.wordbook.domain.model

import com.monovoca.server.common.domain.model.vocabulary.vo.components.Spelling

data class WordbookWord(
    val id: Long? = null,
    val wordbookId: Long,
    val spelling: Spelling?,
    val displayOrder: Int = 0,
    val senses: List<WordbookSense> = emptyList(),
)
