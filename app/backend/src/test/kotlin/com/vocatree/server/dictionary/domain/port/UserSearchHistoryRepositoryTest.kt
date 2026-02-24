package com.vocatree.server.dictionary.domain.port

import com.vocatree.server.dictionary.fixture.UserSearchHistoryFixture
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@SpringBootTest
@ApplyExtension(SpringExtension::class)
@Transactional
class UserSearchHistoryRepositoryTest(
    private val userSearchHistoryRepository: UserSearchHistoryRepository
) : DescribeSpec({

    describe("save") {
        it("새로운 검색 이력을 저장하면 ID가 할당된다.") {
            val history = UserSearchHistoryFixture.create(userId = "user1", spelling = "apple")

            val saved = userSearchHistoryRepository.save(history)

            saved.id shouldNotBe null
            saved.userId shouldBe "user1"
            saved.spelling shouldBe "apple"
        }

        it("기존 검색 이력을 수정하면 변경된 내용이 반영된다.") {
            var history = userSearchHistoryRepository.save(
                UserSearchHistoryFixture.create(userId = "user1", spelling = "test")
            )

            history = history.renewSearchedAt(LocalDateTime.of(2025, 1, 16, 10, 0, 0))
            val updated = userSearchHistoryRepository.save(history)

            updated.searchedAt shouldBe LocalDateTime.of(2025, 1, 16, 10, 0, 0)
        }
    }

    describe("findById") {
        it("ID로 검색 이력을 조회한다.") {
            val saved = userSearchHistoryRepository.save(
                UserSearchHistoryFixture.create(userId = "user1", spelling = "apple")
            )

            val found = userSearchHistoryRepository.findById(saved.id!!)

            found shouldNotBe null
            found!!.userId shouldBe "user1"
            found.spelling shouldBe "apple"
        }

        it("존재하지 않는 ID로 조회하면 null을 반환한다.") {
            val found = userSearchHistoryRepository.findById(999999L)

            found shouldBe null
        }

        it("soft delete된 이력은 조회되지 않는다.") {
            val saved = userSearchHistoryRepository.save(
                UserSearchHistoryFixture.create(userId = "user1", spelling = "test")
            )

            userSearchHistoryRepository.delete(saved)

            val found = userSearchHistoryRepository.findById(saved.id!!)
            found shouldBe null
        }
    }

    describe("findByUserIdOrderBySearchedAtDesc") {
        it("사용자 ID로 검색 이력을 조회한다.") {
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
            userSearchHistoryRepository.save(
                UserSearchHistoryFixture.create(
                    userId = "user2",
                    spelling = "cherry",
                    searchedAt = LocalDateTime.of(2025, 1, 15, 12, 0, 0)
                )
            )

            val histories = userSearchHistoryRepository.findByUserIdOrderBySearchedAtDesc("user1", 10)

            histories shouldHaveSize 2
            histories[0].spelling shouldBe "banana"
            histories[1].spelling shouldBe "apple"
        }

        it("최근 검색 순으로 정렬된다.") {
            userSearchHistoryRepository.save(
                UserSearchHistoryFixture.create(
                    userId = "user1",
                    spelling = "first",
                    searchedAt = LocalDateTime.of(2025, 1, 15, 10, 0, 0)
                )
            )
            userSearchHistoryRepository.save(
                UserSearchHistoryFixture.create(
                    userId = "user1",
                    spelling = "second",
                    searchedAt = LocalDateTime.of(2025, 1, 15, 11, 0, 0)
                )
            )
            userSearchHistoryRepository.save(
                UserSearchHistoryFixture.create(
                    userId = "user1",
                    spelling = "third",
                    searchedAt = LocalDateTime.of(2025, 1, 15, 12, 0, 0)
                )
            )

            val histories = userSearchHistoryRepository.findByUserIdOrderBySearchedAtDesc("user1", 10)

            histories[0].spelling shouldBe "third"
            histories[1].spelling shouldBe "second"
            histories[2].spelling shouldBe "first"
        }

        it("limit 개수만큼만 조회한다.") {
            repeat(5) { i ->
                userSearchHistoryRepository.save(
                    UserSearchHistoryFixture.create(
                        userId = "user1",
                        spelling = "word$i",
                        searchedAt = LocalDateTime.of(2025, 1, 15, 10 + i, 0, 0)
                    )
                )
            }

            val histories = userSearchHistoryRepository.findByUserIdOrderBySearchedAtDesc("user1", 3)

            histories shouldHaveSize 3
        }

        it("soft delete된 이력은 조회되지 않는다.") {
            val saved1 = userSearchHistoryRepository.save(
                UserSearchHistoryFixture.create(userId = "user1", spelling = "apple")
            )
            userSearchHistoryRepository.save(
                UserSearchHistoryFixture.create(userId = "user1", spelling = "banana")
            )

            userSearchHistoryRepository.delete(saved1)

            val histories = userSearchHistoryRepository.findByUserIdOrderBySearchedAtDesc("user1", 10)
            histories shouldHaveSize 1
            histories[0].spelling shouldBe "banana"
        }
    }

    describe("findByUserIdAndSpelling") {
        it("사용자 ID와 spelling으로 검색 이력을 조회한다.") {
            userSearchHistoryRepository.save(
                UserSearchHistoryFixture.create(userId = "user1", spelling = "apple")
            )

            val found = userSearchHistoryRepository.findByUserIdAndSpelling("user1", "apple")

            found shouldNotBe null
            found!!.userId shouldBe "user1"
            found.spelling shouldBe "apple"
        }

        it("존재하지 않으면 null을 반환한다.") {
            val found = userSearchHistoryRepository.findByUserIdAndSpelling("user1", "notexist")

            found shouldBe null
        }

        it("다른 사용자의 검색 이력은 조회되지 않는다.") {
            userSearchHistoryRepository.save(
                UserSearchHistoryFixture.create(userId = "user1", spelling = "apple")
            )

            val found = userSearchHistoryRepository.findByUserIdAndSpelling("user2", "apple")

            found shouldBe null
        }

        it("soft delete된 이력은 조회되지 않는다.") {
            val saved = userSearchHistoryRepository.save(
                UserSearchHistoryFixture.create(userId = "user1", spelling = "apple")
            )

            userSearchHistoryRepository.delete(saved)

            val found = userSearchHistoryRepository.findByUserIdAndSpelling("user1", "apple")
            found shouldBe null
        }
    }

    describe("delete") {
        it("검색 이력을 soft delete한다.") {
            val saved = userSearchHistoryRepository.save(
                UserSearchHistoryFixture.create(userId = "user1", spelling = "test")
            )

            userSearchHistoryRepository.delete(saved)

            val found = userSearchHistoryRepository.findById(saved.id!!)
            found shouldBe null
        }
    }

    describe("deleteAllByUserId") {
        it("사용자의 모든 검색 이력을 soft delete한다.") {
            userSearchHistoryRepository.save(
                UserSearchHistoryFixture.create(userId = "user1", spelling = "apple")
            )
            userSearchHistoryRepository.save(
                UserSearchHistoryFixture.create(userId = "user1", spelling = "banana")
            )
            userSearchHistoryRepository.save(
                UserSearchHistoryFixture.create(userId = "user2", spelling = "cherry")
            )

            userSearchHistoryRepository.deleteAllByUserId("user1")

            val user1Histories = userSearchHistoryRepository.findByUserIdOrderBySearchedAtDesc("user1", 10)
            val user2Histories = userSearchHistoryRepository.findByUserIdOrderBySearchedAtDesc("user2", 10)

            user1Histories shouldHaveSize 0
            user2Histories shouldHaveSize 1
        }
    }
})
