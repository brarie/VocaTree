package com.vocatree.server.wordbook.domain.port

import com.vocatree.server.user.domain.port.UserRepository
import com.vocatree.server.user.fixture.UserFixture
import com.vocatree.server.wordbook.domain.model.enumerated.NodeType
import com.vocatree.server.wordbook.domain.port.options.WordbookNodeSortType
import com.vocatree.server.wordbook.fixture.WordbookBundleFixture
import com.vocatree.server.wordbook.fixture.WordbookFixture
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@SpringBootTest
@ApplyExtension(SpringExtension::class)
@Transactional
class WordbookNodeRepositoryTest(
    private val wordbookNodeRepository: WordbookNodeRepository,
    private val wordbookRepository: WordbookRepository,
    private val wordbookBundleRepository: WordbookBundleRepository,
    private val userRepository: UserRepository,
) : DescribeSpec({

    describe("paginateWithoutParent") {
        it("Bundle이 먼저 나오고 그 다음 Wordbook이 나온다.") {
            val owner = userRepository.save(
                UserFixture.create(username = "user1", email = "user1@test.com")
            )
            wordbookBundleRepository.save(
                WordbookBundleFixture.create(title = "묶음A", ownerId = owner.id!!)
            )
            wordbookRepository.save(
                WordbookFixture.create(title = "단어장A", ownerId = owner.id!!, bundleId = null)
            )

            val page = wordbookNodeRepository.paginateWithoutParent(
                ownerId = owner.id!!,
                pageable = PageRequest.of(0, 10),
                wordbookNodeSortType = WordbookNodeSortType.TITLE_ASC,
                starOnly = false
            )

            page.content shouldHaveSize 2
            page.content[0].nodeType shouldBe NodeType.BUNDLE
            page.content[1].nodeType shouldBe NodeType.SINGLE
        }

        it("TITLE_ASC 정렬 시 Bundle 내에서 제목 오름차순, Wordbook 내에서 제목 오름차순으로 정렬된다.") {
            val owner = userRepository.save(
                UserFixture.create(username = "user2", email = "user2@test.com")
            )
            wordbookBundleRepository.save(
                WordbookBundleFixture.create(title = "묶음C", ownerId = owner.id!!)
            )
            wordbookBundleRepository.save(
                WordbookBundleFixture.create(title = "묶음A", ownerId = owner.id)
            )
            wordbookRepository.save(
                WordbookFixture.create(title = "단어장C", ownerId = owner.id, bundleId = null)
            )
            wordbookRepository.save(
                WordbookFixture.create(title = "단어장A", ownerId = owner.id, bundleId = null)
            )

            val page = wordbookNodeRepository.paginateWithoutParent(
                ownerId = owner.id!!,
                pageable = PageRequest.of(0, 10),
                wordbookNodeSortType = WordbookNodeSortType.TITLE_ASC,
                starOnly = false
            )

            page.content shouldHaveSize 4
            page.content[0].title shouldBe "묶음A"
            page.content[1].title shouldBe "묶음C"
            page.content[2].title shouldBe "단어장A"
            page.content[3].title shouldBe "단어장C"
        }

        it("TITLE_DESC 정렬 시 Bundle 내에서 제목 내림차순, Wordbook 내에서 제목 내림차순으로 정렬된다.") {
            val owner = userRepository.save(
                UserFixture.create(username = "user3", email = "user3@test.com")
            )
            wordbookBundleRepository.save(
                WordbookBundleFixture.create(title = "묶음A", ownerId = owner.id!!)
            )
            wordbookBundleRepository.save(
                WordbookBundleFixture.create(title = "묶음C", ownerId = owner.id)
            )
            wordbookRepository.save(
                WordbookFixture.create(title = "단어장A", ownerId = owner.id, bundleId = null)
            )
            wordbookRepository.save(
                WordbookFixture.create(title = "단어장C", ownerId = owner.id, bundleId = null)
            )

            val page = wordbookNodeRepository.paginateWithoutParent(
                ownerId = owner.id!!,
                pageable = PageRequest.of(0, 10),
                wordbookNodeSortType = WordbookNodeSortType.TITLE_DESC,
                starOnly = false
            )

            page.content shouldHaveSize 4
            page.content[0].title shouldBe "묶음C"
            page.content[1].title shouldBe "묶음A"
            page.content[2].title shouldBe "단어장C"
            page.content[3].title shouldBe "단어장A"
        }

        it("CREATED_AT_ASC 정렬 시 Bundle 내에서 생성일 오름차순, Wordbook 내에서 생성일 오름차순으로 정렬된다.") {
            val owner = userRepository.save(
                UserFixture.create(username = "user4", email = "user4@test.com")
            )
            wordbookBundleRepository.save(
                WordbookBundleFixture.create(
                    title = "묶음2",
                    ownerId = owner.id!!,
                    createdAt = LocalDateTime.of(2025, 1, 2, 12, 0)
                )
            )
            wordbookBundleRepository.save(
                WordbookBundleFixture.create(
                    title = "묶음1",
                    ownerId = owner.id,
                    createdAt = LocalDateTime.of(2025, 1, 1, 12, 0)
                )
            )
            wordbookRepository.save(
                WordbookFixture.create(
                    title = "단어장2",
                    ownerId = owner.id,
                    bundleId = null,
                    createdAt = LocalDateTime.of(2025, 1, 2, 12, 0)
                )
            )
            wordbookRepository.save(
                WordbookFixture.create(
                    title = "단어장1",
                    ownerId = owner.id,
                    bundleId = null,
                    createdAt = LocalDateTime.of(2025, 1, 1, 12, 0)
                )
            )

            val page = wordbookNodeRepository.paginateWithoutParent(
                ownerId = owner.id!!,
                pageable = PageRequest.of(0, 10),
                wordbookNodeSortType = WordbookNodeSortType.CREATED_AT_ASC,
                starOnly = false
            )

            page.content shouldHaveSize 4
            page.content[0].title shouldBe "묶음1"
            page.content[1].title shouldBe "묶음2"
            page.content[2].title shouldBe "단어장1"
            page.content[3].title shouldBe "단어장2"
        }

        it("CREATED_AT_DESC 정렬 시 Bundle 내에서 생성일 내림차순, Wordbook 내에서 생성일 내림차순으로 정렬된다.") {
            val owner = userRepository.save(
                UserFixture.create(username = "user5", email = "user5@test.com")
            )
            wordbookBundleRepository.save(
                WordbookBundleFixture.create(
                    title = "묶음1",
                    ownerId = owner.id!!,
                    createdAt = LocalDateTime.of(2025, 1, 1, 12, 0)
                )
            )
            wordbookBundleRepository.save(
                WordbookBundleFixture.create(
                    title = "묶음2",
                    ownerId = owner.id,
                    createdAt = LocalDateTime.of(2025, 1, 2, 12, 0)
                )
            )
            wordbookRepository.save(
                WordbookFixture.create(
                    title = "단어장1",
                    ownerId = owner.id,
                    bundleId = null,
                    createdAt = LocalDateTime.of(2025, 1, 1, 12, 0)
                )
            )
            wordbookRepository.save(
                WordbookFixture.create(
                    title = "단어장2",
                    ownerId = owner.id,
                    bundleId = null,
                    createdAt = LocalDateTime.of(2025, 1, 2, 12, 0)
                )
            )

            val page = wordbookNodeRepository.paginateWithoutParent(
                ownerId = owner.id,
                pageable = PageRequest.of(0, 10),
                wordbookNodeSortType = WordbookNodeSortType.CREATED_AT_DESC,
                starOnly = false
            )

            page.content shouldHaveSize 4
            page.content[0].title shouldBe "묶음2"
            page.content[1].title shouldBe "묶음1"
            page.content[2].title shouldBe "단어장2"
            page.content[3].title shouldBe "단어장1"
        }

        it("starOnly가 true면 즐겨찾기한 항목만 조회된다.") {
            val owner = userRepository.save(
                UserFixture.create(username = "user6", email = "user6@test.com")
            )
            wordbookBundleRepository.save(
                WordbookBundleFixture.create(title = "묶음A", ownerId = owner.id!!, isStarred = true)
            )
            wordbookBundleRepository.save(
                WordbookBundleFixture.create(title = "묶음B", ownerId = owner.id, isStarred = false)
            )
            wordbookRepository.save(
                WordbookFixture.create(title = "단어장A", ownerId = owner.id, bundleId = null, isStarred = true)
            )
            wordbookRepository.save(
                WordbookFixture.create(title = "단어장B", ownerId = owner.id, bundleId = null, isStarred = false)
            )

            val page = wordbookNodeRepository.paginateWithoutParent(
                ownerId = owner.id,
                pageable = PageRequest.of(0, 10),
                wordbookNodeSortType = WordbookNodeSortType.TITLE_ASC,
                starOnly = true
            )

            page.content shouldHaveSize 2
            page.content[0].title shouldBe "묶음A"
            page.content[0].isStarred shouldBe true
            page.content[1].title shouldBe "단어장A"
            page.content[1].isStarred shouldBe true
        }

        it("starOnly가 false면 즐겨찾기 여부와 관계없이 모든 항목이 조회된다.") {
            val owner = userRepository.save(
                UserFixture.create(username = "user7", email = "user7@test.com")
            )
            wordbookBundleRepository.save(
                WordbookBundleFixture.create(title = "묶음A", ownerId = owner.id!!, isStarred = true)
            )
            wordbookBundleRepository.save(
                WordbookBundleFixture.create(title = "묶음B", ownerId = owner.id, isStarred = false)
            )
            wordbookRepository.save(
                WordbookFixture.create(title = "단어장A", ownerId = owner.id, bundleId = null, isStarred = true)
            )
            wordbookRepository.save(
                WordbookFixture.create(title = "단어장B", ownerId = owner.id, bundleId = null, isStarred = false)
            )

            val page = wordbookNodeRepository.paginateWithoutParent(
                ownerId = owner.id,
                pageable = PageRequest.of(0, 10),
                wordbookNodeSortType = WordbookNodeSortType.TITLE_ASC,
                starOnly = false
            )

            page.content shouldHaveSize 4
        }

        it("다른 사용자의 항목은 조회되지 않는다.") {
            val owner1 = userRepository.save(
                UserFixture.create(username = "owner1", email = "owner1@test.com")
            )
            val owner2 = userRepository.save(
                UserFixture.create(username = "owner2", email = "owner2@test.com")
            )
            wordbookBundleRepository.save(
                WordbookBundleFixture.create(title = "내 묶음", ownerId = owner1.id!!)
            )
            wordbookBundleRepository.save(
                WordbookBundleFixture.create(title = "다른사람 묶음", ownerId = owner2.id!!)
            )
            wordbookRepository.save(
                WordbookFixture.create(title = "내 단어장", ownerId = owner1.id, bundleId = null)
            )
            wordbookRepository.save(
                WordbookFixture.create(title = "다른사람 단어장", ownerId = owner2.id, bundleId = null)
            )

            val page = wordbookNodeRepository.paginateWithoutParent(
                ownerId = owner1.id,
                pageable = PageRequest.of(0, 10),
                wordbookNodeSortType = WordbookNodeSortType.TITLE_ASC,
                starOnly = false
            )

            page.content shouldHaveSize 2
            page.content[0].title shouldBe "내 묶음"
            page.content[1].title shouldBe "내 단어장"
        }

        it("Bundle에 속한 Wordbook은 조회되지 않는다.") {
            val owner = userRepository.save(
                UserFixture.create(username = "user8", email = "user8@test.com")
            )
            val bundle = wordbookBundleRepository.save(
                WordbookBundleFixture.create(title = "묶음", ownerId = owner.id!!)
            )
            wordbookRepository.save(
                WordbookFixture.create(title = "묶음 밖 단어장", ownerId = owner.id, bundleId = null)
            )
            wordbookRepository.save(
                WordbookFixture.create(title = "묶음 안 단어장", ownerId = owner.id, bundleId = bundle.id)
            )

            val page = wordbookNodeRepository.paginateWithoutParent(
                ownerId = owner.id,
                pageable = PageRequest.of(0, 10),
                wordbookNodeSortType = WordbookNodeSortType.TITLE_ASC,
                starOnly = false
            )

            page.content shouldHaveSize 2
            page.content[0].title shouldBe "묶음"
            page.content[1].title shouldBe "묶음 밖 단어장"
        }

        it("페이지네이션이 정상적으로 동작한다.") {
            val owner = userRepository.save(
                UserFixture.create(username = "user9", email = "user9@test.com")
            )
            repeat(15) { i ->
                wordbookRepository.save(
                    WordbookFixture.create(
                        title = "단어장${i + 1}",
                        ownerId = owner.id!!,
                        bundleId = null
                    )
                )
            }

            val page1 = wordbookNodeRepository.paginateWithoutParent(
                ownerId = owner.id!!,
                pageable = PageRequest.of(0, 10),
                wordbookNodeSortType = WordbookNodeSortType.TITLE_ASC,
                starOnly = false
            )
            val page2 = wordbookNodeRepository.paginateWithoutParent(
                ownerId = owner.id,
                pageable = PageRequest.of(1, 10),
                wordbookNodeSortType = WordbookNodeSortType.TITLE_ASC,
                starOnly = false
            )

            page1.content shouldHaveSize 10
            page1.totalElements shouldBe 15
            page1.totalPages shouldBe 2
            page1.hasNext() shouldBe true
            page2.content shouldHaveSize 5
            page2.hasNext() shouldBe false
        }

        it("조회할 항목이 없으면 빈 페이지를 반환한다.") {
            val owner = userRepository.save(
                UserFixture.create(username = "user10", email = "user10@test.com")
            )

            val page = wordbookNodeRepository.paginateWithoutParent(
                ownerId = owner.id!!,
                pageable = PageRequest.of(0, 10),
                wordbookNodeSortType = WordbookNodeSortType.TITLE_ASC,
                starOnly = false
            )

            page.content.shouldBeEmpty()
            page.totalElements shouldBe 0
        }
    }

    describe("paginateInBundle") {
        it("특정 Bundle에 속한 Wordbook들을 제목 오름차순으로 조회한다.") {
            val owner = userRepository.save(
                UserFixture.create(username = "user11", email = "user11@test.com")
            )
            val bundle = wordbookBundleRepository.save(
                WordbookBundleFixture.create(title = "묶음", ownerId = owner.id!!)
            )
            wordbookRepository.save(
                WordbookFixture.create(title = "단어장C", ownerId = owner.id, bundleId = bundle.id)
            )
            wordbookRepository.save(
                WordbookFixture.create(title = "단어장A", ownerId = owner.id, bundleId = bundle.id)
            )
            wordbookRepository.save(
                WordbookFixture.create(title = "단어장B", ownerId = owner.id, bundleId = bundle.id)
            )

            val page = wordbookNodeRepository.paginateInBundle(
                ownerId = owner.id,
                bundleId = bundle.id!!,
                pageable = PageRequest.of(0, 10)
            )

            page.content shouldHaveSize 3
            page.content[0].title shouldBe "단어장A"
            page.content[1].title shouldBe "단어장B"
            page.content[2].title shouldBe "단어장C"
        }

        it("Bundle이 비어있으면 빈 페이지를 반환한다.") {
            val owner = userRepository.save(
                UserFixture.create(username = "user12", email = "user12@test.com")
            )
            val bundle = wordbookBundleRepository.save(
                WordbookBundleFixture.create(title = "빈 묶음", ownerId = owner.id!!)
            )

            val page = wordbookNodeRepository.paginateInBundle(
                ownerId = owner.id,
                bundleId = bundle.id!!,
                pageable = PageRequest.of(0, 10)
            )

            page.content.shouldBeEmpty()
            page.totalElements shouldBe 0
        }

        it("다른 Bundle의 Wordbook은 조회되지 않는다.") {
            val owner = userRepository.save(
                UserFixture.create(username = "user13", email = "user13@test.com")
            )
            val bundle1 = wordbookBundleRepository.save(
                WordbookBundleFixture.create(title = "묶음1", ownerId = owner.id!!)
            )
            val bundle2 = wordbookBundleRepository.save(
                WordbookBundleFixture.create(title = "묶음2", ownerId = owner.id)
            )
            wordbookRepository.save(
                WordbookFixture.create(title = "묶음1의 단어장", ownerId = owner.id, bundleId = bundle1.id)
            )
            wordbookRepository.save(
                WordbookFixture.create(title = "묶음2의 단어장", ownerId = owner.id, bundleId = bundle2.id)
            )

            val page = wordbookNodeRepository.paginateInBundle(
                ownerId = owner.id!!,
                bundleId = bundle1.id!!,
                pageable = PageRequest.of(0, 10)
            )

            page.content shouldHaveSize 1
            page.content[0].title shouldBe "묶음1의 단어장"
        }

        it("다른 사용자의 Wordbook은 조회되지 않는다.") {
            val owner1 = userRepository.save(
                UserFixture.create(username = "owner1x", email = "owner1x@test.com")
            )
            val owner2 = userRepository.save(
                UserFixture.create(username = "owner2x", email = "owner2x@test.com")
            )
            val bundle1 = wordbookBundleRepository.save(
                WordbookBundleFixture.create(title = "내 묶음", ownerId = owner1.id!!)
            )
            val bundle2 = wordbookBundleRepository.save(
                WordbookBundleFixture.create(title = "다른사람 묶음", ownerId = owner2.id!!)
            )
            wordbookRepository.save(
                WordbookFixture.create(title = "내 단어장", ownerId = owner1.id, bundleId = bundle1.id)
            )
            wordbookRepository.save(
                WordbookFixture.create(title = "다른사람 단어장", ownerId = owner2.id, bundleId = bundle2.id)
            )

            val page = wordbookNodeRepository.paginateInBundle(
                ownerId = owner1.id,
                bundleId = bundle1.id!!,
                pageable = PageRequest.of(0, 10)
            )

            page.content shouldHaveSize 1
            page.content[0].title shouldBe "내 단어장"
        }

        it("페이지네이션이 정상적으로 동작한다.") {
            val owner = userRepository.save(
                UserFixture.create(username = "user14", email = "user14@test.com")
            )
            val bundle = wordbookBundleRepository.save(
                WordbookBundleFixture.create(title = "묶음", ownerId = owner.id!!)
            )
            repeat(15) { i ->
                wordbookRepository.save(
                    WordbookFixture.create(
                        title = "단어장${String.format("%02d", i + 1)}",
                        ownerId = owner.id!!,
                        bundleId = bundle.id
                    )
                )
            }

            val page1 = wordbookNodeRepository.paginateInBundle(
                ownerId = owner.id!!,
                bundleId = bundle.id!!,
                pageable = PageRequest.of(0, 10)
            )
            val page2 = wordbookNodeRepository.paginateInBundle(
                ownerId = owner.id!!,
                bundleId = bundle.id!!,
                pageable = PageRequest.of(1, 10)
            )

            page1.content shouldHaveSize 10
            page1.totalElements shouldBe 15
            page1.totalPages shouldBe 2
            page1.hasNext() shouldBe true
            page2.content shouldHaveSize 5
            page2.hasNext() shouldBe false
        }

        it("Bundle 밖의 Wordbook은 조회되지 않는다.") {
            val owner = userRepository.save(
                UserFixture.create(username = "user15", email = "user15@test.com")
            )
            val bundle = wordbookBundleRepository.save(
                WordbookBundleFixture.create(title = "묶음", ownerId = owner.id!!)
            )
            wordbookRepository.save(
                WordbookFixture.create(title = "묶음 안 단어장", ownerId = owner.id!!, bundleId = bundle.id)
            )
            wordbookRepository.save(
                WordbookFixture.create(title = "묶음 밖 단어장", ownerId = owner.id!!, bundleId = null)
            )

            val page = wordbookNodeRepository.paginateInBundle(
                ownerId = owner.id!!,
                bundleId = bundle.id!!,
                pageable = PageRequest.of(0, 10)
            )

            page.content shouldHaveSize 1
            page.content[0].title shouldBe "묶음 안 단어장"
        }
    }
})
