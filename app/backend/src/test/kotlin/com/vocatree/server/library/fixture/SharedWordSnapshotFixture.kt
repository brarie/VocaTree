package com.vocatree.server.library.fixture

import com.vocatree.server.common.domain.model.vocabulary.vo.ExampleVo
import com.vocatree.server.common.domain.model.vocabulary.vo.SenseVo
import com.vocatree.server.common.domain.model.vocabulary.vo.components.Spelling
import com.vocatree.server.library.domain.model.SharedExampleSnapshot
import com.vocatree.server.library.domain.model.SharedSenseSnapshot
import com.vocatree.server.library.domain.model.SharedWordSnapshot

object SharedWordSnapshotFixture {

    fun create(
        id: Long? = null,
        spelling: Spelling? = Spelling("apple"),
        displayOrder: Int = 1,
        senses: List<SharedSenseSnapshot> = listOf(createSense()),
    ): SharedWordSnapshot = SharedWordSnapshot(
        id = id,
        spelling = spelling,
        displayOrder = displayOrder,
        senses = senses,
    )

    fun createSense(
        definition: String = "사과",
        displayOrder: Int = 1,
        examples: List<SharedExampleSnapshot> = listOf(createExample()),
    ): SharedSenseSnapshot = SharedSenseSnapshot(
        sense = SenseVo(definition = definition),
        displayOrder = displayOrder,
        examples = examples,
    )

    fun createExample(
        sentence: String = "I eat an apple.",
        translation: String = "나는 사과를 먹는다.",
        displayOrder: Int = 1,
    ): SharedExampleSnapshot = SharedExampleSnapshot(
        example = ExampleVo(sentence = sentence, translation = translation),
        displayOrder = displayOrder,
    )
}
