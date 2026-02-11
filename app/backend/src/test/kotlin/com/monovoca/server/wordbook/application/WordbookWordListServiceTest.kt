package com.monovoca.server.wordbook.application

import com.monovoca.server.common.domain.model.vocabulary.vo.components.Spelling
import com.monovoca.server.user.domain.port.UserRepository
import com.monovoca.server.user.fixture.UserFixture
import com.monovoca.server.wordbook.domain.exception.WordbookForbiddenException
import com.monovoca.server.wordbook.domain.exception.WordbookNotFoundException
import com.monovoca.server.wordbook.domain.exception.WordbookOneHundredWordsLimitExceededException
import com.monovoca.server.wordbook.domain.exception.WordbookSenseNotFoundException
import com.monovoca.server.wordbook.domain.exception.WordbookWordNotFoundException
import com.monovoca.server.wordbook.domain.model.command.UpdateExampleCommand
import com.monovoca.server.wordbook.domain.model.command.UpdateSenseCommand
import com.monovoca.server.wordbook.domain.model.command.UpdateWordCommand
import com.monovoca.server.wordbook.domain.port.WordbookRepository
import com.monovoca.server.wordbook.domain.port.WordbookWordListRepository
import com.monovoca.server.wordbook.fixture.WordbookFixture
import io.kotest.assertions.throwables.shouldThrow
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
class WordbookWordListServiceTest(
    private val wordbookWordListService: WordbookWordListService,
    private val wordbookRepository: WordbookRepository,
    private val wordbookWordListRepository: WordbookWordListRepository,
    private val userRepository: UserRepository,
) : DescribeSpec({

    describe("getWordList") {
        context("소유자가 조회 요청할 때") {
            it("WordbookWordList를 반환한다.") {
                val owner = userRepository.save(
                    UserFixture.create(username = "wlist1", email = "wlist1@test.com")
                )
                val wordbook = wordbookRepository.save(
                    WordbookFixture.create(ownerId = owner.id!!)
                )

                val wordList = wordbookWordListService.getWordList(owner.id!!, wordbook.id!!)

                wordList.wordbookId shouldBe wordbook.id
            }
        }

        context("소유자가 아닌 사용자가 요청할 때") {
            it("WordbookForbiddenException이 발생한다.") {
                val owner = userRepository.save(
                    UserFixture.create(username = "wlist2", email = "wlist2@test.com")
                )
                val otherUser = userRepository.save(
                    UserFixture.create(username = "wlist3", email = "wlist3@test.com")
                )
                val wordbook = wordbookRepository.save(
                    WordbookFixture.create(ownerId = owner.id!!)
                )

                shouldThrow<WordbookForbiddenException> {
                    wordbookWordListService.getWordList(otherUser.id!!, wordbook.id!!)
                }
            }
        }

        context("존재하지 않는 단어장 ID로 요청할 때") {
            it("WordbookNotFoundException이 발생한다.") {
                val owner = userRepository.save(
                    UserFixture.create(username = "wlist4", email = "wlist4@test.com")
                )

                shouldThrow<WordbookNotFoundException> {
                    wordbookWordListService.getWordList(owner.id!!, 999999L)
                }
            }
        }
    }

    describe("createWord") {
        context("단어 추가 요청할 때") {
            it("새로운 단어가 추가된다.") {
                val owner = userRepository.save(
                    UserFixture.create(username = "wcreate1", email = "wcreate1@test.com")
                )
                val wordbook = wordbookRepository.save(
                    WordbookFixture.create(ownerId = owner.id!!)
                )

                val wordList = wordbookWordListService.createWord(owner.id!!, wordbook.id!!)

                wordList.wordList shouldHaveSize 1
                wordList.wordList[0].displayOrder shouldBe 1
            }
        }

        context("100개 단어가 이미 있을 때") {
            it("WordbookOneHundredWordsLimitExceededException이 발생한다.") {
                val owner = userRepository.save(
                    UserFixture.create(username = "wcreate2", email = "wcreate2@test.com")
                )
                val wordbook = wordbookRepository.save(
                    WordbookFixture.create(ownerId = owner.id!!)
                )

                // 100개 단어 생성
                repeat(100) {
                    wordbookWordListService.createWord(owner.id!!, wordbook.id!!)
                }

                shouldThrow<WordbookOneHundredWordsLimitExceededException> {
                    wordbookWordListService.createWord(owner.id!!, wordbook.id!!)
                }
            }
        }
    }

    describe("updateWord") {
        context("단어 수정 요청할 때") {
            it("단어가 수정된다.") {
                val owner = userRepository.save(
                    UserFixture.create(username = "wupdate1", email = "wupdate1@test.com")
                )
                val wordbook = wordbookRepository.save(
                    WordbookFixture.create(ownerId = owner.id!!)
                )
                val wordList = wordbookWordListService.createWord(owner.id!!, wordbook.id!!)
                val wordId = wordList.wordList[0].id!!

                val command = UpdateWordCommand(spelling = Spelling("apple"))
                val updated = wordbookWordListService.updateWord(
                    owner.id!!,
                    wordbook.id!!,
                    wordId,
                    command
                )

                updated.wordList[0].spelling shouldBe Spelling("apple")
            }
        }

        context("존재하지 않는 단어 ID로 요청할 때") {
            it("WordbookWordNotFoundException이 발생한다.") {
                val owner = userRepository.save(
                    UserFixture.create(username = "wupdate2", email = "wupdate2@test.com")
                )
                val wordbook = wordbookRepository.save(
                    WordbookFixture.create(ownerId = owner.id!!)
                )

                val command = UpdateWordCommand(spelling = Spelling("apple"))
                shouldThrow<WordbookWordNotFoundException> {
                    wordbookWordListService.updateWord(
                        owner.id!!,
                        wordbook.id!!,
                        999999L,
                        command
                    )
                }
            }
        }
    }

    describe("deleteWord") {
        context("단어 삭제 요청할 때") {
            it("단어가 삭제된다.") {
                val owner = userRepository.save(
                    UserFixture.create(username = "wdelete1", email = "wdelete1@test.com")
                )
                val wordbook = wordbookRepository.save(
                    WordbookFixture.create(ownerId = owner.id!!)
                )
                val wordList = wordbookWordListService.createWord(owner.id!!, wordbook.id!!)
                val wordId = wordList.wordList[0].id!!

                val updated = wordbookWordListService.deleteWord(
                    owner.id!!,
                    wordbook.id!!,
                    wordId
                )

                updated.wordList shouldHaveSize 0
            }
        }
    }

    describe("createSense") {
        context("뜻 추가 요청할 때") {
            it("새로운 뜻이 추가된다.") {
                val owner = userRepository.save(
                    UserFixture.create(username = "screate1", email = "screate1@test.com")
                )
                val wordbook = wordbookRepository.save(
                    WordbookFixture.create(ownerId = owner.id!!)
                )
                val wordList = wordbookWordListService.createWord(owner.id!!, wordbook.id!!)
                val wordId = wordList.wordList[0].id!!

                val updated = wordbookWordListService.createSense(
                    owner.id!!,
                    wordbook.id!!,
                    wordId
                )

                updated.wordList[0].senses shouldHaveSize 1
            }
        }

        context("존재하지 않는 단어 ID로 요청할 때") {
            it("WordbookWordNotFoundException이 발생한다.") {
                val owner = userRepository.save(
                    UserFixture.create(username = "screate2", email = "screate2@test.com")
                )
                val wordbook = wordbookRepository.save(
                    WordbookFixture.create(ownerId = owner.id!!)
                )

                shouldThrow<WordbookWordNotFoundException> {
                    wordbookWordListService.createSense(
                        owner.id!!,
                        wordbook.id!!,
                        999999L
                    )
                }
            }
        }
    }

    describe("updateSense") {
        context("뜻 수정 요청할 때") {
            it("뜻이 수정된다.") {
                val owner = userRepository.save(
                    UserFixture.create(username = "supdate1", email = "supdate1@test.com")
                )
                val wordbook = wordbookRepository.save(
                    WordbookFixture.create(ownerId = owner.id!!)
                )
                var wordList = wordbookWordListService.createWord(owner.id!!, wordbook.id!!)
                val wordId = wordList.wordList[0].id!!
                wordList = wordbookWordListService.createSense(owner.id!!, wordbook.id!!, wordId)
                val senseId = wordList.wordList[0].senses[0].id!!

                val command = UpdateSenseCommand(definition = "사과")
                val updated = wordbookWordListService.updateSense(
                    owner.id!!,
                    wordbook.id!!,
                    wordId,
                    senseId,
                    command
                )

                updated.wordList[0].senses[0].sense.definition shouldBe "사과"
            }
        }

        context("존재하지 않는 뜻 ID로 요청할 때") {
            it("WordbookSenseNotFoundException이 발생한다.") {
                val owner = userRepository.save(
                    UserFixture.create(username = "supdate2", email = "supdate2@test.com")
                )
                val wordbook = wordbookRepository.save(
                    WordbookFixture.create(ownerId = owner.id!!)
                )
                val wordList = wordbookWordListService.createWord(owner.id!!, wordbook.id!!)
                val wordId = wordList.wordList[0].id!!

                val command = UpdateSenseCommand(definition = "사과")
                shouldThrow<WordbookSenseNotFoundException> {
                    wordbookWordListService.updateSense(
                        owner.id!!,
                        wordbook.id!!,
                        wordId,
                        999999L,
                        command
                    )
                }
            }
        }
    }

    describe("deleteSense") {
        context("뜻 삭제 요청할 때") {
            it("뜻이 삭제된다.") {
                val owner = userRepository.save(
                    UserFixture.create(username = "sdelete1", email = "sdelete1@test.com")
                )
                val wordbook = wordbookRepository.save(
                    WordbookFixture.create(ownerId = owner.id!!)
                )
                var wordList = wordbookWordListService.createWord(owner.id!!, wordbook.id!!)
                val wordId = wordList.wordList[0].id!!
                wordList = wordbookWordListService.createSense(owner.id!!, wordbook.id!!, wordId)
                val senseId = wordList.wordList[0].senses[0].id!!

                val updated = wordbookWordListService.deleteSense(
                    owner.id!!,
                    wordbook.id!!,
                    wordId,
                    senseId
                )

                updated.wordList[0].senses shouldHaveSize 0
            }
        }
    }

    describe("createExample") {
        context("예문 추가 요청할 때") {
            it("새로운 예문이 추가된다.") {
                val owner = userRepository.save(
                    UserFixture.create(username = "ecreate1", email = "ecreate1@test.com")
                )
                val wordbook = wordbookRepository.save(
                    WordbookFixture.create(ownerId = owner.id!!)
                )
                var wordList = wordbookWordListService.createWord(owner.id!!, wordbook.id!!)
                val wordId = wordList.wordList[0].id!!
                wordList = wordbookWordListService.createSense(owner.id!!, wordbook.id!!, wordId)
                val senseId = wordList.wordList[0].senses[0].id!!

                val updated = wordbookWordListService.createExample(
                    owner.id!!,
                    wordbook.id!!,
                    wordId,
                    senseId
                )

                updated.wordList[0].senses[0].examples shouldHaveSize 1
            }
        }
    }

    describe("updateExample") {
        context("예문 수정 요청할 때") {
            it("예문이 수정된다.") {
                val owner = userRepository.save(
                    UserFixture.create(username = "eupdate1", email = "eupdate1@test.com")
                )
                val wordbook = wordbookRepository.save(
                    WordbookFixture.create(ownerId = owner.id!!)
                )
                var wordList = wordbookWordListService.createWord(owner.id!!, wordbook.id!!)
                val wordId = wordList.wordList[0].id!!
                wordList = wordbookWordListService.createSense(owner.id!!, wordbook.id!!, wordId)
                val senseId = wordList.wordList[0].senses[0].id!!
                wordList = wordbookWordListService.createExample(
                    owner.id!!,
                    wordbook.id!!,
                    wordId,
                    senseId
                )
                val exampleId = wordList.wordList[0].senses[0].examples[0].id!!

                val command = UpdateExampleCommand(
                    sentence = "I like apples.",
                    translation = "나는 사과를 좋아한다."
                )
                val updated = wordbookWordListService.updateExample(
                    owner.id!!,
                    wordbook.id!!,
                    wordId,
                    senseId,
                    exampleId,
                    command
                )

                updated.wordList[0].senses[0].examples[0].example.sentence shouldBe "I like apples."
                updated.wordList[0].senses[0].examples[0].example.translation shouldBe "나는 사과를 좋아한다."
            }
        }
    }

    describe("deleteExample") {
        context("예문 삭제 요청할 때") {
            it("예문이 삭제된다.") {
                val owner = userRepository.save(
                    UserFixture.create(username = "edelete1", email = "edelete1@test.com")
                )
                val wordbook = wordbookRepository.save(
                    WordbookFixture.create(ownerId = owner.id!!)
                )
                var wordList = wordbookWordListService.createWord(owner.id!!, wordbook.id!!)
                val wordId = wordList.wordList[0].id!!
                wordList = wordbookWordListService.createSense(owner.id!!, wordbook.id!!, wordId)
                val senseId = wordList.wordList[0].senses[0].id!!
                wordList = wordbookWordListService.createExample(
                    owner.id!!,
                    wordbook.id!!,
                    wordId,
                    senseId
                )
                val exampleId = wordList.wordList[0].senses[0].examples[0].id!!

                val updated = wordbookWordListService.deleteExample(
                    owner.id!!,
                    wordbook.id!!,
                    wordId,
                    senseId,
                    exampleId
                )

                updated.wordList[0].senses[0].examples shouldHaveSize 0
            }
        }
    }

    describe("swapWordsOrder") {
        context("단어 순서 교환 요청할 때") {
            it("두 단어의 순서가 바뀐다.") {
                val owner = userRepository.save(
                    UserFixture.create(username = "wswap1", email = "wswap1@test.com")
                )
                val wordbook = wordbookRepository.save(
                    WordbookFixture.create(ownerId = owner.id!!)
                )
                var wordList = wordbookWordListService.createWord(owner.id!!, wordbook.id!!)
                wordList = wordbookWordListService.createWord(owner.id!!, wordbook.id!!)
                val wordId1 = wordList.wordList[0].id!!
                val wordId2 = wordList.wordList[1].id!!
                val order1 = wordList.wordList[0].displayOrder
                val order2 = wordList.wordList[1].displayOrder

                val updated = wordbookWordListService.swapWordsOrder(
                    owner.id!!,
                    wordbook.id!!,
                    wordId1,
                    wordId2
                )

                val word1 = updated.wordList.find { it.id == wordId1 }
                val word2 = updated.wordList.find { it.id == wordId2 }
                word1?.displayOrder shouldBe order2
                word2?.displayOrder shouldBe order1
            }
        }

        context("존재하지 않는 단어 ID로 요청할 때") {
            it("WordbookWordNotFoundException이 발생한다.") {
                val owner = userRepository.save(
                    UserFixture.create(username = "wswap2", email = "wswap2@test.com")
                )
                val wordbook = wordbookRepository.save(
                    WordbookFixture.create(ownerId = owner.id!!)
                )
                val wordList = wordbookWordListService.createWord(owner.id!!, wordbook.id!!)
                val wordId = wordList.wordList[0].id!!

                shouldThrow<WordbookWordNotFoundException> {
                    wordbookWordListService.swapWordsOrder(
                        owner.id!!,
                        wordbook.id!!,
                        wordId,
                        999999L
                    )
                }
            }
        }
    }

    describe("swapSensesOrder") {
        context("뜻 순서 교환 요청할 때") {
            it("두 뜻의 순서가 바뀐다.") {
                val owner = userRepository.save(
                    UserFixture.create(username = "sswap1", email = "sswap1@test.com")
                )
                val wordbook = wordbookRepository.save(
                    WordbookFixture.create(ownerId = owner.id!!)
                )
                var wordList = wordbookWordListService.createWord(owner.id!!, wordbook.id!!)
                val wordId = wordList.wordList[0].id!!
                wordList = wordbookWordListService.createSense(owner.id!!, wordbook.id!!, wordId)
                wordList = wordbookWordListService.createSense(owner.id!!, wordbook.id!!, wordId)
                val senseId1 = wordList.wordList[0].senses[0].id!!
                val senseId2 = wordList.wordList[0].senses[1].id!!
                val order1 = wordList.wordList[0].senses[0].displayOrder
                val order2 = wordList.wordList[0].senses[1].displayOrder

                val updated = wordbookWordListService.swapSensesOrder(
                    owner.id!!,
                    wordbook.id!!,
                    wordId,
                    senseId1,
                    senseId2
                )

                val sense1 = updated.wordList[0].senses.find { it.id == senseId1 }
                val sense2 = updated.wordList[0].senses.find { it.id == senseId2 }
                sense1?.displayOrder shouldBe order2
                sense2?.displayOrder shouldBe order1
            }
        }
    }

    describe("swapExamplesOrder") {
        context("예문 순서 교환 요청할 때") {
            it("두 예문의 순서가 바뀐다.") {
                val owner = userRepository.save(
                    UserFixture.create(username = "eswap1", email = "eswap1@test.com")
                )
                val wordbook = wordbookRepository.save(
                    WordbookFixture.create(ownerId = owner.id!!)
                )
                var wordList = wordbookWordListService.createWord(owner.id!!, wordbook.id!!)
                val wordId = wordList.wordList[0].id!!
                wordList = wordbookWordListService.createSense(owner.id!!, wordbook.id!!, wordId)
                val senseId = wordList.wordList[0].senses[0].id!!
                wordList = wordbookWordListService.createExample(
                    owner.id!!,
                    wordbook.id!!,
                    wordId,
                    senseId
                )
                wordList = wordbookWordListService.createExample(
                    owner.id!!,
                    wordbook.id!!,
                    wordId,
                    senseId
                )
                val exampleId1 = wordList.wordList[0].senses[0].examples[0].id!!
                val exampleId2 = wordList.wordList[0].senses[0].examples[1].id!!
                val order1 = wordList.wordList[0].senses[0].examples[0].displayOrder
                val order2 = wordList.wordList[0].senses[0].examples[1].displayOrder

                val updated = wordbookWordListService.swapExamplesOrder(
                    owner.id!!,
                    wordbook.id!!,
                    wordId,
                    senseId,
                    exampleId1,
                    exampleId2
                )

                val example1 = updated.wordList[0].senses[0].examples.find { it.id == exampleId1 }
                val example2 = updated.wordList[0].senses[0].examples.find { it.id == exampleId2 }
                example1?.displayOrder shouldBe order2
                example2?.displayOrder shouldBe order1
            }
        }
    }
})
