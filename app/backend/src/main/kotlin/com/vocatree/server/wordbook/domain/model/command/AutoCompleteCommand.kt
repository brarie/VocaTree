package com.vocatree.server.wordbook.domain.model.command

import com.vocatree.server.common.domain.model.vocabulary.vo.ExampleVo
import com.vocatree.server.common.domain.model.vocabulary.vo.SenseVo
import com.vocatree.server.common.domain.model.vocabulary.vo.components.Spelling
import com.vocatree.server.wordbook.domain.model.query.AutoCompleteData

data class AutoCompleteCommand(
    val spelling: Spelling,
    val senses: List<SenseData>,
) {
    data class SenseData(
        val senseVo: SenseVo,
        val examples: List<ExampleVo>
    )

    companion object {
        fun from(data: AutoCompleteData): AutoCompleteCommand {
            return AutoCompleteCommand(
                spelling = data.spelling,
                senses = data.senses.map {
                    SenseData(
                        senseVo = it.senseVo,
                        examples = it.examples
                    )
                }
            )
        }
    }
}
