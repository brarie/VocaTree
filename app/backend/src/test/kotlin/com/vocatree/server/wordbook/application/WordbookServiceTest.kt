package com.vocatree.server.wordbook.application

import com.vocatree.server.user.domain.port.UserRepository
import com.vocatree.server.user.fixture.UserFixture
import com.vocatree.server.wordbook.domain.exception.CannotDeactivateSpellingColumnException
import com.vocatree.server.wordbook.domain.exception.MissingParentDependencyColumnException
import com.vocatree.server.wordbook.domain.exception.ParentColumnDeactivationRestrictedException
import com.vocatree.server.wordbook.domain.exception.WordbookBundleForbiddenException
import com.vocatree.server.wordbook.domain.exception.WordbookBundleNotFoundException
import com.vocatree.server.wordbook.domain.exception.WordbookForbiddenException
import com.vocatree.server.wordbook.domain.exception.WordbookNotFoundException
import com.vocatree.server.wordbook.domain.model.enumerated.ColumnType
import com.vocatree.server.wordbook.domain.port.WordbookBundleRepository
import com.vocatree.server.wordbook.domain.port.WordbookRepository
import com.vocatree.server.wordbook.fixture.WordbookBundleFixture
import com.vocatree.server.wordbook.fixture.WordbookFixture
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@ApplyExtension(SpringExtension::class)
@Transactional
class WordbookServiceTest(
    private val wordbookService: WordbookService,
    private val wordbookRepository: WordbookRepository,
    private val wordbookBundleRepository: WordbookBundleRepository,
    private val userRepository: UserRepository,
) : DescribeSpec({

    describe("create") {
        context("단어장 생성 요청할 때") {
            it("새로운 단어장을 생성한다.") {
                val owner = userRepository.save(
                    UserFixture.create(username = "create1", email = "create1@test.com")
                )

                val wordbook = wordbookService.create(owner.id!!, "새 단어장")

                wordbook.id shouldNotBe null
                wordbook.title shouldBe "새 단어장"
                wordbook.ownerId shouldBe owner.id
                wordbook.isStarred shouldBe false
                wordbook.bundleId shouldBe null
            }

            it("기본 활성화된 칼럼들이 설정된다.") {
                val owner = userRepository.save(
                    UserFixture.create(username = "create2", email = "create2@test.com")
                )

                val wordbook = wordbookService.create(owner.id!!, "새 단어장")

                wordbook.columns[ColumnType.SPELLING]?.isActive shouldBe true
                wordbook.columns[ColumnType.DEFINITION]?.isActive shouldBe true
                wordbook.columns[ColumnType.PART_OF_SPEECH]?.isActive shouldBe true
                wordbook.columns[ColumnType.ENG_DEFINITION] shouldBe null
            }
        }
    }

    describe("updateTitle") {
        context("소유자가 제목 변경 요청할 때") {
            it("제목이 변경된다.") {
                val owner = userRepository.save(
                    UserFixture.create(username = "update1", email = "update1@test.com")
                )
                val wordbook = wordbookRepository.save(
                    WordbookFixture.create(
                        title = "원래 제목",
                        ownerId = owner.id!!
                    )
                )

                val updated = wordbookService.updateTitle(owner.id!!, wordbook.id!!, "새 제목")

                updated.title shouldBe "새 제목"
            }
        }

        context("소유자가 아닌 사용자가 요청할 때") {
            it("WordbookForbiddenException이 발생한다.") {
                val owner = userRepository.save(
                    UserFixture.create(username = "update2", email = "update2@test.com")
                )
                val otherUser = userRepository.save(
                    UserFixture.create(username = "update3", email = "update3@test.com")
                )
                val wordbook = wordbookRepository.save(
                    WordbookFixture.create(ownerId = owner.id!!)
                )

                shouldThrow<WordbookForbiddenException> {
                    wordbookService.updateTitle(otherUser.id!!, wordbook.id!!, "새 제목")
                }
            }
        }

        context("존재하지 않는 단어장 ID로 요청할 때") {
            it("WordbookNotFoundException이 발생한다.") {
                val owner = userRepository.save(
                    UserFixture.create(username = "update4", email = "update4@test.com")
                )

                shouldThrow<WordbookNotFoundException> {
                    wordbookService.updateTitle(owner.id!!, 999999L, "새 제목")
                }
            }
        }
    }

    describe("delete") {
        context("소유자가 삭제 요청할 때") {
            it("단어장이 삭제된다.") {
                val owner = userRepository.save(
                    UserFixture.create(username = "delete1", email = "delete1@test.com")
                )
                val wordbook = wordbookRepository.save(
                    WordbookFixture.create(ownerId = owner.id!!)
                )

                wordbookService.delete(owner.id!!, wordbook.id!!)

                val found = wordbookRepository.findById(wordbook.id!!)
                found shouldBe null
            }
        }

        context("소유자가 아닌 사용자가 요청할 때") {
            it("WordbookForbiddenException이 발생한다.") {
                val owner = userRepository.save(
                    UserFixture.create(username = "delete2", email = "delete2@test.com")
                )
                val otherUser = userRepository.save(
                    UserFixture.create(username = "delete3", email = "delete3@test.com")
                )
                val wordbook = wordbookRepository.save(
                    WordbookFixture.create(ownerId = owner.id!!)
                )

                shouldThrow<WordbookForbiddenException> {
                    wordbookService.delete(otherUser.id!!, wordbook.id!!)
                }
            }
        }
    }

    describe("toggleStarred") {
        context("즐겨찾기 토글 요청할 때") {
            it("즐겨찾기 상태가 변경된다.") {
                val owner = userRepository.save(
                    UserFixture.create(username = "star1", email = "star1@test.com")
                )
                val wordbook = wordbookRepository.save(
                    WordbookFixture.create(ownerId = owner.id!!, isStarred = false)
                )

                val toggled = wordbookService.toggleStarred(owner.id!!, wordbook.id!!)

                toggled.isStarred shouldBe true
            }
        }
    }

    describe("move") {
        context("번들로 이동 요청할 때") {
            it("단어장이 번들 내로 이동한다.") {
                val owner = userRepository.save(
                    UserFixture.create(username = "move1", email = "move1@test.com")
                )
                val bundle = wordbookBundleRepository.save(
                    WordbookBundleFixture.create(ownerId = owner.id!!)
                )
                val wordbook = wordbookRepository.save(
                    WordbookFixture.create(ownerId = owner.id!!, bundleId = null)
                )

                val moved = wordbookService.move(owner.id!!, wordbook.id!!, bundle.id!!)

                moved.bundleId shouldBe bundle.id
            }
        }

        context("번들 밖으로 이동 요청할 때") {
            it("단어장이 번들 밖으로 이동한다.") {
                val owner = userRepository.save(
                    UserFixture.create(username = "move2", email = "move2@test.com")
                )
                val bundle = wordbookBundleRepository.save(
                    WordbookBundleFixture.create(ownerId = owner.id!!)
                )
                val wordbook = wordbookRepository.save(
                    WordbookFixture.create(ownerId = owner.id!!, bundleId = bundle.id!!)
                )

                val moved = wordbookService.move(owner.id!!, wordbook.id!!, null)

                moved.bundleId shouldBe null
            }
        }

        context("존재하지 않는 번들로 이동 요청할 때") {
            it("WordbookBundleNotFoundException이 발생한다.") {
                val owner = userRepository.save(
                    UserFixture.create(username = "move3", email = "move3@test.com")
                )
                val wordbook = wordbookRepository.save(
                    WordbookFixture.create(ownerId = owner.id!!)
                )

                shouldThrow<WordbookBundleNotFoundException> {
                    wordbookService.move(owner.id!!, wordbook.id!!, 999999L)
                }
            }
        }

        context("다른 사용자의 번들로 이동 요청할 때") {
            it("WordbookBundleForbiddenException이 발생한다.") {
                val owner = userRepository.save(
                    UserFixture.create(username = "move4", email = "move4@test.com")
                )
                val otherUser = userRepository.save(
                    UserFixture.create(username = "move5", email = "move5@test.com")
                )
                val bundle = wordbookBundleRepository.save(
                    WordbookBundleFixture.create(ownerId = otherUser.id!!)
                )
                val wordbook = wordbookRepository.save(
                    WordbookFixture.create(ownerId = owner.id!!)
                )

                shouldThrow<WordbookBundleForbiddenException> {
                    wordbookService.move(owner.id!!, wordbook.id!!, bundle.id!!)
                }
            }
        }
    }

    describe("getWordbook") {
        context("소유자가 조회 요청할 때") {
            it("단어장을 반환한다.") {
                val owner = userRepository.save(
                    UserFixture.create(username = "get1", email = "get1@test.com")
                )
                val wordbook = wordbookRepository.save(
                    WordbookFixture.create(
                        title = "조회 테스트",
                        ownerId = owner.id!!
                    )
                )

                val found = wordbookService.getWordbook(owner.id!!, wordbook.id!!)

                found.id shouldBe wordbook.id
                found.title shouldBe "조회 테스트"
            }
        }

        context("소유자가 아닌 사용자가 요청할 때") {
            it("WordbookForbiddenException이 발생한다.") {
                val owner = userRepository.save(
                    UserFixture.create(username = "get2", email = "get2@test.com")
                )
                val otherUser = userRepository.save(
                    UserFixture.create(username = "get3", email = "get3@test.com")
                )
                val wordbook = wordbookRepository.save(
                    WordbookFixture.create(ownerId = owner.id!!)
                )

                shouldThrow<WordbookForbiddenException> {
                    wordbookService.getWordbook(otherUser.id!!, wordbook.id!!)
                }
            }
        }
    }

    describe("getWordbookColumns") {
        context("칼럼 정보 조회 요청할 때") {
            it("칼럼 설정을 반환한다.") {
                val owner = userRepository.save(
                    UserFixture.create(username = "getcol1", email = "getcol1@test.com")
                )
                val wordbook = wordbookRepository.save(
                    WordbookFixture.create(ownerId = owner.id!!)
                )

                val columns = wordbookService.getWordbookColumns(owner.id!!, wordbook.id!!)

                columns.size shouldBe 4
                columns[ColumnType.SPELLING]?.isActive shouldBe true
                columns[ColumnType.DEFINITION]?.isActive shouldBe true
                columns[ColumnType.PART_OF_SPEECH]?.isActive shouldBe true
                columns[ColumnType.SENTENCE]?.isActive shouldBe true
            }
        }
    }

    describe("updateColumnWidth") {
        context("칼럼 너비 변경 요청할 때") {
            it("칼럼 너비가 변경된다.") {
                val owner = userRepository.save(
                    UserFixture.create(username = "width1", email = "width1@test.com")
                )
                val wordbook = wordbookRepository.save(
                    WordbookFixture.create(ownerId = owner.id!!)
                )

                val columns = wordbookService.updateColumnWidth(
                    owner.id!!,
                    wordbook.id!!,
                    ColumnType.SPELLING,
                    300
                )

                columns[ColumnType.SPELLING]?.width shouldBe 300
            }
        }

        context("소유자가 아닌 사용자가 요청할 때") {
            it("WordbookForbiddenException이 발생한다.") {
                val owner = userRepository.save(
                    UserFixture.create(username = "width2", email = "width2@test.com")
                )
                val otherUser = userRepository.save(
                    UserFixture.create(username = "width3", email = "width3@test.com")
                )
                val wordbook = wordbookRepository.save(
                    WordbookFixture.create(ownerId = owner.id!!)
                )

                shouldThrow<WordbookForbiddenException> {
                    wordbookService.updateColumnWidth(
                        otherUser.id!!,
                        wordbook.id!!,
                        ColumnType.SPELLING,
                        300
                    )
                }
            }
        }
    }

    describe("toggleColumnActiveStatus") {
        context("일반 칼럼 활성화 토글 요청할 때") {
            it("칼럼 활성화 상태가 변경된다.") {
                val owner = userRepository.save(
                    UserFixture.create(username = "toggle1", email = "toggle1@test.com")
                )
                val wordbook = wordbookRepository.save(
                    WordbookFixture.create(ownerId = owner.id!!)
                )

                val columns = wordbookService.toggleColumnActiveStatus(
                    owner.id!!,
                    wordbook.id!!,
                    ColumnType.DEFINITION
                )

                columns[ColumnType.DEFINITION]?.isActive shouldBe false
            }
        }

        context("SPELLING 칼럼 비활성화 요청할 때") {
            it("CannotDeactivateSpellingColumnException이 발생한다.") {
                val owner = userRepository.save(
                    UserFixture.create(username = "toggle2", email = "toggle2@test.com")
                )
                val wordbook = wordbookRepository.save(
                    WordbookFixture.create(ownerId = owner.id!!)
                )

                shouldThrow<CannotDeactivateSpellingColumnException> {
                    wordbookService.toggleColumnActiveStatus(
                        owner.id!!,
                        wordbook.id!!,
                        ColumnType.SPELLING
                    )
                }
            }
        }

        context("Level 2가 전부 비활성화 상태에서 Level 3 활성화 요청할 때") {
            it("MissingParentDependencyColumnException이 발생한다.") {
                val owner = userRepository.save(
                    UserFixture.create(username = "toggle3", email = "toggle3@test.com")
                )
                var wordbook = wordbookRepository.save(
                    WordbookFixture.create(ownerId = owner.id!!)
                )

                // Level 3 먼저 비활성화
                wordbook = wordbook.toggleColumnActiveStatus(ColumnType.SENTENCE)
                // Level 2 모두 비활성화
                wordbook = wordbook.toggleColumnActiveStatus(ColumnType.DEFINITION)
                wordbook = wordbook.toggleColumnActiveStatus(ColumnType.PART_OF_SPEECH)
                wordbookRepository.save(wordbook)

                // Level 3 다시 활성화 시도
                shouldThrow<MissingParentDependencyColumnException> {
                    wordbookService.toggleColumnActiveStatus(
                        owner.id!!,
                        wordbook.id!!,
                        ColumnType.SENTENCE
                    )
                }
            }
        }

        context("Level 3가 활성화 상태에서 마지막 Level 2 비활성화 요청할 때") {
            it("ParentColumnDeactivationRestrictedException이 발생한다.") {
                val owner = userRepository.save(
                    UserFixture.create(username = "toggle4", email = "toggle4@test.com")
                )
                var wordbook = wordbookRepository.save(
                    WordbookFixture.create(ownerId = owner.id!!)
                )

                // Level 2 중 하나만 남기고 비활성화 (DEFINITION만 남김)
                wordbook = wordbook.toggleColumnActiveStatus(ColumnType.PART_OF_SPEECH)
                // SENTENCE는 이미 활성화되어 있음
                wordbookRepository.save(wordbook)

                // 마지막 Level 2 (DEFINITION) 비활성화 시도
                shouldThrow<ParentColumnDeactivationRestrictedException> {
                    wordbookService.toggleColumnActiveStatus(
                        owner.id!!,
                        wordbook.id!!,
                        ColumnType.DEFINITION
                    )
                }
            }
        }
    }
})
