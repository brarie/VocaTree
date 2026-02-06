package com.monovoca.server.wordbook.domain.model

import com.monovoca.server.common.domain.model.vocabulary.vo.SenseVo

data class WordbookSense(
    val id: Long? = null,
    val wordId: Long,
    val sense: SenseVo,
    val displayOrder: Int = 0,
    val examples: List<WordbookExample> = emptyList(),
)
