package com.vocatree.server.dictionary.application

import com.vocatree.server.common.infrastructure.holder.TestLocalDateTimeHolder
import com.vocatree.server.dictionary.domain.exception.UserSearchHistoryNotFoundException
import com.vocatree.server.dictionary.domain.port.UserSearchHistoryRepository
import com.vocatree.server.dictionary.fixture.UserSearchHistoryFixture
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@SpringBootTest
@ApplyExtension(SpringExtension::class)
@Transactional
class UserSearchHistoryServiceTest(
    private val userSearchHistoryService: UserSearchHistoryService,
    private val userSearchHistoryRepository: UserSearchHistoryRepository,
    private val testLocalDateTimeHolder: TestLocalDateTimeHolder
) : DescribeSpec({

    afterEach {
        testLocalDateTimeHolder.reset()
    }

    describe("getMySearchHistory") {
        context("정상적인 조회 요청일 때") {
            it("최근 검색 이력을 반환한다.") {
                userSearchHistoryRepository.save(
                    UserSearchHistoryFixture.create(
                        userId = "user1",
                        spelling = "apple",
                        searchedAt = LocalDateTime.of(2025, 1, 15, 10, 0, 0)
                    )
                )
                userSearchHistoryRepository.save(
                    UserSearchHistoryFixture.create(
                        userId = "user1",
                        spelling = "banana",
                        searchedAt = LocalDateTime.of(2025, 1, 15, 11, 0, 0)
                    )
                )

                val histories = userSearchHistoryService.getMySearchHistory("user1", 10)

                histories shouldHaveSize 2
                histories[0].spelling shouldBe "banana"
                histories[1].spelling shouldBe "apple"
            }

            it("limit 개수만큼만 반환한다.") {
                repeat(5) { i ->
                    userSearchHistoryRepository.save(
                        UserSearchHistoryFixture.create(
                            userId = "user1",
                            spelling = "word$i",
                            searchedAt = LocalDateTime.of(2025, 1, 15, 10 + i, 0, 0)
                        )
                    )
                }

                val histories = userSearchHistoryService.getMySearchHistory("user1", 3)

                histories shouldHaveSize 3
            }
        }

        context("검색 이력이 없을 때") {
            it("빈 리스트를 반환한다.") {
                val histories = userSearchHistoryService.getMySearchHistory("user-no-history", 10)

                histories shouldHaveSize 0
            }
        }
    }

    describe("deleteAllMySearchHistory") {
        context("정상적인 삭제 요청일 때") {
            it("사용자의 모든 검색 이력을 삭제한다.") {
                userSearchHistoryRepository.save(
                    UserSearchHistoryFixture.create(userId = "user1", spelling = "apple")
                )
                userSearchHistoryRepository.save(
                    UserSearchHistoryFixture.create(userId = "user1", spelling = "banana")
                )

                userSearchHistoryService.deleteAllMySearchHistory("user1")

                val histories = userSearchHistoryService.getMySearchHistory("user1", 10)
                histories shouldHaveSize 0
            }

            it("다른 사용자의 검색 이력은 삭제되지 않는다.") {
                userSearchHistoryRepository.save(
                    UserSearchHistoryFixture.create(userId = "user1", spelling = "apple")
                )
                userSearchHistoryRepository.save(
                    UserSearchHistoryFixture.create(userId = "user2", spelling = "banana")
                )

                userSearchHistoryService.deleteAllMySearchHistory("user1")

                val user2Histories = userSearchHistoryService.getMySearchHistory("user2", 10)
                user2Histories shouldHaveSize 1
            }
        }
    }

    describe("deleteMySearchHistory") {
        context("정상적인 삭제 요청일 때") {
            it("특정 검색 이력을 삭제한다.") {
                val saved = userSearchHistoryRepository.save(
                    UserSearchHistoryFixture.create(userId = "user1", spelling = "apple")
                )
                userSearchHistoryRepository.save(
                    UserSearchHistoryFixture.create(userId = "user1", spelling = "banana")
                )

                userSearchHistoryService.deleteMySearchHistory("user1", saved.id!!)

                val histories = userSearchHistoryService.getMySearchHistory("user1", 10)
                histories shouldHaveSize 1
                histories[0].spelling shouldBe "banana"
            }
        }

        context("존재하지 않는 검색 이력을 삭제하려 할 때") {
            it("UserSearchHistoryNotFoundException이 발생한다.") {
                shouldThrow<UserSearchHistoryNotFoundException> {
                    userSearchHistoryService.deleteMySearchHistory("user1", 999L)
                }
            }
        }

        context("다른 사용자의 검색 이력을 삭제하려 할 때") {
            it("UserSearchHistoryNotFoundException이 발생한다.") {
                val saved = userSearchHistoryRepository.save(
                    UserSearchHistoryFixture.create(userId = "user1", spelling = "apple")
                )

                shouldThrow<UserSearchHistoryNotFoundException> {
                    userSearchHistoryService.deleteMySearchHistory("user2", saved.id!!)
                }
            }
        }
    }

    describe("recordSearch") {
        context("처음 검색하는 단어일 때") {
            it("새로운 검색 이력이 생성된다.") {
                userSearchHistoryService.recordSearch("user1", "apple")

                val histories = userSearchHistoryService.getMySearchHistory("user1", 10)
                histories shouldHaveSize 1
                histories[0].spelling shouldBe "apple"
            }
        }

        context("이미 검색한 단어를 다시 검색할 때") {
            it("searchedAt이 업데이트되고 중복 생성되지 않는다.") {
                val oldTime = LocalDateTime.of(2025, 1, 15, 10, 0, 0)
                val newTime = LocalDateTime.of(2025, 1, 15, 11, 0, 0)
                testLocalDateTimeHolder.setDateTimes(newTime)

                userSearchHistoryRepository.save(
                    UserSearchHistoryFixture.create(
                        userId = "user1",
                        spelling = "apple",
                        searchedAt = oldTime
                    )
                )

                userSearchHistoryService.recordSearch("user1", "apple")

                val histories = userSearchHistoryService.getMySearchHistory("user1", 10)
                histories shouldHaveSize 1
                histories[0].spelling shouldBe "apple"
                histories[0].searchedAt shouldBe newTime
            }
        }

        context("같은 단어를 다른 사용자가 검색할 때") {
            it("각각 별도의 검색 이력이 생성된다.") {
                userSearchHistoryService.recordSearch("user1", "apple")
                userSearchHistoryService.recordSearch("user2", "apple")

                val user1Histories = userSearchHistoryService.getMySearchHistory("user1", 10)
                val user2Histories = userSearchHistoryService.getMySearchHistory("user2", 10)

                user1Histories shouldHaveSize 1
                user2Histories shouldHaveSize 1
            }
        }
    }
})
