package com.vocatree.server.dictionary.domain.model.word

import com.vocatree.server.common.domain.model.vocabulary.vo.components.Spelling
import com.vocatree.server.common.domain.model.vocabulary.vo.components.PartOfSpeech
import com.vocatree.server.dictionary.domain.exception.PublicExampleNotFoundException
import com.vocatree.server.dictionary.domain.model.word.command.CreateExampleCommand
import com.vocatree.server.dictionary.domain.model.word.command.CreateSenseCommand
import com.vocatree.server.dictionary.domain.model.word.command.UpdateExampleCommand
import com.vocatree.server.dictionary.domain.model.word.command.UpdateSenseCommand
import com.vocatree.server.dictionary.fixture.PublicWordFixture
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

class PublicSenseTest : DescribeSpec({

    describe("create") {
        it("command로 sense를 생성할 수 있다") {
            val command = CreateSenseCommand(
                definition = "테스트 뜻",
                engDefinition = "test meaning",
                britishPhonetic = "/test/",
                americanPhonetic = "/test/",
                partOfSpeech = PartOfSpeech.NOUN,
                synonyms = listOf(Spelling("synonym")),
                antonyms = listOf(Spelling("antonym"))
            )

            val sense = PublicSense.create(command, displayOrder = 0)

            sense.sense.definition shouldBe "테스트 뜻"
            sense.sense.engDefinition shouldBe "test meaning"
            sense.sense.partOfSpeech shouldBe PartOfSpeech.NOUN
            sense.sense.synonyms shouldHaveSize 1
            sense.sense.antonyms shouldHaveSize 1
            sense.displayOrder shouldBe 0
        }
    }

    describe("update") {
        it("command로 sense를 업데이트할 수 있다") {
            val sense = PublicWordFixture.createSense(id = 1L, definition = "원래 뜻")
            val command = UpdateSenseCommand(
                definition = "수정된 뜻",
                engDefinition = "updated meaning",
                britishPhonetic = "/updated/",
                americanPhonetic = "/updated/",
                partOfSpeech = PartOfSpeech.VERB,
                synonyms = listOf(Spelling("new")),
                antonyms = emptyList()
            )

            val updated = sense.update(command)

            updated.sense.definition shouldBe "수정된 뜻"
            updated.sense.engDefinition shouldBe "updated meaning"
            updated.sense.partOfSpeech shouldBe PartOfSpeech.VERB
            updated.displayOrder shouldBe sense.displayOrder
        }
    }

    describe("addExample") {
        it("새로운 example을 추가할 수 있다") {
            val sense = PublicWordFixture.createSense(id = 1L)
            val command = CreateExampleCommand(
                sentence = "Test sentence",
                translation = "테스트 문장"
            )

            val updated = sense.addExample(command)

            updated.examples shouldHaveSize 1
            updated.examples[0].example.sentence shouldBe "Test sentence"
            updated.examples[0].displayOrder shouldBe 0
        }

        it("이미 example이 있으면 다음 displayOrder로 추가된다") {
            val example1 = PublicWordFixture.createExample(id = 1L, displayOrder = 0)
            val example2 = PublicWordFixture.createExample(id = 2L, displayOrder = 1)
            val sense = PublicWordFixture.createSense(id = 1L, examples = listOf(example1, example2))
            val command = CreateExampleCommand(sentence = "Third example")

            val updated = sense.addExample(command)

            updated.examples shouldHaveSize 3
            updated.examples[2].displayOrder shouldBe 2
        }
    }

    describe("updateExample") {
        it("존재하는 example을 수정할 수 있다") {
            val example = PublicWordFixture.createExample(id = 1L, sentence = "Original", displayOrder = 0)
            val sense = PublicWordFixture.createSense(id = 1L, examples = listOf(example))
            val command = UpdateExampleCommand(
                sentence = "Updated",
                translation = "수정됨",
                isOfficial = true,
                sourceInfo = "test source"
            )

            val updated = sense.updateExample(1L, command)

            updated.examples[0].example.sentence shouldBe "Updated"
            updated.examples[0].example.translation shouldBe "수정됨"
            updated.examples[0].isOfficial shouldBe true
            updated.examples[0].displayOrder shouldBe 0
        }

        it("존재하지 않는 example을 수정하면 예외가 발생한다") {
            val sense = PublicWordFixture.createSense(id = 1L)
            val command = UpdateExampleCommand(
                sentence = "Test",
                translation = null,
                isOfficial = false,
                sourceInfo = null
            )

            shouldThrow<PublicExampleNotFoundException> {
                sense.updateExample(999L, command)
            }
        }
    }

    describe("deleteExample") {
        it("존재하는 example을 삭제할 수 있다") {
            val example1 = PublicWordFixture.createExample(id = 1L, displayOrder = 0)
            val example2 = PublicWordFixture.createExample(id = 2L, displayOrder = 1)
            val sense = PublicWordFixture.createSense(id = 1L, examples = listOf(example1, example2))

            val updated = sense.deleteExample(1L)

            updated.examples shouldHaveSize 1
            updated.examples[0].id shouldBe 2L
        }

        it("존재하지 않는 example을 삭제하면 예외가 발생한다") {
            val sense = PublicWordFixture.createSense(id = 1L)

            shouldThrow<PublicExampleNotFoundException> {
                sense.deleteExample(999L)
            }
        }
    }

    describe("reorderExamples") {
        it("example들의 순서를 변경할 수 있다") {
            val example1 = PublicWordFixture.createExample(id = 1L, displayOrder = 0)
            val example2 = PublicWordFixture.createExample(id = 2L, displayOrder = 1)
            val example3 = PublicWordFixture.createExample(id = 3L, displayOrder = 2)
            val sense = PublicWordFixture.createSense(id = 1L, examples = listOf(example1, example2, example3))

            val updated = sense.reorderExamples(listOf(3L to 0, 1L to 1, 2L to 2))

            updated.examples[0].id shouldBe 3L
            updated.examples[1].id shouldBe 1L
            updated.examples[2].id shouldBe 2L
        }
    }

    describe("init") {
        it("examples가 displayOrder로 정렬되어 있지 않으면 예외가 발생한다") {
            val example1 = PublicWordFixture.createExample(id = 1L, displayOrder = 1)
            val example2 = PublicWordFixture.createExample(id = 2L, displayOrder = 0)

            shouldThrow<IllegalArgumentException> {
                PublicWordFixture.createSense(examples = listOf(example1, example2))
            }
        }
    }
})
