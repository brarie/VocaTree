package com.monovoca.server.dictionary.application

import com.monovoca.server.common.domain.model.vocabulary.vo.components.Spelling
import com.monovoca.server.common.domain.model.vocabulary.vo.components.PartOfSpeech
import com.monovoca.server.dictionary.domain.exception.PublicSenseNotFoundException
import com.monovoca.server.dictionary.domain.exception.PublicWordAlreadyExistsException
import com.monovoca.server.dictionary.domain.exception.PublicWordNotFoundException
import com.monovoca.server.dictionary.domain.model.word.command.CreateExampleCommand
import com.monovoca.server.dictionary.domain.model.word.command.CreateSenseCommand
import com.monovoca.server.dictionary.domain.model.word.command.UpdateExampleCommand
import com.monovoca.server.dictionary.domain.model.word.command.UpdateSenseCommand
import com.monovoca.server.dictionary.domain.port.DictionaryWordRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@ApplyExtension(SpringExtension::class)
@Transactional
class DictionaryWordServiceTest(
    private val dictionaryWordService: DictionaryWordService,
    private val dictionaryWordRepository: DictionaryWordRepository
) : DescribeSpec({

    describe("createWord") {
        context("정상적인 단어 생성 요청일 때") {
            it("단어가 생성되고 도메인 객체를 반환한다") {
                val word = dictionaryWordService.createWord("apple")

                word.id shouldNotBe null
                word.spelling.value shouldBe "apple"
                word.slug shouldNotBe null
            }

            it("중복되지 않는 slug는 ID 없이 생성된다") {
                val word = dictionaryWordService.createWord("well-known")

                word.slug shouldBe "well-known"
            }

            it("중복되는 slug는 ID를 포함해서 생성된다") {
                val first = dictionaryWordService.createWord("well-known")
                val second = dictionaryWordService.createWord("well known")

                first.slug shouldBe "well-known"
                second.slug shouldBe "well-known-${second.id}"
            }

            it("특수문자는 하이픈으로 변환된다") {
                val word = dictionaryWordService.createWord("don't")

                word.slug shouldBe "don-t"
            }

            it("공백이 포함된 숙어도 정상적으로 slug가 생성된다") {
                val word = dictionaryWordService.createWord("so as to")

                word.slug shouldBe "so-as-to"
            }
        }

        context("이미 존재하는 spelling으로 생성 요청할 때") {
            it("PublicWordAlreadyExistsException이 발생한다") {
                dictionaryWordService.createWord("apple")

                shouldThrow<PublicWordAlreadyExistsException> {
                    dictionaryWordService.createWord("apple")
                }
            }
        }
    }

    describe("updateWord") {
        context("정상적인 수정 요청일 때") {
            it("단어의 spelling이 수정된다") {
                val word = dictionaryWordService.createWord("test")

                val updated = dictionaryWordService.updateWord(word.id!!, "tested")

                updated.spelling.value shouldBe "tested"
            }

            it("spelling 변경 시 slug도 새로 생성된다") {
                val word = dictionaryWordService.createWord("well-known")

                val updated = dictionaryWordService.updateWord(word.id!!, "well known")

                updated.spelling.value shouldBe "well known"
                updated.slug shouldBe "well-known-${word.id}"
            }

            it("slug가 중복되지 않으면 ID 없이 생성된다") {
                val word = dictionaryWordService.createWord("test")

                val updated = dictionaryWordService.updateWord(word.id!!, "unique-word")

                updated.slug shouldBe "unique-word"
            }
        }

        context("존재하지 않는 단어를 수정하려 할 때") {
            it("PublicWordNotFoundException이 발생한다") {
                shouldThrow<PublicWordNotFoundException> {
                    dictionaryWordService.updateWord(999L, "test")
                }
            }
        }

        context("다른 단어와 중복된 spelling으로 수정하려 할 때") {
            it("PublicWordAlreadyExistsException이 발생한다") {
                dictionaryWordService.createWord("apple")
                val word = dictionaryWordService.createWord("banana")

                shouldThrow<PublicWordAlreadyExistsException> {
                    dictionaryWordService.updateWord(word.id!!, "apple")
                }
            }
        }

        context("같은 spelling으로 수정할 때") {
            it("예외가 발생하지 않고 slug는 변경되지 않는다") {
                val word = dictionaryWordService.createWord("test")
                val originalSlug = word.slug

                val updated = dictionaryWordService.updateWord(word.id!!, "test")

                updated.spelling.value shouldBe "test"
                updated.slug shouldBe originalSlug
            }
        }
    }

    describe("deleteWord") {
        context("정상적인 삭제 요청일 때") {
            it("단어가 삭제된다") {
                val word = dictionaryWordService.createWord("delete")

                dictionaryWordService.deleteWord(word.id!!)

                dictionaryWordRepository.findById(word.id) shouldBe null
            }
        }

        context("존재하지 않는 단어를 삭제하려 할 때") {
            it("PublicWordNotFoundException이 발생한다") {
                shouldThrow<PublicWordNotFoundException> {
                    dictionaryWordService.deleteWord(999L)
                }
            }
        }
    }

    describe("createSense") {
        context("정상적인 sense 생성 요청일 때") {
            it("sense가 추가되고 단어를 반환한다") {
                val word = dictionaryWordService.createWord("minute")
                val command = CreateSenseCommand(
                    definition = "분 (시간)",
                    engDefinition = "a unit of time",
                    partOfSpeech = PartOfSpeech.NOUN
                )

                val updated = dictionaryWordService.createSense(word.id!!, command)

                updated.senses shouldHaveSize 1
                updated.senses[0].sense.definition shouldBe "분 (시간)"
            }
        }

        context("존재하지 않는 단어에 sense를 추가하려 할 때") {
            it("PublicWordNotFoundException이 발생한다") {
                val command = CreateSenseCommand(definition = "뜻")

                shouldThrow<PublicWordNotFoundException> {
                    dictionaryWordService.createSense(999L, command)
                }
            }
        }
    }

    describe("updateSense") {
        context("정상적인 sense 수정 요청일 때") {
            it("sense가 수정된다") {
                val word = dictionaryWordService.createWord("test")
                val createCommand = CreateSenseCommand(definition = "원래 뜻")
                val created = dictionaryWordService.createSense(word.id!!, createCommand)
                val senseId = created.senses[0].id!!

                val updateCommand = UpdateSenseCommand(
                    definition = "수정된 뜻",
                    engDefinition = "updated",
                    britishPhonetic = null,
                    americanPhonetic = null,
                    partOfSpeech = PartOfSpeech.VERB,
                    synonyms = listOf(Spelling("synonym")),
                    antonyms = emptyList()
                )
                val updated = dictionaryWordService.updateSense(word.id, senseId, updateCommand)

                updated.senses[0].sense.definition shouldBe "수정된 뜻"
                updated.senses[0].sense.partOfSpeech shouldBe PartOfSpeech.VERB
                updated.senses[0].sense.synonyms shouldBe listOf(Spelling("synonym"))
            }
        }

        context("존재하지 않는 단어의 sense를 수정하려 할 때") {
            it("PublicWordNotFoundException이 발생한다") {
                val command = UpdateSenseCommand(
                    definition = "뜻",
                    engDefinition = null,
                    britishPhonetic = null,
                    americanPhonetic = null,
                    partOfSpeech = null,
                    synonyms = emptyList(),
                    antonyms = emptyList()
                )

                shouldThrow<PublicWordNotFoundException> {
                    dictionaryWordService.updateSense(999L, 1L, command)
                }
            }
        }

        context("존재하지 않는 sense를 수정하려 할 때") {
            it("PublicSenseNotFoundException이 발생한다") {
                val word = dictionaryWordService.createWord("test")
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
                    dictionaryWordService.updateSense(word.id!!, 999L, command)
                }
            }
        }
    }

    describe("deleteSense") {
        context("정상적인 sense 삭제 요청일 때") {
            it("sense가 삭제된다") {
                val word = dictionaryWordService.createWord("test")
                val command = CreateSenseCommand(definition = "뜻")
                val created = dictionaryWordService.createSense(word.id!!, command)
                val senseId = created.senses[0].id!!

                val updated = dictionaryWordService.deleteSense(word.id, senseId)

                updated.senses shouldHaveSize 0
            }
        }

        context("존재하지 않는 sense를 삭제하려 할 때") {
            it("PublicSenseNotFoundException이 발생한다") {
                val word = dictionaryWordService.createWord("test")

                shouldThrow<PublicSenseNotFoundException> {
                    dictionaryWordService.deleteSense(word.id!!, 999L)
                }
            }
        }
    }

    describe("reorderSenses") {
        context("정상적인 순서 변경 요청일 때") {
            it("sense들의 순서가 변경된다") {
                val word = dictionaryWordService.createWord("test")
                val command1 = CreateSenseCommand(definition = "첫 번째")
                val command2 = CreateSenseCommand(definition = "두 번째")
                var updated = dictionaryWordService.createSense(word.id!!, command1)
                updated = dictionaryWordService.createSense(word.id, command2)

                val sense1Id = updated.senses[0].id!!
                val sense2Id = updated.senses[1].id!!

                val reordered = dictionaryWordService.reorderSenses(
                    word.id,
                    listOf(sense2Id to 0, sense1Id to 1)
                )

                reordered.senses[0].id shouldBe sense2Id
                reordered.senses[1].id shouldBe sense1Id
            }
        }
    }

    describe("createExample") {
        context("정상적인 example 생성 요청일 때") {
            it("example이 추가된다") {
                val word = dictionaryWordService.createWord("test")
                val senseCommand = CreateSenseCommand(definition = "뜻")
                val created = dictionaryWordService.createSense(word.id!!, senseCommand)
                val senseId = created.senses[0].id!!

                val exampleCommand = CreateExampleCommand(
                    sentence = "Test sentence",
                    translation = "테스트 문장"
                )
                val updated = dictionaryWordService.createExample(word.id, senseId, exampleCommand)

                updated.senses[0].examples shouldHaveSize 1
                updated.senses[0].examples[0].example.sentence shouldBe "Test sentence"
            }
        }

        context("존재하지 않는 sense에 example을 추가하려 할 때") {
            it("PublicSenseNotFoundException이 발생한다") {
                val word = dictionaryWordService.createWord("test")
                val command = CreateExampleCommand(sentence = "Test")

                shouldThrow<PublicSenseNotFoundException> {
                    dictionaryWordService.createExample(word.id!!, 999L, command)
                }
            }
        }
    }

    describe("updateExample") {
        context("정상적인 example 수정 요청일 때") {
            it("example이 수정된다") {
                val word = dictionaryWordService.createWord("test")
                val senseCommand = CreateSenseCommand(definition = "뜻")
                var updated = dictionaryWordService.createSense(word.id!!, senseCommand)
                val senseId = updated.senses[0].id!!

                val createCommand = CreateExampleCommand(sentence = "Original")
                updated = dictionaryWordService.createExample(word.id, senseId, createCommand)
                val exampleId = updated.senses[0].examples[0].id!!

                val updateCommand = UpdateExampleCommand(
                    sentence = "Updated",
                    translation = "수정됨",
                    isOfficial = true,
                    sourceInfo = "source"
                )
                val result = dictionaryWordService.updateExample(word.id, senseId, exampleId, updateCommand)

                result.senses[0].examples[0].example.sentence shouldBe "Updated"
                result.senses[0].examples[0].isOfficial shouldBe true
            }
        }
    }

    describe("deleteExample") {
        context("정상적인 example 삭제 요청일 때") {
            it("example이 삭제된다") {
                val word = dictionaryWordService.createWord("test")
                val senseCommand = CreateSenseCommand(definition = "뜻")
                var updated = dictionaryWordService.createSense(word.id!!, senseCommand)
                val senseId = updated.senses[0].id!!

                val createCommand = CreateExampleCommand(sentence = "Test")
                updated = dictionaryWordService.createExample(word.id, senseId, createCommand)
                val exampleId = updated.senses[0].examples[0].id!!

                val result = dictionaryWordService.deleteExample(word.id, senseId, exampleId)

                result.senses[0].examples shouldHaveSize 0
            }
        }
    }

    describe("paginateViewsBySpelling") {
        context("정상적인 검색 요청일 때") {
            it("검색 결과를 페이징하여 반환한다") {
                dictionaryWordService.createWord("apple")
                dictionaryWordService.createWord("application")
                dictionaryWordService.createWord("apply")

                val page = dictionaryWordService.paginateViewsBySpelling("app", PageRequest.of(0, 10), null)

                page.totalElements shouldBe 3
                page.content shouldHaveSize 3
            }

            it("검색 결과에 slug가 포함된다") {
                dictionaryWordService.createWord("test")

                val page = dictionaryWordService.paginateViewsBySpelling("test", PageRequest.of(0, 10), null)

                page.content[0].slug shouldNotBe null
                page.content[0].slug shouldContain "test"
            }

            it("검색 결과에 topDefinitions가 포함된다") {
                val word = dictionaryWordService.createWord("minute")
                dictionaryWordService.createSense(word.id!!, CreateSenseCommand(definition = "분 (시간)"))
                dictionaryWordService.createSense(word.id, CreateSenseCommand(definition = "아주 작은"))

                val page = dictionaryWordService.paginateViewsBySpelling("minute", PageRequest.of(0, 10), null)

                page.content[0].topDefinitions shouldHaveSize 2
            }
        }

        context("검색 결과가 없을 때") {
            it("빈 페이지를 반환한다") {
                val page = dictionaryWordService.paginateViewsBySpelling("notexist", PageRequest.of(0, 10), null)

                page.totalElements shouldBe 0
                page.content shouldHaveSize 0
            }
        }
    }

    describe("findBySlug") {
        context("정상적인 조회 요청일 때") {
            it("slug로 단어를 조회한다") {
                val word = dictionaryWordService.createWord("don't")

                val found = dictionaryWordService.findBySlug("don-t")

                found.id shouldBe word.id
                found.spelling.value shouldBe "don't"
            }
        }

        context("존재하지 않는 slug로 조회할 때") {
            it("PublicWordNotFoundException이 발생한다") {
                shouldThrow<PublicWordNotFoundException> {
                    dictionaryWordService.findBySlug("notexist")
                }
            }
        }
    }
})
