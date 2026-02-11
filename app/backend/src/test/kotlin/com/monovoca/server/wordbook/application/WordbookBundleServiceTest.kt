package com.monovoca.server.wordbook.application

import com.monovoca.server.user.domain.port.UserRepository
import com.monovoca.server.user.fixture.UserFixture
import com.monovoca.server.wordbook.domain.exception.CannotDeleteWordbookBundleWithWordbooks
import com.monovoca.server.wordbook.domain.exception.WordbookBundleForbiddenException
import com.monovoca.server.wordbook.domain.exception.WordbookBundleNotFoundException
import com.monovoca.server.wordbook.domain.port.WordbookBundleRepository
import com.monovoca.server.wordbook.domain.port.WordbookRepository
import com.monovoca.server.wordbook.fixture.WordbookBundleFixture
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
class WordbookBundleServiceTest(
    private val wordbookBundleService: WordbookBundleService,
    private val wordbookBundleRepository: WordbookBundleRepository,
    private val wordbookRepository: WordbookRepository,
    private val userRepository: UserRepository,
) : DescribeSpec({

    describe("create") {
        context("번들 생성 요청할 때") {
            it("새로운 번들을 생성한다.") {
                val owner = userRepository.save(
                    UserFixture.create(username = "bundle1", email = "bundle1@test.com")
                )

                val bundle = wordbookBundleService.create(owner.id!!, "새 번들")

                bundle.id shouldNotBe null
                bundle.title shouldBe "새 번들"
                bundle.ownerId shouldBe owner.id
                bundle.isStarred shouldBe false
            }
        }
    }

    describe("updateTitle") {
        context("소유자가 제목 변경 요청할 때") {
            it("제목이 변경된다.") {
                val owner = userRepository.save(
                    UserFixture.create(username = "bundle2", email = "bundle2@test.com")
                )
                val bundle = wordbookBundleRepository.save(
                    WordbookBundleFixture.create(
                        title = "원래 제목",
                        ownerId = owner.id!!
                    )
                )

                val updated = wordbookBundleService.updateTitle(owner.id!!, bundle.id!!, "새 제목")

                updated.title shouldBe "새 제목"
            }
        }

        context("소유자가 아닌 사용자가 요청할 때") {
            it("WordbookBundleForbiddenException이 발생한다.") {
                val owner = userRepository.save(
                    UserFixture.create(username = "bundle3", email = "bundle3@test.com")
                )
                val otherUser = userRepository.save(
                    UserFixture.create(username = "bundle4", email = "bundle4@test.com")
                )
                val bundle = wordbookBundleRepository.save(
                    WordbookBundleFixture.create(ownerId = owner.id!!)
                )

                shouldThrow<WordbookBundleForbiddenException> {
                    wordbookBundleService.updateTitle(otherUser.id!!, bundle.id!!, "새 제목")
                }
            }
        }

        context("존재하지 않는 번들 ID로 요청할 때") {
            it("WordbookBundleNotFoundException이 발생한다.") {
                val owner = userRepository.save(
                    UserFixture.create(username = "bundle5", email = "bundle5@test.com")
                )

                shouldThrow<WordbookBundleNotFoundException> {
                    wordbookBundleService.updateTitle(owner.id!!, 999999L, "새 제목")
                }
            }
        }
    }

    describe("delete") {
        context("번들 내 단어장이 없을 때") {
            it("번들이 삭제된다.") {
                val owner = userRepository.save(
                    UserFixture.create(username = "bundle6", email = "bundle6@test.com")
                )
                val bundle = wordbookBundleRepository.save(
                    WordbookBundleFixture.create(ownerId = owner.id!!)
                )

                wordbookBundleService.delete(owner.id!!, bundle.id!!)

                val found = wordbookBundleRepository.findById(bundle.id!!)
                found shouldBe null
            }
        }

        context("번들 내 단어장이 있을 때") {
            it("CannotDeleteWordbookBundleWithWordbooks이 발생한다.") {
                val owner = userRepository.save(
                    UserFixture.create(username = "bundle7", email = "bundle7@test.com")
                )
                val bundle = wordbookBundleRepository.save(
                    WordbookBundleFixture.create(ownerId = owner.id!!)
                )
                wordbookRepository.save(
                    WordbookFixture.create(
                        ownerId = owner.id!!,
                        bundleId = bundle.id!!
                    )
                )

                shouldThrow<CannotDeleteWordbookBundleWithWordbooks> {
                    wordbookBundleService.delete(owner.id!!, bundle.id!!)
                }
            }
        }

        context("소유자가 아닌 사용자가 요청할 때") {
            it("WordbookBundleForbiddenException이 발생한다.") {
                val owner = userRepository.save(
                    UserFixture.create(username = "bundle8", email = "bundle8@test.com")
                )
                val otherUser = userRepository.save(
                    UserFixture.create(username = "bundle9", email = "bundle9@test.com")
                )
                val bundle = wordbookBundleRepository.save(
                    WordbookBundleFixture.create(ownerId = owner.id!!)
                )

                shouldThrow<WordbookBundleForbiddenException> {
                    wordbookBundleService.delete(otherUser.id!!, bundle.id!!)
                }
            }
        }
    }

    describe("toggleStarred") {
        context("즐겨찾기 토글 요청할 때") {
            it("즐겨찾기 상태가 변경된다.") {
                val owner = userRepository.save(
                    UserFixture.create(username = "bstar1", email = "bstar1@test.com")
                )
                val bundle = wordbookBundleRepository.save(
                    WordbookBundleFixture.create(ownerId = owner.id!!, isStarred = false)
                )

                val toggled = wordbookBundleService.toggleStarred(owner.id!!, bundle.id!!)

                toggled.isStarred shouldBe true
            }
        }
    }

    describe("getBundle") {
        context("소유자가 조회 요청할 때") {
            it("번들을 반환한다.") {
                val owner = userRepository.save(
                    UserFixture.create(username = "bget1", email = "bget1@test.com")
                )
                val bundle = wordbookBundleRepository.save(
                    WordbookBundleFixture.create(
                        title = "조회 테스트 번들",
                        ownerId = owner.id!!
                    )
                )

                val found = wordbookBundleService.getBundle(owner.id!!, bundle.id!!)

                found.id shouldBe bundle.id
                found.title shouldBe "조회 테스트 번들"
            }
        }

        context("소유자가 아닌 사용자가 요청할 때") {
            it("WordbookBundleForbiddenException이 발생한다.") {
                val owner = userRepository.save(
                    UserFixture.create(username = "bget2", email = "bget2@test.com")
                )
                val otherUser = userRepository.save(
                    UserFixture.create(username = "bget3", email = "bget3@test.com")
                )
                val bundle = wordbookBundleRepository.save(
                    WordbookBundleFixture.create(ownerId = owner.id!!)
                )

                shouldThrow<WordbookBundleForbiddenException> {
                    wordbookBundleService.getBundle(otherUser.id!!, bundle.id!!)
                }
            }
        }
    }

    describe("getAllBundles") {
        context("사용자의 모든 번들 조회 요청할 때") {
            it("사용자의 모든 번들을 반환한다.") {
                val owner = userRepository.save(
                    UserFixture.create(username = "bgetall1", email = "bgetall1@test.com")
                )
                wordbookBundleRepository.save(
                    WordbookBundleFixture.create(title = "번들1", ownerId = owner.id!!)
                )
                wordbookBundleRepository.save(
                    WordbookBundleFixture.create(title = "번들2", ownerId = owner.id!!)
                )

                val bundles = wordbookBundleService.getAllBundles(owner.id!!)

                bundles shouldHaveSize 2
            }
        }

        context("다른 사용자의 번들은 조회되지 않을 때") {
            it("본인의 번들만 반환한다.") {
                val owner = userRepository.save(
                    UserFixture.create(username = "bgetall2", email = "bgetall2@test.com")
                )
                val otherUser = userRepository.save(
                    UserFixture.create(username = "bgetall3", email = "bgetall3@test.com")
                )
                wordbookBundleRepository.save(
                    WordbookBundleFixture.create(title = "내 번들", ownerId = owner.id!!)
                )
                wordbookBundleRepository.save(
                    WordbookBundleFixture.create(title = "다른 사람 번들", ownerId = otherUser.id!!)
                )

                val bundles = wordbookBundleService.getAllBundles(owner.id!!)

                bundles shouldHaveSize 1
                bundles[0].title shouldBe "내 번들"
            }
        }
    }
})
