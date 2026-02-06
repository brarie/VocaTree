package com.monovoca.server.wordbook.domain.port

import com.monovoca.server.user.domain.port.UserRepository
import com.monovoca.server.user.fixture.UserFixture
import com.monovoca.server.wordbook.fixture.WordbookBundleFixture
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@ApplyExtension(SpringExtension::class)
@Transactional
class WordbookBundleRepositoryTest(
    private val wordbookBundleRepository: WordbookBundleRepository,
    private val userRepository: UserRepository,
) : DescribeSpec({

    describe("save") {
        it("새로운 묶음을 저장하면 ID가 할당된다.") {
            val owner = userRepository.save(
                UserFixture.create(username = "saveuser1", email = "save1@test.com")
            )
            val bundle = WordbookBundleFixture.create(
                title = "영어 묶음",
                ownerId = owner.id!!
            )

            val saved = wordbookBundleRepository.save(bundle)

            saved.id shouldNotBe null
            saved.title shouldBe "영어 묶음"
            saved.ownerId shouldBe owner.id
            saved.isStarred shouldBe false
        }

        it("기존 묶음을 수정하면 변경된 내용이 반영된다.") {
            val owner = userRepository.save(
                UserFixture.create(username = "saveuser2", email = "save2@test.com")
            )
            var bundle = wordbookBundleRepository.save(
                WordbookBundleFixture.create(
                    title = "원래 제목",
                    ownerId = owner.id!!
                )
            )
            bundle = bundle.updateTitle("수정된 제목")

            val updated = wordbookBundleRepository.save(bundle)

            updated.title shouldBe "수정된 제목"
        }

        it("묶음을 즐겨찾기로 변경할 수 있다.") {
            val owner = userRepository.save(
                UserFixture.create(username = "saveuser3", email = "save3@test.com")
            )
            var bundle = wordbookBundleRepository.save(
                WordbookBundleFixture.create(
                    title = "즐겨찾기 테스트",
                    ownerId = owner.id!!,
                    isStarred = false
                )
            )
            bundle = bundle.toggleStarred()

            val updated = wordbookBundleRepository.save(bundle)

            updated.isStarred shouldBe true
        }
    }

    describe("findById") {
        it("ID로 묶음을 조회한다.") {
            val owner = userRepository.save(
                UserFixture.create(username = "finduser1", email = "find1@test.com")
            )
            val saved = wordbookBundleRepository.save(
                WordbookBundleFixture.create(
                    title = "조회 테스트",
                    ownerId = owner.id!!
                )
            )

            val found = wordbookBundleRepository.findById(saved.id!!)

            found shouldNotBe null
            found!!.id shouldBe saved.id
            found.title shouldBe "조회 테스트"
            found.ownerId shouldBe owner.id
        }

        it("존재하지 않는 ID로 조회하면 null을 반환한다.") {
            val found = wordbookBundleRepository.findById(999999L)

            found shouldBe null
        }
    }

    describe("findAllByOwnerId") {
        it("특정 사용자의 모든 묶음을 조회한다.") {
            val owner1 = userRepository.save(
                UserFixture.create(username = "owner1", email = "owner1@test.com")
            )
            val owner2 = userRepository.save(
                UserFixture.create(username = "owner2", email = "owner2@test.com")
            )
            val bundle1 = wordbookBundleRepository.save(
                WordbookBundleFixture.create(title = "묶음1", ownerId = owner1.id!!)
            )
            val bundle2 = wordbookBundleRepository.save(
                WordbookBundleFixture.create(title = "묶음2", ownerId = owner1.id)
            )
            wordbookBundleRepository.save(
                WordbookBundleFixture.create(title = "다른사람묶음", ownerId = owner2.id!!)
            )

            val bundles = wordbookBundleRepository.findAllByOwnerId(owner1.id)

            bundles shouldHaveSize 2
            bundles.map { it.title } shouldContainExactlyInAnyOrder listOf("묶음1", "묶음2")
        }

        it("묶음이 없으면 빈 리스트를 반환한다.") {
            val owner = userRepository.save(
                UserFixture.create(username = "noowner", email = "noowner@test.com")
            )

            val bundles = wordbookBundleRepository.findAllByOwnerId(owner.id!!)

            bundles.shouldBeEmpty()
        }
    }

    describe("delete") {
        it("묶음을 삭제한다.") {
            val owner = userRepository.save(
                UserFixture.create(username = "deluser1", email = "del1@test.com")
            )
            val bundle = wordbookBundleRepository.save(
                WordbookBundleFixture.create(
                    title = "삭제할 묶음",
                    ownerId = owner.id!!
                )
            )

            wordbookBundleRepository.delete(bundle)

            val found = wordbookBundleRepository.findById(bundle.id!!)
            found shouldBe null
        }
    }
})
