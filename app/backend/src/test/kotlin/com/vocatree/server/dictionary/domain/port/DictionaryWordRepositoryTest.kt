package com.vocatree.server.dictionary.domain.port

import com.vocatree.server.common.domain.model.vocabulary.vo.components.Spelling
import com.vocatree.server.common.domain.model.vocabulary.vo.components.PartOfSpeech
import com.vocatree.server.dictionary.fixture.PublicWordFixture
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
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
            saved.senses[0].sense.definition shouldBe "사과"
            saved.senses[0].examples shouldHaveSize 1
            saved.senses[0].examples[0].example.sentence shouldBe "I like apples"
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
            updated.senses[0].sense.definition shouldBe "분 (시간)"
        }

        it("sense를 수정하면 변경된 내용이 반영된다.") {
            val sense = PublicWordFixture.createSense(id = null, definition = "원래 뜻")
            var word = dictionaryWordRepository.save(
                PublicWordFixture.createWord(spelling = "test", senses = listOf(sense))
            )

            val updatedSense = word.senses[0].copy(
                sense = word.senses[0].sense.copy(definition = "수정된 뜻")
            )
            word = word.copy(senses = listOf(updatedSense))
            val saved = dictionaryWordRepository.save(word)

            saved.senses[0].sense.definition shouldBe "수정된 뜻"
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
            saved.senses[0].sense.definition shouldBe "두 번째"
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
            saved.senses[0].examples[0].example.sentence shouldBe "Test sentence"
        }
    }

    describe("existsBySpelling") {
        it("동일한 spelling이 존재하면 true를 반환한다.") {
            dictionaryWordRepository.save(
                PublicWordFixture.createWord(spelling = "apple", slug = "apple")
            )

            val exists = dictionaryWordRepository.existsBySpelling(Spelling("apple"))

            exists shouldBe true
        }

        it("존재하지 않는 spelling이면 false를 반환한다.") {
            val exists = dictionaryWordRepository.existsBySpelling(Spelling("notexist"))

            exists shouldBe false
        }
    }

    describe("existsBySlug") {
        it("동일한 slug가 존재하면 true를 반환한다.") {
            dictionaryWordRepository.save(
                PublicWordFixture.createWord(spelling = "well-known", slug = "well-known")
            )

            val exists = dictionaryWordRepository.existsBySlug("well-known")

            exists shouldBe true
        }

        it("존재하지 않는 slug이면 false를 반환한다.") {
            val exists = dictionaryWordRepository.existsBySlug("notexist")

            exists shouldBe false
        }
    }

    describe("findById") {
        it("ID로 단어를 조회한다.") {
            val saved = dictionaryWordRepository.save(
                PublicWordFixture.createWord(spelling = "apple", slug = "apple")
            )

            val found = dictionaryWordRepository.findById(saved.id!!)

            found shouldNotBe null
            found!!.spelling.value shouldBe "apple"
            found.slug shouldBe "apple"
        }

        it("존재하지 않는 ID로 조회하면 null을 반환한다.") {
            val found = dictionaryWordRepository.findById(999999L)

            found shouldBe null
        }

        it("sense와 example을 함께 조회한다.") {
            val example = PublicWordFixture.createExample(sentence = "Test")
            val sense = PublicWordFixture.createSense(definition = "뜻", examples = listOf(example))
            val saved = dictionaryWordRepository.save(
                PublicWordFixture.createWord(spelling = "word", slug = "word", senses = listOf(sense))
            )

            val found = dictionaryWordRepository.findById(saved.id!!)

            found shouldNotBe null
            found!!.senses shouldHaveSize 1
            found.senses[0].examples shouldHaveSize 1
        }
    }

    describe("findBySlug") {
        it("slug로 단어를 조회한다.") {
            val saved = dictionaryWordRepository.save(
                PublicWordFixture.createWord(spelling = "don't", slug = "don-t")
            )

            val found = dictionaryWordRepository.findBySlug("don-t")

            found shouldNotBe null
            found!!.id shouldBe saved.id
            found.spelling.value shouldBe "don't"
        }

        it("존재하지 않는 slug로 조회하면 null을 반환한다.") {
            val found = dictionaryWordRepository.findBySlug("notexist")

            found shouldBe null
        }

        it("sense와 example을 함께 조회한다.") {
            val example = PublicWordFixture.createExample(sentence = "Don't do that")
            val sense = PublicWordFixture.createSense(definition = "~하지 마라", examples = listOf(example))
            val saved = dictionaryWordRepository.save(
                PublicWordFixture.createWord(spelling = "don't", slug = "don-t", senses = listOf(sense))
            )

            val found = dictionaryWordRepository.findBySlug("don-t")

            found shouldNotBe null
            found!!.senses shouldHaveSize 1
            found.senses[0].examples shouldHaveSize 1
        }
    }

    describe("paginateViewsBySpelling") {
        it("prefix 검색으로 단어를 조회한다.") {
            val sense1 = PublicWordFixture.createSense(definition = "뜻1", displayOrder = 0)
            val sense2 = PublicWordFixture.createSense(definition = "뜻2", displayOrder = 1)
            val sense3 = PublicWordFixture.createSense(definition = "뜻3", displayOrder = 2)
            dictionaryWordRepository.save(
                PublicWordFixture.createWord(
                    spelling = "apple",
                    slug = "apple",
                    senses = listOf(sense1, sense2, sense3)
                )
            )
            dictionaryWordRepository.save(
                PublicWordFixture.createWord(spelling = "application", slug = "application")
            )
            dictionaryWordRepository.save(
                PublicWordFixture.createWord(spelling = "apply", slug = "apply")
            )

            val page = dictionaryWordRepository.paginateViewsBySpelling("app", PageRequest.of(0, 10))

            page.totalElements shouldBe 3
            page.content shouldHaveSize 3
            page.content[0].spelling shouldBe "apple"
            page.content[0].slug shouldBe "apple"
            page.content[0].topDefinitions shouldHaveSize 3
            page.content[1].spelling shouldBe "apply"
            page.content[1].topDefinitions shouldHaveSize 0
            page.content[2].spelling shouldBe "application"
        }

        it("길이순으로 정렬된다.") {
            dictionaryWordRepository.save(
                PublicWordFixture.createWord(spelling = "test", slug = "test")
            )
            dictionaryWordRepository.save(
                PublicWordFixture.createWord(spelling = "testing", slug = "testing")
            )
            dictionaryWordRepository.save(
                PublicWordFixture.createWord(spelling = "tester", slug = "tester")
            )

            val page = dictionaryWordRepository.paginateViewsBySpelling("test", PageRequest.of(0, 10))

            page.content[0].spelling shouldBe "test"
            page.content[1].spelling shouldBe "tester"
            page.content[2].spelling shouldBe "testing"
        }

        it("topDefinitions는 최대 3개까지만 포함한다.") {
            val senses = (1..5).map { PublicWordFixture.createSense(definition = "뜻$it", displayOrder = it - 1) }
            dictionaryWordRepository.save(
                PublicWordFixture.createWord(spelling = "minute", slug = "minute", senses = senses)
            )

            val page = dictionaryWordRepository.paginateViewsBySpelling("minute", PageRequest.of(0, 10))

            page.content[0].topDefinitions shouldHaveSize 3
            page.content[0].topDefinitions[0] shouldBe "뜻1"
            page.content[0].topDefinitions[1] shouldBe "뜻2"
            page.content[0].topDefinitions[2] shouldBe "뜻3"
        }

        it("페이징이 적용된다.") {
            val letters = "abcdefghijklmnopqrstuvwxy"
            letters.forEach { ch ->
                dictionaryWordRepository.save(
                    PublicWordFixture.createWord(spelling = "word$ch", slug = "word-$ch")
                )
            }

            val page1 = dictionaryWordRepository.paginateViewsBySpelling("word", PageRequest.of(0, 10))
            val page2 = dictionaryWordRepository.paginateViewsBySpelling("word", PageRequest.of(1, 10))

            page1.totalElements shouldBe 25
            page1.content shouldHaveSize 10
            page2.content shouldHaveSize 10
        }
    }

    describe("delete") {
        it("단어를 삭제한다.") {
            val word = dictionaryWordRepository.save(
                PublicWordFixture.createWord(spelling = "delete", slug = "delete")
            )

            dictionaryWordRepository.delete(word)

            val found = dictionaryWordRepository.findById(word.id!!)
            found shouldBe null
        }

        it("단어를 삭제하면 연관된 sense와 example도 삭제된다.") {
            val example = PublicWordFixture.createExample()
            val sense = PublicWordFixture.createSense(examples = listOf(example))
            val word = dictionaryWordRepository.save(
                PublicWordFixture.createWord(spelling = "delete", slug = "delete", senses = listOf(sense))
            )

            dictionaryWordRepository.delete(word)

            val found = dictionaryWordRepository.findById(word.id!!)
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

            val found = dictionaryWordRepository.findById(word.id!!)

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
