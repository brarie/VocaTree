package com.monovoca.server.wordbook.domain.model

import com.monovoca.server.common.domain.model.vocabulary.vo.ExampleVo
import com.monovoca.server.common.domain.model.vocabulary.vo.SenseVo
import com.monovoca.server.common.domain.model.vocabulary.vo.components.Spelling
import com.monovoca.server.wordbook.domain.model.command.AutoCompleteCommand
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

class WordbookWordTest : DescribeSpec({

    describe("autoComplete") {
        context("완전히 빈 단어 (sense 1개, example 1개, 모두 빈 값)일 때") {
            it("자동완성 데이터로 기존 데이터를 대체한다.") {
                val emptyExample = WordbookExample.create(displayOrder = 1)
                val emptySense = WordbookSense.create(displayOrder = 1).copy(examples = listOf(emptyExample))
                val word = WordbookWord.create(displayOrder = 1).copy(senses = listOf(emptySense))

                val command = AutoCompleteCommand(
                    spelling = Spelling("apple"),
                    senses = listOf(
                        AutoCompleteCommand.SenseData(
                            senseVo = SenseVo(definition = "사과"),
                            examples = listOf(
                                ExampleVo(sentence = "I like apples.", translation = "나는 사과를 좋아한다.")
                            )
                        ),
                        AutoCompleteCommand.SenseData(
                            senseVo = SenseVo(definition = "애플 (회사)"),
                            examples = emptyList()
                        )
                    )
                )

                val result = word.autoComplete(command)

                result.spelling shouldBe Spelling("apple")
                result.senses shouldHaveSize 2
                result.senses[0].sense.definition shouldBe "사과"
                result.senses[0].examples shouldHaveSize 1
                result.senses[0].examples[0].example.sentence shouldBe "I like apples."
                result.senses[0].examples[0].example.translation shouldBe "나는 사과를 좋아한다."
                result.senses[1].sense.definition shouldBe "애플 (회사)"
                result.senses[1].examples shouldHaveSize 1 // 빈 예문 자동 생성
            }
        }

        context("데이터가 있는 단어일 때") {
            it("자동완성 데이터를 기존 sense 뒤에 추가한다.") {
                val existingExample = WordbookExample(
                    example = ExampleVo(sentence = "existing sentence"),
                    displayOrder = 1
                )
                val existingSense = WordbookSense(
                    sense = SenseVo(definition = "기존 뜻"),
                    displayOrder = 1,
                    examples = listOf(existingExample)
                )
                val word = WordbookWord(
                    spelling = Spelling("test"),
                    displayOrder = 1,
                    senses = listOf(existingSense)
                )

                val command = AutoCompleteCommand(
                    spelling = Spelling("test"),
                    senses = listOf(
                        AutoCompleteCommand.SenseData(
                            senseVo = SenseVo(definition = "시험"),
                            examples = listOf(ExampleVo(sentence = "This is a test."))
                        )
                    )
                )

                val result = word.autoComplete(command)

                result.spelling shouldBe Spelling("test")
                result.senses shouldHaveSize 2
                result.senses[0].sense.definition shouldBe "기존 뜻"
                result.senses[0].displayOrder shouldBe 1
                result.senses[1].sense.definition shouldBe "시험"
                result.senses[1].displayOrder shouldBe 2
                result.senses[1].examples[0].example.sentence shouldBe "This is a test."
            }
        }

        context("자동완성 데이터에 빈 examples가 들어올 때") {
            it("빈 예문을 자동으로 생성한다.") {
                val emptyExample = WordbookExample.create(displayOrder = 1)
                val emptySense = WordbookSense.create(displayOrder = 1).copy(examples = listOf(emptyExample))
                val word = WordbookWord.create(displayOrder = 1).copy(senses = listOf(emptySense))

                val command = AutoCompleteCommand(
                    spelling = Spelling("test"),
                    senses = listOf(
                        AutoCompleteCommand.SenseData(
                            senseVo = SenseVo(definition = "테스트"),
                            examples = emptyList() // 빈 예문 리스트
                        )
                    )
                )

                val result = word.autoComplete(command)

                result.senses shouldHaveSize 1
                result.senses[0].examples shouldHaveSize 1
                result.senses[0].examples[0].example.isBlank() shouldBe true
            }
        }
    }
})
