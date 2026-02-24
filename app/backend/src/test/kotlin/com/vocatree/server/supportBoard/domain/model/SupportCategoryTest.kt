package com.vocatree.server.supportBoard.domain.model

import com.vocatree.server.supportBoard.fixture.SupportCategoryFixture
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class SupportCategoryTest : DescribeSpec({

    describe("create") {
        it("대분류 카테고리를 생성할 수 있다") {
            val category = SupportCategory.create(
                categoryName = "대분류",
                parentCategory = null
            )

            category.categoryName shouldBe "대분류"
            category.parentCategory shouldBe null
            category.isMainCategory() shouldBe true
            category.isSubCategory() shouldBe false
        }

        it("중분류 카테고리를 생성할 수 있다") {
            val mainCategory = SupportCategoryFixture.createMainCategory(id = 1L, categoryName = "대분류")

            val subCategory = SupportCategory.create(
                categoryName = "중분류",
                parentCategory = mainCategory
            )

            subCategory.categoryName shouldBe "중분류"
            subCategory.parentCategory shouldBe mainCategory
            subCategory.isMainCategory() shouldBe false
            subCategory.isSubCategory() shouldBe true
        }
    }

    describe("init") {
        it("중분류의 부모가 대분류가 아니면 예외가 발생한다") {
            val mainCategory = SupportCategoryFixture.createMainCategory(id = 1L)
            val subCategory = SupportCategoryFixture.createSubCategory(id = 2L, parentCategory = mainCategory)

            shouldThrow<IllegalArgumentException> {
                SupportCategory(
                    id = 3L,
                    categoryName = "잘못된 중분류",
                    parentCategory = subCategory  // 중분류를 부모로 설정 (잘못됨)
                )
            }
        }
    }

    describe("update") {
        it("카테고리 이름을 변경할 수 있다") {
            val category = SupportCategoryFixture.createMainCategory(categoryName = "원래 이름")

            val updated = category.update("새로운 이름")

            updated.categoryName shouldBe "새로운 이름"
            updated.id shouldBe category.id
            updated.parentCategory shouldBe category.parentCategory
        }
    }

    describe("isMainCategory") {
        it("parentCategory가 null이면 true를 반환한다") {
            val category = SupportCategoryFixture.createMainCategory()

            category.isMainCategory() shouldBe true
        }

        it("parentCategory가 있으면 false를 반환한다") {
            val category = SupportCategoryFixture.createSubCategory()

            category.isMainCategory() shouldBe false
        }
    }

    describe("isSubCategory") {
        it("parentCategory가 있으면 true를 반환한다") {
            val category = SupportCategoryFixture.createSubCategory()

            category.isSubCategory() shouldBe true
        }

        it("parentCategory가 null이면 false를 반환한다") {
            val category = SupportCategoryFixture.createMainCategory()

            category.isSubCategory() shouldBe false
        }
    }
})
