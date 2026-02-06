package com.monovoca.server.wordbook.domain.model

import com.monovoca.server.common.domain.model.vocabulary.vo.ExampleVo

data class WordbookExample(
    val id: Long? = null,
    val senseId: Long,
    val example: ExampleVo,
    val displayOrder: Int = 0,
)
