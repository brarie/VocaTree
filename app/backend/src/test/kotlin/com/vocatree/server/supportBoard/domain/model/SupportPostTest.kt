package com.vocatree.server.supportBoard.domain.model

import com.vocatree.server.common.infrastructure.holder.TestLocalDateTimeHolder
import com.vocatree.server.supportBoard.fixture.SupportCategoryFixture
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDateTime

class SupportPostTest : DescribeSpec({

    val localDateTimeHolder = TestLocalDateTimeHolder()

    afterEach {
        localDateTimeHolder.reset()
    }

    describe("create") {
        it("중분류 카테고리로 게시글을 생성할 수 있다") {
            val subCategory = SupportCategoryFixture.createSubCategory(id = 1L)
            val now = LocalDateTime.of(2025, 1, 15, 10, 30, 0)
            localDateTimeHolder.setDateTimes(now)

            val post = SupportPost.create(
                title = "제목",
                content = "내용",
                authorId = "author-1",
                supportCategory = subCategory,
                localDateTimeHolder = localDateTimeHolder
            )

            post.title shouldBe "제목"
            post.content shouldBe "내용"
            post.authorId shouldBe "author-1"
            post.supportCategory shouldBe subCategory
            post.createdAt shouldBe now
        }
    }

    describe("init") {
        it("대분류 카테고리로 게시글을 생성하면 예외가 발생한다") {
            val mainCategory = SupportCategoryFixture.createMainCategory(id = 1L)

            shouldThrow<IllegalArgumentException> {
                SupportPost(
                    id = 1L,
                    title = "제목",
                    content = "내용",
                    authorId = "author-1",
                    supportCategory = mainCategory,  // 대분류 (잘못됨)
                    createdAt = LocalDateTime.now()
                )
            }
        }
    }

    describe("update") {
        it("제목과 내용을 변경할 수 있다") {
            val subCategory1 = SupportCategoryFixture.createSubCategory(id = 1L, categoryName = "원래 카테고리")
            val post = SupportPost(
                id = 1L,
                title = "원래 제목",
                content = "원래 내용",
                authorId = "author-1",
                supportCategory = subCategory1,
                createdAt = LocalDateTime.of(2025, 1, 1, 12, 0, 0)
            )

            val updated = post.update(
                toTitle = "새로운 제목",
                toContent = "새로운 내용",
                supportCategory = subCategory1
            )

            updated.title shouldBe "새로운 제목"
            updated.content shouldBe "새로운 내용"
            updated.id shouldBe post.id
            updated.authorId shouldBe post.authorId
            updated.createdAt shouldBe post.createdAt
        }

        it("카테고리를 변경할 수 있다") {
            val subCategory1 = SupportCategoryFixture.createSubCategory(id = 1L, categoryName = "원래 카테고리")
            val subCategory2 = SupportCategoryFixture.createSubCategory(id = 2L, categoryName = "새로운 카테고리")
            val post = SupportPost(
                id = 1L,
                title = "제목",
                content = "내용",
                authorId = "author-1",
                supportCategory = subCategory1,
                createdAt = LocalDateTime.of(2025, 1, 1, 12, 0, 0)
            )

            val updated = post.update(
                toTitle = "제목",
                toContent = "내용",
                supportCategory = subCategory2
            )

            updated.supportCategory shouldBe subCategory2
        }

        it("대분류 카테고리로 변경하면 예외가 발생한다") {
            val subCategory = SupportCategoryFixture.createSubCategory(id = 1L)
            val mainCategory = SupportCategoryFixture.createMainCategory(id = 2L)
            val post = SupportPost(
                id = 1L,
                title = "제목",
                content = "내용",
                authorId = "author-1",
                supportCategory = subCategory,
                createdAt = LocalDateTime.of(2025, 1, 1, 12, 0, 0)
            )

            shouldThrow<IllegalArgumentException> {
                post.update(
                    toTitle = "제목",
                    toContent = "내용",
                    supportCategory = mainCategory  // 대분류로 변경 (잘못됨)
                )
            }
        }
    }
})
