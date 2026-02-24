package com.vocatree.server.supportBoard.domain.port

import com.vocatree.server.supportBoard.fixture.SupportCategoryFixture
import com.vocatree.server.supportBoard.fixture.SupportPostFixture
import com.vocatree.server.user.domain.port.UserRepository
import com.vocatree.server.user.fixture.UserFixture
import io.kotest.assertions.throwables.shouldThrow
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
class SupportCategoryRepositoryTest(
    private val supportCategoryRepository: SupportCategoryRepository,
    private val supportPostRepository: SupportPostRepository,
    private val userRepository: UserRepository,
) : DescribeSpec({

    describe("save") {
        it("대분류 카테고리를 저장하면 ID가 할당된다.") {
            val mainCategory = SupportCategoryFixture.createMainCategory(
                categoryName = "공지사항"
            )

            val saved = supportCategoryRepository.save(mainCategory)

            saved.id shouldNotBe null
            saved.categoryName shouldBe "공지사항"
            saved.isMainCategory() shouldBe true
        }

        it("중분류 카테고리를 저장하면 부모 카테고리와 함께 저장된다.") {
            val mainCategory = supportCategoryRepository.save(
                SupportCategoryFixture.createMainCategory(categoryName = "고객지원")
            )
            val subCategory = SupportCategoryFixture.createSubCategory(
                categoryName = "FAQ",
                parentCategory = mainCategory
            )

            val saved = supportCategoryRepository.save(subCategory)

            saved.id shouldNotBe null
            saved.categoryName shouldBe "FAQ"
            saved.isSubCategory() shouldBe true
            saved.parentCategory?.id shouldBe mainCategory.id
        }

        it("기존 카테고리를 수정하면 변경된 내용이 반영된다.") {
            var mainCategory = supportCategoryRepository.save(
                SupportCategoryFixture.createMainCategory(categoryName = "기존이름")
            )
            mainCategory = mainCategory.update(toCategoryName = "수정된이름")

            val updated = supportCategoryRepository.save(mainCategory)

            updated.categoryName shouldBe "수정된이름"
        }
    }

    describe("findById") {
        it("ID로 대분류를 조회한다.") {
            val saved = supportCategoryRepository.save(
                SupportCategoryFixture.createMainCategory(categoryName = "고객지원")
            )

            val found = supportCategoryRepository.findById(saved.id!!)

            found shouldNotBe null
            found!!.categoryName shouldBe "고객지원"
        }

        it("ID로 중분류를 조회한다.") {
            val main = supportCategoryRepository.save(
                SupportCategoryFixture.createMainCategory(categoryName = "고객지원")
            )
            val saved = supportCategoryRepository.save(
                SupportCategoryFixture.createSubCategory(
                    categoryName = "FAQ",
                    parentCategory = main
                )
            )

            val found = supportCategoryRepository.findById(saved.id!!)

            found shouldNotBe null
            found!!.categoryName shouldBe "FAQ"
            found.parentCategory?.id shouldBe main.id
        }

        it("존재하지 않는 ID로 조회하면 null을 반환한다.") {
            val found = supportCategoryRepository.findById(999999L)

            found shouldBe null
        }
    }

    describe("findMainCategories") {
        it("모든 대분류 카테고리를 조회한다.") {
            val main1 = supportCategoryRepository.save(
                SupportCategoryFixture.createMainCategory(categoryName = "공지사항")
            )
            val main2 = supportCategoryRepository.save(
                SupportCategoryFixture.createMainCategory(categoryName = "고객지원")
            )
            supportCategoryRepository.save(
                SupportCategoryFixture.createSubCategory(
                    categoryName = "FAQ",
                    parentCategory = main1
                )
            )

            val mainCategories = supportCategoryRepository.findMainCategories()

            mainCategories shouldHaveSize 2
            mainCategories.map { it.categoryName } shouldContainExactlyInAnyOrder listOf("공지사항", "고객지원")
        }

        it("대분류 카테고리가 없으면 빈 리스트를 반환한다.") {
            val mainCategories = supportCategoryRepository.findMainCategories()

            mainCategories.shouldBeEmpty()
        }
    }

    describe("findSubCategoriesById") {
        it("특정 대분류 카테고리의 중분류 목록을 조회한다.") {
            val main1 = supportCategoryRepository.save(
                SupportCategoryFixture.createMainCategory(categoryName = "고객지원")
            )
            val main2 = supportCategoryRepository.save(
                SupportCategoryFixture.createMainCategory(categoryName = "공지사항")
            )
            supportCategoryRepository.save(
                SupportCategoryFixture.createSubCategory(categoryName = "FAQ", parentCategory = main1)
            )
            supportCategoryRepository.save(
                SupportCategoryFixture.createSubCategory(categoryName = "문의하기", parentCategory = main1)
            )
            supportCategoryRepository.save(
                SupportCategoryFixture.createSubCategory(categoryName = "공지", parentCategory = main2)
            )

            val subCategories = supportCategoryRepository.findSubCategoriesById(main1.id!!)

            subCategories shouldHaveSize 2
            subCategories.map { it.categoryName } shouldContainExactlyInAnyOrder listOf("FAQ", "문의하기")
        }

        it("중분류가 없으면 빈 리스트를 반환한다.") {
            val main = supportCategoryRepository.save(
                SupportCategoryFixture.createMainCategory(categoryName = "빈 카테고리")
            )

            val subCategories = supportCategoryRepository.findSubCategoriesById(main.id!!)

            subCategories.shouldBeEmpty()
        }
    }

    describe("hasSubCategories") {
        it("중분류가 있으면 true를 반환한다.") {
            val main = supportCategoryRepository.save(
                SupportCategoryFixture.createMainCategory(categoryName = "고객지원")
            )
            supportCategoryRepository.save(
                SupportCategoryFixture.createSubCategory(categoryName = "FAQ", parentCategory = main)
            )

            supportCategoryRepository.hasSubCategories(main.id!!) shouldBe true
        }

        it("중분류가 없으면 false를 반환한다.") {
            val main = supportCategoryRepository.save(
                SupportCategoryFixture.createMainCategory(categoryName = "빈 카테고리")
            )

            supportCategoryRepository.hasSubCategories(main.id!!) shouldBe false
        }
    }

    describe("hasRelatedSupportPosts") {
        it("연관된 게시글이 있으면 true를 반환한다.") {
            val user = userRepository.save(
                UserFixture.create(username = "testuser1", email = "test1@example.com")
            )
            val main = supportCategoryRepository.save(
                SupportCategoryFixture.createMainCategory(categoryName = "고객지원")
            )
            val sub = supportCategoryRepository.save(
                SupportCategoryFixture.createSubCategory(categoryName = "FAQ", parentCategory = main)
            )
            supportPostRepository.save(
                SupportPostFixture.create(
                    title = "테스트 게시글",
                    authorId = user.id!!,
                    supportCategory = sub
                )
            )

            supportCategoryRepository.hasRelatedSupportPosts(sub.id!!) shouldBe true
        }

        it("연관된 게시글이 없으면 false를 반환한다.") {
            val main = supportCategoryRepository.save(
                SupportCategoryFixture.createMainCategory(categoryName = "고객지원")
            )
            val sub = supportCategoryRepository.save(
                SupportCategoryFixture.createSubCategory(categoryName = "FAQ", parentCategory = main)
            )

            supportCategoryRepository.hasRelatedSupportPosts(sub.id!!) shouldBe false
        }
    }

    describe("delete") {
        it("카테고리를 삭제한다.") {
            val main = supportCategoryRepository.save(
                SupportCategoryFixture.createMainCategory(categoryName = "삭제할 카테고리")
            )

            supportCategoryRepository.delete(main)

            val mainCategories = supportCategoryRepository.findMainCategories()
            mainCategories.shouldBeEmpty()
        }

        it("하위 카테고리가 있으면 Exception이 발생한다.") {
            val main = supportCategoryRepository.save(
                SupportCategoryFixture.createMainCategory(categoryName = "대분류")
            )
            supportCategoryRepository.save(
                SupportCategoryFixture.createSubCategory(categoryName = "중분류", parentCategory = main)
            )

            shouldThrow<Exception> {
                supportCategoryRepository.delete(main)
            }
        }

        it("관련된 게시글이 있으면 Exception이 발생한다.") {
            val user = userRepository.save(
                UserFixture.create(username = "testuser2", email = "test2@example.com")
            )
            val main = supportCategoryRepository.save(
                SupportCategoryFixture.createMainCategory(categoryName = "대분류")
            )
            val sub = supportCategoryRepository.save(
                SupportCategoryFixture.createSubCategory(categoryName = "중분류", parentCategory = main)
            )
            supportPostRepository.save(
                SupportPostFixture.create(
                    title = "게시글",
                    authorId = user.id!!,
                    supportCategory = sub
                )
            )

            shouldThrow<Exception> {
                supportCategoryRepository.delete(sub)
            }
        }
    }
})
