package com.monovoca.server.dictionary.domain.port

import com.monovoca.server.common.domain.model.vo.Spelling
import com.monovoca.server.common.domain.model.vo.enumerated.PartOfSpeech
import com.monovoca.server.dictionary.fixture.PublicWordFixture
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@ApplyExtension(SpringExtension::class)
@Transactional
class DictionaryWordRepositoryTest(
    private val dictionaryWordRepository: DictionaryWordRepository
) : DescribeSpec({

    describe("save") {
        it("새로운 단어를 저장하면 ID가 할당된다.") {
            val word = PublicWordFixture.createWord(spelling = "apple")

            val saved = dictionaryWordRepository.save(word)

            saved.id shouldNotBe null
            saved.spelling.value shouldBe "apple"
        }

        it("sense와 example을 포함한 단어를 저장할 수 있다.") {
            val example = PublicWordFixture.createExample(
                sentence = "I like apples",
                translation = "나는 사과를 좋아한다"
            )
            val sense = PublicWordFixture.createSense(
                definition = "사과",
                engDefinition = "a fruit",
                examples = listOf(example)
            )
            val word = PublicWordFixture.createWord(spelling = "apple", senses = listOf(sense))

            val saved = dictionaryWordRepository.save(word)

            saved.id shouldNotBe null
            saved.senses shouldHaveSize 1
            saved.senses[0].definition shouldBe "사과"
            saved.senses[0].examples shouldHaveSize 1
            saved.senses[0].examples[0].sentence shouldBe "I like apples"
        }

        it("기존 단어를 수정하면 변경된 내용이 반영된다.") {
            var word = dictionaryWordRepository.save(
                PublicWordFixture.createWord(spelling = "test")
            )

            word = word.updateSpelling(Spelling("tested"))
            val updated = dictionaryWordRepository.save(word)

            updated.spelling.value shouldBe "tested"
        }

        it("sense를 추가하면 저장된다.") {
            var word = dictionaryWordRepository.save(
                PublicWordFixture.createWord(spelling = "minute")
            )

            val sense = PublicWordFixture.createSense(
                definition = "분 (시간)",
                engDefinition = "a unit of time",
                partOfSpeech = PartOfSpeech.NOUN
            )
            word = word.copy(senses = listOf(sense))
            val updated = dictionaryWordRepository.save(word)

            updated.senses shouldHaveSize 1
            updated.senses[0].definition shouldBe "분 (시간)"
        }

        it("sense를 수정하면 변경된 내용이 반영된다.") {
            val sense = PublicWordFixture.createSense(id = null, definition = "원래 뜻")
            var word = dictionaryWordRepository.save(
                PublicWordFixture.createWord(spelling = "test", senses = listOf(sense))
            )

            val updatedSense = word.senses[0].copy(definition = "수정된 뜻")
            word = word.copy(senses = listOf(updatedSense))
            val saved = dictionaryWordRepository.save(word)

            saved.senses[0].definition shouldBe "수정된 뜻"
        }

        it("sense를 삭제하면 제거된다.") {
            val sense1 = PublicWordFixture.createSense(id = null, definition = "첫 번째")
            val sense2 = PublicWordFixture.createSense(id = null, definition = "두 번째", displayOrder = 1)
            var word = dictionaryWordRepository.save(
                PublicWordFixture.createWord(spelling = "test", senses = listOf(sense1, sense2))
            )

            word = word.copy(senses = listOf(word.senses[1]))
            val saved = dictionaryWordRepository.save(word)

            saved.senses shouldHaveSize 1
            saved.senses[0].definition shouldBe "두 번째"
        }

        it("example을 추가하면 저장된다.") {
            val sense = PublicWordFixture.createSense(id = null)
            var word = dictionaryWordRepository.save(
                PublicWordFixture.createWord(spelling = "test", senses = listOf(sense))
            )

            val example = PublicWordFixture.createExample(sentence = "Test sentence")
            val updatedSense = word.senses[0].copy(examples = listOf(example))
            word = word.copy(senses = listOf(updatedSense))
            val saved = dictionaryWordRepository.save(word)

            saved.senses[0].examples shouldHaveSize 1
            saved.senses[0].examples[0].sentence shouldBe "Test sentence"
        }
    }

    describe("existsBySpelling") {
        it("동일한 spelling이 존재하면 true를 반환한다.") {
            dictionaryWordRepository.save(
                PublicWordFixture.createWord(spelling = "apple")
            )

            val exists = dictionaryWordRepository.existsBySpelling(Spelling("apple"))

            exists shouldBe true
        }

        it("존재하지 않는 spelling이면 false를 반환한다.") {
            val exists = dictionaryWordRepository.existsBySpelling(Spelling("notexist"))

            exists shouldBe false
        }
    }

    describe("findPublicWordById") {
        it("ID로 단어를 조회한다.") {
            val saved = dictionaryWordRepository.save(
                PublicWordFixture.createWord(spelling = "apple")
            )

            val found = dictionaryWordRepository.findPublicWordById(saved.id!!)

            found shouldNotBe null
            found!!.spelling.value shouldBe "apple"
        }

        it("존재하지 않는 ID로 조회하면 null을 반환한다.") {
            val found = dictionaryWordRepository.findPublicWordById(999999L)

            found shouldBe null
        }

        it("sense와 example을 함께 조회한다.") {
            val example = PublicWordFixture.createExample(sentence = "Test")
            val sense = PublicWordFixture.createSense(definition = "뜻", examples = listOf(example))
            val saved = dictionaryWordRepository.save(
                PublicWordFixture.createWord(spelling = "word", senses = listOf(sense))
            )

            val found = dictionaryWordRepository.findPublicWordById(saved.id!!)

            found shouldNotBe null
            found!!.senses shouldHaveSize 1
            found.senses[0].examples shouldHaveSize 1
        }
    }

    describe("delete") {
        it("단어를 삭제한다.") {
            val word = dictionaryWordRepository.save(
                PublicWordFixture.createWord(spelling = "delete")
            )

            dictionaryWordRepository.delete(word)

            val found = dictionaryWordRepository.findPublicWordById(word.id!!)
            found shouldBe null
        }

        it("단어를 삭제하면 연관된 sense와 example도 삭제된다.") {
            val example = PublicWordFixture.createExample()
            val sense = PublicWordFixture.createSense(examples = listOf(example))
            val word = dictionaryWordRepository.save(
                PublicWordFixture.createWord(spelling = "delete", senses = listOf(sense))
            )

            dictionaryWordRepository.delete(word)

            val found = dictionaryWordRepository.findPublicWordById(word.id!!)
            found shouldBe null
        }
    }

    describe("쿼리 측정") {
        it("findPublicWordById (4개의 sense, 각 sense당 5개의 example).") {
            val senses = (1..4).map { senseIdx ->
                val examples = (1..5).map { exIdx ->
                    PublicWordFixture.createExample(
                        sentence = "Example sentence $senseIdx-$exIdx",
                        translation = "예문 번역 $senseIdx-$exIdx",
                        displayOrder = exIdx - 1
                    )
                }
                PublicWordFixture.createSense(
                    definition = "뜻 $senseIdx",
                    engDefinition = "meaning $senseIdx",
                    partOfSpeech = PartOfSpeech.NOUN,
                    examples = examples,
                    displayOrder = senseIdx - 1
                )
            }
            val word = dictionaryWordRepository.save(
                PublicWordFixture.createWord(spelling = "query", senses = senses)
            )

            println("=== findPublicWordById 쿼리 측정 시작 ===")

            val found = dictionaryWordRepository.findPublicWordById(word.id!!)

            found shouldNotBe null
            found!!.spelling.value shouldBe "query"
            found.senses shouldHaveSize 4
            found.senses.forEach { sense ->
                sense.examples shouldHaveSize 5
            }
        }

        it("save (4개의 sense, 각 sense당 5개의 example).") {
            var word = dictionaryWordRepository.save(
                PublicWordFixture.createWord(spelling = "savetest")
            )

            val senses = (1..4).map { senseIdx ->
                val examples = (1..5).map { exIdx ->
                    PublicWordFixture.createExample(
                        sentence = "Save test sentence $senseIdx-$exIdx",
                        translation = "저장 테스트 번역 $senseIdx-$exIdx",
                        displayOrder = exIdx - 1
                    )
                }
                PublicWordFixture.createSense(
                    definition = "저장 테스트 뜻 $senseIdx",
                    engDefinition = "save test meaning $senseIdx",
                    partOfSpeech = PartOfSpeech.VERB,
                    examples = examples,
                    displayOrder = senseIdx - 1
                )
            }
            word = word.copy(senses = senses)

            println("=== save 쿼리 측정 시작 ===")

            val saved = dictionaryWordRepository.save(word)

            saved.id shouldNotBe null
            saved.senses shouldHaveSize 4
            saved.senses.forEach { sense ->
                sense.id shouldNotBe null
                sense.examples shouldHaveSize 5
                sense.examples.forEach { example ->
                    example.id shouldNotBe null
                }
            }
        }
    }
})
