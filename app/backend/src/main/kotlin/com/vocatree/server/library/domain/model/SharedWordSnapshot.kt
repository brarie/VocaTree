package com.vocatree.server.library.domain.model

import com.vocatree.server.common.domain.model.vocabulary.vo.ExampleVo
import com.vocatree.server.common.domain.model.vocabulary.vo.SenseVo
import com.vocatree.server.common.domain.model.vocabulary.vo.components.Spelling

data class SharedWordSnapshot(
    val id: Long? = null,
    val spelling: Spelling?,
    val displayOrder: Int,
    val senses: List<SharedSenseSnapshot>,
)

data class SharedSenseSnapshot(
    val sense: SenseVo,
    val displayOrder: Int,
    val examples: List<SharedExampleSnapshot>
)

data class SharedExampleSnapshot(
    val example: ExampleVo,
    val displayOrder: Int,
)