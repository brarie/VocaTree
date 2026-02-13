package com.monovoca.server.wordbook.domain.model.query

import com.monovoca.server.common.domain.model.vocabulary.vo.ExampleVo
import com.monovoca.server.common.domain.model.vocabulary.vo.SenseVo
import com.monovoca.server.common.domain.model.vocabulary.vo.components.Spelling

data class AutoCompleteData(
    val spelling: Spelling,
    val senses: List<SenseData>
)

data class SenseData(
    val senseVo: SenseVo,
    val examples: List<ExampleVo>
)
