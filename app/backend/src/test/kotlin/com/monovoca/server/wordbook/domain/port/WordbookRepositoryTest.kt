package com.monovoca.server.wordbook.domain.port

import com.monovoca.server.user.domain.port.UserRepository
import com.monovoca.server.user.fixture.UserFixture
import com.monovoca.server.wordbook.fixture.WordbookFixture
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
class WordbookRepositoryTest(
    private val wordbookRepository: WordbookRepository,
    private val userRepository: UserRepository,
) : DescribeSpec({

    describe("save") {
        it("새로운 단어장을 저장하면 ID가 할당된다.") {
            val owner = userRepository.save(
                UserFixture.create(username = "saveuser1", email = "save1@test.com")
            )
            val wordbook = WordbookFixture.create(
                title = "영어 단어장",
                ownerId = owner.id!!
            )

            val saved = wordbookRepository.save(wordbook)

            saved.id shouldNotBe null
            saved.title shouldBe "영어 단어장"
            saved.ownerId shouldBe owner.id
            saved.isStarred shouldBe false
            saved.bundleId shouldBe null
        }

        it("기존 단어장을 수정하면 변경된 내용이 반영된다.") {
            val owner = userRepository.save(
                UserFixture.create(username = "saveuser2", email = "save2@test.com")
            )
            var wordbook = wordbookRepository.save(
                WordbookFixture.create(
                    title = "원래 제목",
                    ownerId = owner.id!!
                )
            )
            wordbook = wordbook.updateTitle("수정된 제목")

            val updated = wordbookRepository.save(wordbook)

            updated.title shouldBe "수정된 제목"
        }

        it("단어장을 즐겨찾기로 변경할 수 있다.") {
            val owner = userRepository.save(
                UserFixture.create(username = "saveuser3", email = "save3@test.com")
            )
            var wordbook = wordbookRepository.save(
                WordbookFixture.create(
                    title = "즐겨찾기 테스트",
                    ownerId = owner.id!!,
                    isStarred = false
                )
            )
            wordbook = wordbook.toggleStarred()

            val updated = wordbookRepository.save(wordbook)

            updated.isStarred shouldBe true
        }
    }

    describe("findById") {
        it("ID로 단어장을 조회한다.") {
            val owner = userRepository.save(
                UserFixture.create(username = "finduser1", email = "find1@test.com")
            )
            val saved = wordbookRepository.save(
                WordbookFixture.create(
                    title = "조회 테스트",
                    ownerId = owner.id!!
                )
            )

            val found = wordbookRepository.findById(saved.id!!)

            found shouldNotBe null
            found!!.id shouldBe saved.id
            found.title shouldBe "조회 테스트"
            found.ownerId shouldBe owner.id
        }

        it("존재하지 않는 ID로 조회하면 null을 반환한다.") {
            val found = wordbookRepository.findById(999999L)

            found shouldBe null
        }
    }

    describe("delete") {
        it("단어장을 삭제한다.") {
            val owner = userRepository.save(
                UserFixture.create(username = "deluser1", email = "del1@test.com")
            )
            val wordbook = wordbookRepository.save(
                WordbookFixture.create(
                    title = "삭제할 단어장",
                    ownerId = owner.id!!
                )
            )

            wordbookRepository.delete(wordbook)

            val found = wordbookRepository.findById(wordbook.id!!)
            found shouldBe null
        }
    }
})
