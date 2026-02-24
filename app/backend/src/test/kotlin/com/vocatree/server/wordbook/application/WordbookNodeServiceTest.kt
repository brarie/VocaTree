package com.vocatree.server.wordbook.application

import com.vocatree.server.user.domain.port.UserRepository
import com.vocatree.server.user.fixture.UserFixture
import com.vocatree.server.wordbook.domain.port.WordbookBundleRepository
import com.vocatree.server.wordbook.domain.port.WordbookRepository
import com.vocatree.server.wordbook.domain.port.options.WordbookNodeSortType
import com.vocatree.server.wordbook.fixture.WordbookBundleFixture
import com.vocatree.server.wordbook.fixture.WordbookFixture
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@ApplyExtension(SpringExtension::class)
@Transactional
class WordbookNodeServiceTest(
    private val wordbookNodeService: WordbookNodeService,
    private val wordbookRepository: WordbookRepository,
    private val wordbookBundleRepository: WordbookBundleRepository,
    private val userRepository: UserRepository,
) : DescribeSpec({

    describe("paginateNodesWithoutParent") {
        context("부모 없는 노드들 조회 요청할 때") {
            it("단어장과 번들 목록을 반환한다.") {
                val owner = userRepository.save(
                    UserFixture.create(username = "node1", email = "node1@test.com")
                )
                wordbookRepository.save(
                    WordbookFixture.create(
                        title = "단어장1",
                        ownerId = owner.id!!,
                        bundleId = null
                    )
                )
                wordbookBundleRepository.save(
                    WordbookBundleFixture.create(
                        title = "번들1",
                        ownerId = owner.id!!
                    )
                )

                val page = wordbookNodeService.paginateNodesWithoutParent(
                    userId = owner.id!!,
                    pageable = PageRequest.of(0, 10),
                    wordbookNodeSortType = WordbookNodeSortType.CREATED_AT_DESC,
                    starOnly = false
                )

                page.totalElements shouldBe 2
            }
        }

        context("즐겨찾기만 조회 요청할 때") {
            it("즐겨찾기한 노드만 반환한다.") {
                val owner = userRepository.save(
                    UserFixture.create(username = "node2", email = "node2@test.com")
                )
                wordbookRepository.save(
                    WordbookFixture.create(
                        title = "즐겨찾기",
                        ownerId = owner.id!!,
                        isStarred = true
                    )
                )
                wordbookRepository.save(
                    WordbookFixture.create(
                        title = "일반",
                        ownerId = owner.id!!,
                        isStarred = false
                    )
                )

                val page = wordbookNodeService.paginateNodesWithoutParent(
                    userId = owner.id!!,
                    pageable = PageRequest.of(0, 10),
                    wordbookNodeSortType = WordbookNodeSortType.CREATED_AT_DESC,
                    starOnly = true
                )

                page.totalElements shouldBe 1
                page.content[0].title shouldBe "즐겨찾기"
            }
        }
    }

    describe("paginateNodesInBundle") {
        context("번들 내 노드들 조회 요청할 때") {
            it("번들 내 단어장 목록을 반환한다.") {
                val owner = userRepository.save(
                    UserFixture.create(username = "node3", email = "node3@test.com")
                )
                val bundle = wordbookBundleRepository.save(
                    WordbookBundleFixture.create(
                        title = "테스트 번들",
                        ownerId = owner.id!!
                    )
                )
                wordbookRepository.save(
                    WordbookFixture.create(
                        title = "번들 내 단어장",
                        ownerId = owner.id!!,
                        bundleId = bundle.id!!
                    )
                )
                wordbookRepository.save(
                    WordbookFixture.create(
                        title = "번들 밖 단어장",
                        ownerId = owner.id!!,
                        bundleId = null
                    )
                )

                val page = wordbookNodeService.paginateNodesInBundle(
                    userId = owner.id!!,
                    bundleId = bundle.id!!,
                    pageable = PageRequest.of(0, 10)
                )

                page.totalElements shouldBe 1
                page.content[0].title shouldBe "번들 내 단어장"
            }
        }
    }
})
