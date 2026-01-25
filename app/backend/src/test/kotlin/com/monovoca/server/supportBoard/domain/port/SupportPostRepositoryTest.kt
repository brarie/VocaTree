package com.monovoca.server.supportBoard.domain.port

import com.monovoca.server.supportBoard.fixture.SupportCategoryFixture
import com.monovoca.server.supportBoard.fixture.SupportPostFixture
import com.monovoca.server.user.domain.port.UserCommandRepository
import com.monovoca.server.user.fixture.UserFixture
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@SpringBootTest
@ApplyExtension(SpringExtension::class)
@Transactional
class SupportPostRepositoryTest(
    private val supportPostRepository: SupportPostRepository,
    private val supportCategoryRepository: SupportCategoryRepository,
    private val userCommandRepository: UserCommandRepository,
) : DescribeSpec({

    describe("save") {
        it("새로운 게시글을 저장하면 ID가 할당된다.") {
            val author = userCommandRepository.save(
                UserFixture.create(username = "saveuser1", email = "save1@test.com")
            )
            val main = supportCategoryRepository.save(
                SupportCategoryFixture.createMainCategory(categoryName = "고객지원")
            )
            val subCategory = supportCategoryRepository.save(
                SupportCategoryFixture.createSubCategory(categoryName = "FAQ", parentCategory = main)
            )
            val post = SupportPostFixture.create(
                title = "테스트 제목",
                content = "테스트 내용",
                authorId = author.id!!,
                supportCategory = subCategory
            )

            val saved = supportPostRepository.save(post)

            saved.id shouldNotBe null
            saved.title shouldBe "테스트 제목"
            saved.content shouldBe "테스트 내용"
            saved.authorId shouldBe author.id
            saved.supportCategory.id shouldBe subCategory.id
        }

        it("기존 게시글을 수정하면 변경된 내용이 반영된다.") {
            val author = userCommandRepository.save(
                UserFixture.create(username = "saveuser2", email = "save2@test.com")
            )
            val main = supportCategoryRepository.save(
                SupportCategoryFixture.createMainCategory(categoryName = "고객지원")
            )
            val subCategory = supportCategoryRepository.save(
                SupportCategoryFixture.createSubCategory(categoryName = "FAQ", parentCategory = main)
            )
            var post = supportPostRepository.save(
                SupportPostFixture.create(
                    title = "원래 제목",
                    content = "원래 내용",
                    authorId = author.id!!,
                    supportCategory = subCategory
                )
            )
            post = post.update(
                toTitle = "수정된 제목",
                toContent = "수정된 내용",
                supportCategory = subCategory
            )

            val updated = supportPostRepository.save(post)

            updated.title shouldBe "수정된 제목"
            updated.content shouldBe "수정된 내용"
        }
    }

    describe("findById") {
        it("ID로 게시글을 조회한다.") {
            val author = userCommandRepository.save(
                UserFixture.create(username = "finduser1", email = "find1@test.com")
            )
            val main = supportCategoryRepository.save(
                SupportCategoryFixture.createMainCategory(categoryName = "고객지원")
            )
            val subCategory = supportCategoryRepository.save(
                SupportCategoryFixture.createSubCategory(categoryName = "FAQ", parentCategory = main)
            )
            val saved = supportPostRepository.save(
                SupportPostFixture.create(
                    title = "테스트 게시글",
                    content = "테스트 내용",
                    authorId = author.id!!,
                    supportCategory = subCategory
                )
            )

            val found = supportPostRepository.findById(saved.id!!)

            found shouldNotBe null
            found!!.title shouldBe "테스트 게시글"
            found.content shouldBe "테스트 내용"
            found.authorId shouldBe author.id
            found.supportCategory.id shouldBe subCategory.id
        }

        it("존재하지 않는 ID로 조회하면 null을 반환한다.") {
            val found = supportPostRepository.findById(999999L)

            found shouldBe null
        }
    }

    describe("paginateViewsByCategoryTree") {
        it("게시글이 여러 개 있으면 페이지네이션된 결과를 반환한다.") {
            val author = userCommandRepository.save(
                UserFixture.create(username = "pageuser1", email = "page1@test.com", nickname = "테스트작성자")
            )
            val main = supportCategoryRepository.save(
                SupportCategoryFixture.createMainCategory(categoryName = "고객지원")
            )
            val subCategory = supportCategoryRepository.save(
                SupportCategoryFixture.createSubCategory(categoryName = "FAQ", parentCategory = main)
            )
            repeat(15) { i ->
                supportPostRepository.save(
                    SupportPostFixture.create(
                        title = "게시글 $i",
                        content = "내용 $i",
                        authorId = author.id!!,
                        createdAt = LocalDateTime.of(2025, 1, 1, 12, 0, i),
                        supportCategory = subCategory
                    )
                )
            }

            val page = supportPostRepository.paginateViewsByCategoryTree(
                categoryId = subCategory.id!!,
                pageable = PageRequest.of(0, 10)
            )

            page.content shouldHaveSize 10
            page.totalElements shouldBe 15
            page.totalPages shouldBe 2
            page.hasNext() shouldBe true
            page.content.forEachIndexed { i, it ->
                it.title shouldBe "게시글 ${14 - i}"
                it.content shouldBe "내용 ${14 - i}"
                it.author.id shouldBe author.id
                it.author.nickname shouldBe "테스트작성자"
                it.supportCategoryId shouldBe subCategory.id!!
                it.supportCategoryName shouldBe "FAQ"
                it.createdAt shouldBe LocalDateTime.of(2025, 1, 1, 12, 0, 14 - i)
            }
        }

        it("두 번째 페이지를 조회할 수 있다.") {
            val author = userCommandRepository.save(
                UserFixture.create(username = "pageuser2", email = "page2@test.com")
            )
            val main = supportCategoryRepository.save(
                SupportCategoryFixture.createMainCategory(categoryName = "고객지원")
            )
            val subCategory = supportCategoryRepository.save(
                SupportCategoryFixture.createSubCategory(categoryName = "FAQ", parentCategory = main)
            )
            repeat(15) { i ->
                supportPostRepository.save(
                    SupportPostFixture.create(
                        title = "게시글 $i",
                        content = "내용 $i",
                        authorId = author.id!!,
                        createdAt = LocalDateTime.of(2025, 1, 1, 12, 0, i),
                        supportCategory = subCategory
                    )
                )
            }

            val page = supportPostRepository.paginateViewsByCategoryTree(
                categoryId = subCategory.id!!,
                pageable = PageRequest.of(1, 10)
            )

            page.content shouldHaveSize 5
            page.hasNext() shouldBe false
        }

        it("해당 카테고리에 게시글이 없으면 빈 페이지를 반환한다.") {
            val main = supportCategoryRepository.save(
                SupportCategoryFixture.createMainCategory(categoryName = "고객지원")
            )
            val subCategory = supportCategoryRepository.save(
                SupportCategoryFixture.createSubCategory(categoryName = "FAQ", parentCategory = main)
            )

            val page = supportPostRepository.paginateViewsByCategoryTree(
                categoryId = subCategory.id!!,
                pageable = PageRequest.of(0, 10)
            )

            page.content.shouldBeEmpty()
            page.totalElements shouldBe 0
        }

        it("다른 카테고리의 게시글은 조회되지 않는다.") {
            val author = userCommandRepository.save(
                UserFixture.create(username = "pageuser3", email = "page3@test.com")
            )
            val main = supportCategoryRepository.save(
                SupportCategoryFixture.createMainCategory(categoryName = "고객지원")
            )
            val subCategory = supportCategoryRepository.save(
                SupportCategoryFixture.createSubCategory(categoryName = "FAQ", parentCategory = main)
            )
            val main2 = supportCategoryRepository.save(
                SupportCategoryFixture.createMainCategory(categoryName = "공지사항")
            )
            val subCategory2 = supportCategoryRepository.save(
                SupportCategoryFixture.createSubCategory(categoryName = "일반 공지", parentCategory = main2)
            )

            supportPostRepository.save(
                SupportPostFixture.create(title = "FAQ 게시글", authorId = author.id!!, supportCategory = subCategory)
            )
            supportPostRepository.save(
                SupportPostFixture.create(title = "공지 게시글", authorId = author.id!!, supportCategory = subCategory2)
            )

            val page = supportPostRepository.paginateViewsByCategoryTree(
                categoryId = subCategory.id!!,
                pageable = PageRequest.of(0, 10)
            )

            page.content shouldHaveSize 1
            page.content[0].title shouldBe "FAQ 게시글"
        }

        it("대분류의 ID로 조회하면 하위 중분류의 게시글도 함께 조회된다.") {
            val author = userCommandRepository.save(
                UserFixture.create(username = "pageuser4", email = "page4@test.com")
            )
            val main = supportCategoryRepository.save(
                SupportCategoryFixture.createMainCategory(categoryName = "고객지원")
            )
            val subCategory = supportCategoryRepository.save(
                SupportCategoryFixture.createSubCategory(categoryName = "FAQ", parentCategory = main)
            )
            val subCategory2 = supportCategoryRepository.save(
                SupportCategoryFixture.createSubCategory(categoryName = "문의하기", parentCategory = main)
            )

            supportPostRepository.save(
                SupportPostFixture.create(title = "FAQ 게시글", authorId = author.id!!, supportCategory = subCategory)
            )
            supportPostRepository.save(
                SupportPostFixture.create(title = "문의하기 게시글", authorId = author.id!!, supportCategory = subCategory2)
            )

            val page = supportPostRepository.paginateViewsByCategoryTree(
                categoryId = main.id!!,
                pageable = PageRequest.of(0, 10)
            )

            page.content shouldHaveSize 2
            val titles = page.content.map { it.title }
            titles shouldBe listOf("문의하기 게시글", "FAQ 게시글")
        }
    }

    describe("delete") {
        it("게시글을 삭제한다.") {
            val author = userCommandRepository.save(
                UserFixture.create(username = "deleteuser", email = "delete1@test.com")
            )
            val main = supportCategoryRepository.save(
                SupportCategoryFixture.createMainCategory(categoryName = "고객지원")
            )
            val subCategory = supportCategoryRepository.save(
                SupportCategoryFixture.createSubCategory(categoryName = "FAQ", parentCategory = main)
            )
            val post = supportPostRepository.save(
                SupportPostFixture.create(
                    title = "삭제할 게시글",
                    authorId = author.id!!,
                    supportCategory = subCategory
                )
            )

            supportPostRepository.delete(post)

            val page = supportPostRepository.paginateViewsByCategoryTree(
                categoryId = subCategory.id!!,
                pageable = PageRequest.of(0, 10)
            )
            page.content.shouldBeEmpty()
        }
    }

    describe("쿼리 측정") {
        it("10명의 유저, 1개의 대분류, 10개의 중분류, 10개의 게시글로 페이지네이션 쿼리를 측정한다.") {
            // 1. 유저 10명 생성
            val authors = (1..10).map { i ->
                userCommandRepository.save(
                    UserFixture.create(username = "query$i", email = "query$i@test.com", nickname = "작성자$i")
                )
            }

            // 2. 대분류 1개 생성
            val mainCategory = supportCategoryRepository.save(
                SupportCategoryFixture.createMainCategory(categoryName = "쿼리측정 대분류")
            )

            // 3. 대분류 아래 중분류 10개 생성
            val subCategories = (1..10).map { i ->
                supportCategoryRepository.save(
                    SupportCategoryFixture.createSubCategory(
                        categoryName = "중분류$i",
                        parentCategory = mainCategory
                    )
                )
            }

            // 4. 각 중분류에 게시글 1개씩, 각각 다른 유저가 작성 (총 10개)
            subCategories.forEachIndexed { i, subCategory ->
                supportPostRepository.save(
                    SupportPostFixture.create(
                        title = "게시글${i + 1}",
                        content = "내용${i + 1}",
                        authorId = authors[i].id!!,
                        supportCategory = subCategory
                    )
                )
            }

            println("=== pagination 쿼리 측정 시작 ===")

            // 5. 대분류로 페이지네이션 조회 (하위 중분류의 게시글 10개 모두 조회됨)
            val page = supportPostRepository.paginateViewsByCategoryTree(
                categoryId = mainCategory.id!!,
                pageable = PageRequest.of(0, 10)
            )

            // 검증
            page.content shouldHaveSize 10
            page.totalElements shouldBe 10
            page.content.map { it.title } shouldBe (10 downTo 1).map { "게시글$it" }
        }
    }
})
