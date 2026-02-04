package com.monovoca.server.dictionary.domain.model.word

import com.monovoca.server.common.domain.model.vocabulary.vo.components.Spelling
import com.monovoca.server.dictionary.domain.exception.PublicSenseNotFoundException
import com.monovoca.server.dictionary.domain.model.word.command.CreateSenseCommand
import com.monovoca.server.dictionary.domain.model.word.command.UpdateSenseCommand
import com.monovoca.server.dictionary.fixture.PublicWordFixture
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

class PublicWordTest : DescribeSpec({

    describe("updateSpelling") {
        it("spelling을 변경할 수 있다") {
            val word = PublicWordFixture.createWord(id = 1L, spelling = "original")

            val updated = word.updateSpelling(Spelling("changed"))

            updated.spelling.value shouldBe "changed"
            updated.id shouldBe 1L
        }
    }

    describe("addSense") {
        it("새로운 sense를 추가할 수 있다") {
            val word = PublicWordFixture.createWord(id = 1L)
            val command = CreateSenseCommand(
                definition = "새로운 뜻",
                engDefinition = "new meaning"
            )

            val updated = word.addSense(command)

            updated.senses shouldHaveSize 1
            updated.senses[0].sense.definition shouldBe "새로운 뜻"
            updated.senses[0].displayOrder shouldBe 0
        }

        it("이미 sense가 있으면 다음 displayOrder로 추가된다") {
            val sense1 = PublicWordFixture.createSense(id = 1L, displayOrder = 0)
            val sense2 = PublicWordFixture.createSense(id = 2L, displayOrder = 1)
            val word = PublicWordFixture.createWord(id = 1L, senses = listOf(sense1, sense2))
            val command = CreateSenseCommand(definition = "세 번째 뜻")

            val updated = word.addSense(command)

            updated.senses shouldHaveSize 3
            updated.senses[2].displayOrder shouldBe 2
        }
    }

    describe("updateSense") {
        it("존재하는 sense를 수정할 수 있다") {
            val sense = PublicWordFixture.createSense(id = 1L, definition = "원래 뜻", displayOrder = 0)
            val word = PublicWordFixture.createWord(id = 1L, senses = listOf(sense))
            val command = UpdateSenseCommand(
                definition = "수정된 뜻",
                engDefinition = "updated meaning",
                britishPhonetic = null,
                americanPhonetic = null,
                partOfSpeech = null,
                synonyms = emptyList(),
                antonyms = emptyList()
            )

            val updated = word.updateSense(1L, command)

            updated.senses[0].sense.definition shouldBe "수정된 뜻"
            updated.senses[0].sense.engDefinition shouldBe "updated meaning"
            updated.senses[0].displayOrder shouldBe 0
        }

        it("존재하지 않는 sense를 수정하면 PublicSenseNotFoundException이 발생한다") {
            val word = PublicWordFixture.createWord(id = 1L)
            val command = UpdateSenseCommand(
                definition = "뜻",
                engDefinition = null,
                britishPhonetic = null,
                americanPhonetic = null,
                partOfSpeech = null,
                synonyms = emptyList(),
                antonyms = emptyList()
            )

            shouldThrow<PublicSenseNotFoundException> {
                word.updateSense(999L, command)
            }
        }
    }

    describe("deleteSense") {
        it("존재하는 sense를 삭제할 수 있다") {
            val sense1 = PublicWordFixture.createSense(id = 1L, displayOrder = 0)
            val sense2 = PublicWordFixture.createSense(id = 2L, displayOrder = 1)
            val word = PublicWordFixture.createWord(id = 1L, senses = listOf(sense1, sense2))

            val updated = word.deleteSense(1L)

            updated.senses shouldHaveSize 1
            updated.senses[0].id shouldBe 2L
        }

        it("존재하지 않는 sense를 삭제하면 예외가 발생한다") {
            val word = PublicWordFixture.createWord(id = 1L)

            shouldThrow<PublicSenseNotFoundException> {
                word.deleteSense(999L)
            }
        }
    }

    describe("reorderSenses") {
        it("sense들의 순서를 변경할 수 있다") {
            val sense1 = PublicWordFixture.createSense(id = 1L, displayOrder = 0)
            val sense2 = PublicWordFixture.createSense(id = 2L, displayOrder = 1)
            val sense3 = PublicWordFixture.createSense(id = 3L, displayOrder = 2)
            val word = PublicWordFixture.createWord(id = 1L, senses = listOf(sense1, sense2, sense3))

            val updated = word.reorderSenses(listOf(3L to 0, 1L to 1, 2L to 2))

            updated.senses[0].id shouldBe 3L
            updated.senses[1].id shouldBe 1L
            updated.senses[2].id shouldBe 2L
        }
    }

    describe("init") {
        it("senses가 displayOrder로 정렬되어 있지 않으면 예외가 발생한다") {
            val sense1 = PublicWordFixture.createSense(id = 1L, displayOrder = 1)
            val sense2 = PublicWordFixture.createSense(id = 2L, displayOrder = 0)

            shouldThrow<IllegalArgumentException> {
                PublicWordFixture.createWord(senses = listOf(sense1, sense2))
            }
        }
    }
})
