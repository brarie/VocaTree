package com.monovoca.server.supportBoard.application

import com.monovoca.server.common.infrastructure.holder.TestLocalDateTimeHolder
import com.monovoca.server.supportBoard.domain.exception.SupportCategoryNotFoundException
import com.monovoca.server.supportBoard.domain.exception.SupportPostNotFoundException
import com.monovoca.server.supportBoard.domain.port.SupportCategoryRepository
import com.monovoca.server.supportBoard.domain.port.SupportPostRepository
import com.monovoca.server.supportBoard.fixture.SupportCategoryFixture
import com.monovoca.server.supportBoard.fixture.SupportPostFixture
import com.monovoca.server.user.domain.port.UserCommandRepository
import com.monovoca.server.user.fixture.UserFixture
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@SpringBootTest
@ApplyExtension(SpringExtension::class)
@Transactional
class SupportPostServiceTest(
    private val supportPostService: SupportPostService,
    private val supportCategoryRepository: SupportCategoryRepository,
    private val supportPostRepository: SupportPostRepository,
    private val userCommandRepository: UserCommandRepository,
    private val testLocalDateTimeHolder: TestLocalDateTimeHolder
) : DescribeSpec({

    afterEach {
        testLocalDateTimeHolder.reset()
    }

    describe("paginateViewsByCategoryTree") {
        context("게시글이 존재할 때") {
            it("페이징된 게시글 뷰를 반환한다") {
                val user = userCommandRepository.save(
                    UserFixture.create(username = "page1", email = "page1@test.com")
                )
                val main = supportCategoryRepository.save(
                    SupportCategoryFixture.createMainCategory(categoryName = "대분류")
                )
                val sub = supportCategoryRepository.save(
                    SupportCategoryFixture.createSubCategory(categoryName = "중분류", parentCategory = main)
                )
                supportPostRepository.save(
                    SupportPostFixture.create(title = "게시글1", authorId = user.id!!, supportCategory = sub)
                )
                supportPostRepository.save(
                    SupportPostFixture.create(title = "게시글2", authorId = user.id!!, supportCategory = sub)
                )

                val pageable = PageRequest.of(0, 10)
                val result = supportPostService.paginateViewsByCategoryTree(main.id!!, pageable)

                result.content.size shouldBe 2
            }
        }

        context("게시글이 없을 때") {
            it("빈 페이지를 반환한다") {
                val main = supportCategoryRepository.save(
                    SupportCategoryFixture.createMainCategory(categoryName = "대분류")
                )

                val pageable = PageRequest.of(0, 10)
                val result = supportPostService.paginateViewsByCategoryTree(main.id!!, pageable)

                result.content.size shouldBe 0
            }
        }
    }

    describe("create") {
        context("정상적인 게시글 생성 요청일 때") {
            it("게시글이 생성되고 도메인 객체를 반환한다") {
                val user = userCommandRepository.save(
                    UserFixture.create(username = "create1", email = "create1@test.com")
                )
                val main = supportCategoryRepository.save(
                    SupportCategoryFixture.createMainCategory(categoryName = "대분류")
                )
                val sub = supportCategoryRepository.save(
                    SupportCategoryFixture.createSubCategory(categoryName = "중분류", parentCategory = main)
                )
                val now = LocalDateTime.of(2025, 1, 15, 10, 30, 0)
                testLocalDateTimeHolder.setDateTimes(now)

                val post = supportPostService.create(
                    userId = user.id!!,
                    title = "새로운 게시글",
                    content = "게시글 내용",
                    categoryId = sub.id!!
                )

                post.id shouldNotBe null
                post.title shouldBe "새로운 게시글"
                post.content shouldBe "게시글 내용"
                post.authorId shouldBe user.id
                post.supportCategory.id shouldBe sub.id
                post.createdAt shouldBe now
            }
        }

        context("존재하지 않는 카테고리로 생성 요청할 때") {
            it("SupportCategoryNotFoundException이 발생한다") {
                shouldThrow<SupportCategoryNotFoundException> {
                    supportPostService.create(
                        userId = "user-1",
                        title = "새로운 게시글",
                        content = "게시글 내용",
                        categoryId = 999L
                    )
                }
            }
        }

        context("대분류 카테고리로 생성 요청할 때") {
            it("IllegalArgumentException이 발생한다") {
                val main = supportCategoryRepository.save(
                    SupportCategoryFixture.createMainCategory(categoryName = "대분류")
                )

                shouldThrow<IllegalArgumentException> {
                    supportPostService.create(
                        userId = "user-1",
                        title = "새로운 게시글",
                        content = "게시글 내용",
                        categoryId = main.id!!
                    )
                }
            }
        }
    }

    describe("update") {
        context("정상적인 수정 요청일 때") {
            it("게시글이 수정되고 도메인 객체를 반환한다") {
                val user = userCommandRepository.save(
                    UserFixture.create(username = "update1", email = "update1@test.com")
                )
                val main = supportCategoryRepository.save(
                    SupportCategoryFixture.createMainCategory(categoryName = "대분류")
                )
                val sub1 = supportCategoryRepository.save(
                    SupportCategoryFixture.createSubCategory(categoryName = "중분류1", parentCategory = main)
                )
                val savedPost = supportPostRepository.save(
                    SupportPostFixture.create(
                        title = "원래 제목",
                        content = "원래 내용",
                        authorId = user.id!!,
                        supportCategory = sub1
                    )
                )

                val updatedPost = supportPostService.update(
                    postId = savedPost.id!!,
                    toTitle = "새로운 제목",
                    toContent = "새로운 내용",
                    toCategoryId = sub1.id!!
                )

                updatedPost.id shouldBe savedPost.id
                updatedPost.title shouldBe "새로운 제목"
                updatedPost.content shouldBe "새로운 내용"
            }
        }

        context("다른 카테고리로 이동할 때") {
            it("게시글의 카테고리가 변경된다") {
                val user = userCommandRepository.save(
                    UserFixture.create(username = "update2", email = "update2@test.com")
                )
                val main = supportCategoryRepository.save(
                    SupportCategoryFixture.createMainCategory(categoryName = "대분류")
                )
                val sub1 = supportCategoryRepository.save(
                    SupportCategoryFixture.createSubCategory(categoryName = "중분류1", parentCategory = main)
                )
                val sub2 = supportCategoryRepository.save(
                    SupportCategoryFixture.createSubCategory(categoryName = "중분류2", parentCategory = main)
                )
                val savedPost = supportPostRepository.save(
                    SupportPostFixture.create(
                        title = "게시글",
                        content = "내용",
                        authorId = user.id!!,
                        supportCategory = sub1
                    )
                )

                val updatedPost = supportPostService.update(
                    postId = savedPost.id!!,
                    toTitle = "게시글",
                    toContent = "내용",
                    toCategoryId = sub2.id!!
                )

                updatedPost.supportCategory.id shouldBe sub2.id
            }
        }

        context("존재하지 않는 게시글을 수정하려 할 때") {
            it("SupportPostNotFoundException이 발생한다") {
                val main = supportCategoryRepository.save(
                    SupportCategoryFixture.createMainCategory(categoryName = "대분류")
                )
                val sub = supportCategoryRepository.save(
                    SupportCategoryFixture.createSubCategory(categoryName = "중분류", parentCategory = main)
                )

                shouldThrow<SupportPostNotFoundException> {
                    supportPostService.update(
                        postId = 999L,
                        toTitle = "새로운 제목",
                        toContent = "새로운 내용",
                        toCategoryId = sub.id!!
                    )
                }
            }
        }

        context("존재하지 않는 카테고리로 이동하려 할 때") {
            it("SupportCategoryNotFoundException이 발생한다") {
                val user = userCommandRepository.save(
                    UserFixture.create(username = "update3", email = "update3@test.com")
                )
                val main = supportCategoryRepository.save(
                    SupportCategoryFixture.createMainCategory(categoryName = "대분류")
                )
                val sub = supportCategoryRepository.save(
                    SupportCategoryFixture.createSubCategory(categoryName = "중분류", parentCategory = main)
                )
                val post = supportPostRepository.save(
                    SupportPostFixture.create(authorId = user.id!!, supportCategory = sub)
                )

                shouldThrow<SupportCategoryNotFoundException> {
                    supportPostService.update(
                        postId = post.id!!,
                        toTitle = "제목",
                        toContent = "내용",
                        toCategoryId = 999L
                    )
                }
            }
        }

        context("대분류 카테고리로 이동하려 할 때") {
            it("IllegalArgumentException이 발생한다") {
                val user = userCommandRepository.save(
                    UserFixture.create(username = "update4", email = "update4@test.com")
                )
                val main1 = supportCategoryRepository.save(
                    SupportCategoryFixture.createMainCategory(categoryName = "대분류1")
                )
                val main2 = supportCategoryRepository.save(
                    SupportCategoryFixture.createMainCategory(categoryName = "대분류2")
                )
                val sub = supportCategoryRepository.save(
                    SupportCategoryFixture.createSubCategory(categoryName = "중분류", parentCategory = main1)
                )
                val post = supportPostRepository.save(
                    SupportPostFixture.create(authorId = user.id!!, supportCategory = sub)
                )

                shouldThrow<IllegalArgumentException> {
                    supportPostService.update(
                        postId = post.id!!,
                        toTitle = "제목",
                        toContent = "내용",
                        toCategoryId = main2.id!!
                    )
                }
            }
        }
    }

    describe("delete") {
        context("정상적인 삭제 요청일 때") {
            it("게시글이 삭제된다") {
                val user = userCommandRepository.save(
                    UserFixture.create(username = "delete1", email = "delete1@test.com")
                )
                val main = supportCategoryRepository.save(
                    SupportCategoryFixture.createMainCategory(categoryName = "대분류")
                )
                val sub = supportCategoryRepository.save(
                    SupportCategoryFixture.createSubCategory(categoryName = "중분류", parentCategory = main)
                )
                val post = supportPostRepository.save(
                    SupportPostFixture.create(authorId = user.id!!, supportCategory = sub)
                )

                supportPostService.delete(post.id!!)

                supportPostRepository.findById(post.id!!) shouldBe null
            }
        }

        context("존재하지 않는 게시글을 삭제하려 할 때") {
            it("SupportPostNotFoundException이 발생한다") {
                shouldThrow<SupportPostNotFoundException> {
                    supportPostService.delete(999L)
                }
            }
        }
    }
})
