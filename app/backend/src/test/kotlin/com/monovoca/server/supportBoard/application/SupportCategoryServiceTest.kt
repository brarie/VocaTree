package com.monovoca.server.supportBoard.application

import com.monovoca.server.supportBoard.domain.exception.CannotDeleteCategoryWithPostsException
import com.monovoca.server.supportBoard.domain.exception.CannotDeleteCategoryWithSubCategoriesException
import com.monovoca.server.supportBoard.domain.exception.SupportCategoryNotFoundException
import com.monovoca.server.supportBoard.domain.port.SupportCategoryRepository
import com.monovoca.server.supportBoard.domain.port.SupportPostRepository
import com.monovoca.server.supportBoard.fixture.SupportCategoryFixture
import com.monovoca.server.supportBoard.fixture.SupportPostFixture
import com.monovoca.server.user.domain.port.UserRepository
import com.monovoca.server.user.fixture.UserFixture
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@ApplyExtension(SpringExtension::class)
@Transactional
class SupportCategoryServiceTest(
    private val supportCategoryService: SupportCategoryService,
    private val supportCategoryRepository: SupportCategoryRepository,
    private val supportPostRepository: SupportPostRepository,
    private val userRepository: UserRepository
) : DescribeSpec({

    describe("findMainCategories") {
        context("대분류 카테고리가 존재할 때") {
            it("대분류 카테고리 목록을 반환한다") {
                val main1 = SupportCategoryFixture.createMainCategory(categoryName = "대분류1")
                val main2 = SupportCategoryFixture.createMainCategory(categoryName = "대분류2")
                supportCategoryRepository.save(main1)
                supportCategoryRepository.save(main2)

                val mainCategories = supportCategoryService.findMainCategories()

                mainCategories shouldHaveSize 2
                mainCategories.map { it.categoryName } shouldContain "대분류1"
                mainCategories.map { it.categoryName } shouldContain "대분류2"
            }
        }

        context("대분류 카테고리가 없을 때") {
            it("빈 목록을 반환한다") {
                val mainCategories = supportCategoryService.findMainCategories()

                mainCategories shouldHaveSize 0
            }
        }
    }

    describe("findSubCategoriesById") {
        context("중분류 카테고리가 존재할 때") {
            it("해당 대분류의 중분류 목록을 반환한다") {
                val main = supportCategoryRepository.save(
                    SupportCategoryFixture.createMainCategory(categoryName = "대분류")
                )
                val sub1 = supportCategoryRepository.save(
                    SupportCategoryFixture.createSubCategory(categoryName = "중분류1", parentCategory = main)
                )
                val sub2 = supportCategoryRepository.save(
                    SupportCategoryFixture.createSubCategory(categoryName = "중분류2", parentCategory = main)
                )

                val subCategories = supportCategoryService.findSubCategoriesById(main.id!!)

                subCategories shouldHaveSize 2
                subCategories.map { it.categoryName } shouldContain "중분류1"
                subCategories.map { it.categoryName } shouldContain "중분류2"
            }
        }

        context("중분류 카테고리가 없을 때") {
            it("빈 목록을 반환한다") {
                val main = supportCategoryRepository.save(
                    SupportCategoryFixture.createMainCategory(categoryName = "대분류")
                )

                val subCategories = supportCategoryService.findSubCategoriesById(main.id!!)

                subCategories shouldHaveSize 0
            }
        }
    }

    describe("createMainCategory") {
        context("정상적인 대분류 생성 요청일 때") {
            it("대분류 카테고리가 생성되고 ID가 할당된다") {
                val category = supportCategoryService.createMainCategory("새로운 대분류")

                category.id shouldNotBe null
                category.categoryName shouldBe "새로운 대분류"
                category.isMainCategory() shouldBe true
            }
        }
    }

    describe("createSubCategory") {
        context("정상적인 중분류 생성 요청일 때") {
            it("중분류 카테고리가 생성되고 ID가 할당된다") {
                val main = supportCategoryRepository.save(
                    SupportCategoryFixture.createMainCategory(categoryName = "대분류")
                )

                val subCategory = supportCategoryService.createSubCategory("새로운 중분류", main.id!!)

                subCategory.id shouldNotBe null
                subCategory.categoryName shouldBe "새로운 중분류"
                subCategory.isSubCategory() shouldBe true
                subCategory.parentCategory?.id shouldBe main.id
            }
        }

        context("존재하지 않는 부모 카테고리로 생성 요청할 때") {
            it("SupportCategoryNotFoundException이 발생한다") {
                shouldThrow<SupportCategoryNotFoundException> {
                    supportCategoryService.createSubCategory("새로운 중분류", 999L)
                }
            }
        }
    }

    describe("update") {
        context("정상적인 수정 요청일 때") {
            it("카테고리 이름이 변경된다") {
                val category = supportCategoryRepository.save(
                    SupportCategoryFixture.createMainCategory(categoryName = "원래 이름")
                )

                val updated = supportCategoryService.update(category.id!!, "새로운 이름")

                updated.categoryName shouldBe "새로운 이름"
                updated.id shouldBe category.id
            }
        }

        context("존재하지 않는 카테고리를 수정하려 할 때") {
            it("SupportCategoryNotFoundException이 발생한다") {
                shouldThrow<SupportCategoryNotFoundException> {
                    supportCategoryService.update(999L, "새로운 이름")
                }
            }
        }
    }

    describe("delete") {
        context("삭제 가능한 카테고리일 때") {
            it("카테고리가 삭제된다") {
                val category = supportCategoryRepository.save(
                    SupportCategoryFixture.createMainCategory(categoryName = "삭제할 카테고리")
                )

                supportCategoryService.delete(category.id!!)

                supportCategoryRepository.findById(category.id!!) shouldBe null
            }
        }

        context("존재하지 않는 카테고리를 삭제하려 할 때") {
            it("SupportCategoryNotFoundException이 발생한다") {
                shouldThrow<SupportCategoryNotFoundException> {
                    supportCategoryService.delete(999L)
                }
            }
        }

        context("하위 카테고리가 있는 카테고리를 삭제하려 할 때") {
            it("CannotDeleteCategoryWithSubCategoriesException이 발생한다") {
                val main = supportCategoryRepository.save(
                    SupportCategoryFixture.createMainCategory(categoryName = "대분류")
                )
                supportCategoryRepository.save(
                    SupportCategoryFixture.createSubCategory(categoryName = "중분류", parentCategory = main)
                )

                shouldThrow<CannotDeleteCategoryWithSubCategoriesException> {
                    supportCategoryService.delete(main.id!!)
                }
            }
        }

        context("관련 게시글이 있는 카테고리를 삭제하려 할 때") {
            it("CannotDeleteCategoryWithPostsException이 발생한다") {
                val user = userRepository.save(
                    UserFixture.create(username = "testuser", email = "test@example.com")
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

                shouldThrow<CannotDeleteCategoryWithPostsException> {
                    supportCategoryService.delete(sub.id!!)
                }
            }
        }
    }
})
