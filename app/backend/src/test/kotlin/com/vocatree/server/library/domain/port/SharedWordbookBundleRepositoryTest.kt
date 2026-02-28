package com.vocatree.server.library.domain.port

import com.vocatree.server.library.domain.model.enumerated.SharedState
import com.vocatree.server.library.domain.model.enumerated.TrustedLevel
import com.vocatree.server.library.domain.port.options.SharedWordbookBundleSortType
import com.vocatree.server.library.fixture.SharedWordbookBundleFixture
import com.vocatree.server.library.fixture.SharedWordbookFixture
import com.vocatree.server.library.fixture.SharedWordbookRevisionFixture
import com.vocatree.server.user.domain.port.UserRepository
import com.vocatree.server.user.fixture.UserFixture
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@ApplyExtension(SpringExtension::class)
@Transactional
class SharedWordbookBundleRepositoryTest(
    private val sharedWordbookBundleRepository: SharedWordbookBundleRepository,
    private val sharedWordbookRepository: SharedWordbookRepository,
    private val sharedWordbookRevisionRepository: SharedWordbookRevisionRepository,
    private val userRepository: UserRepository,
) : DescribeSpec({

    describe("save") {
        it("새로운 번들을 저장하면 ID가 할당된다.") {
            val creator = userRepository.save(UserFixture.create(username = "bndls1", email = "bndls1@test.com"))

            val saved = sharedWordbookBundleRepository.save(
                SharedWordbookBundleFixture.create(creatorId = creator.id!!)
            )

            saved.id shouldNotBe null
            saved.title shouldBe "테스트 번들"
            saved.creatorId shouldBe creator.id
            saved.sharedState shouldBe SharedState.PUBLIC
            saved.trustedLevel shouldBe TrustedLevel.BASIC
        }

        it("번들의 slug를 업데이트할 수 있다.") {
            val creator = userRepository.save(UserFixture.create(username = "bndls2", email = "bndls2@test.com"))
            val saved = sharedWordbookBundleRepository.save(
                SharedWordbookBundleFixture.create(creatorId = creator.id!!, slug = null)
            )

            val updated = sharedWordbookBundleRepository.save(saved.updateSlug())

            updated.slug shouldNotBe null
            updated.slug!!.endsWith("-${updated.id}") shouldBe true
        }

        it("번들 메타데이터를 수정하면 변경된 내용이 반영된다.") {
            val creator = userRepository.save(UserFixture.create(username = "bndls3", email = "bndls3@test.com"))
            val saved = sharedWordbookBundleRepository.save(
                SharedWordbookBundleFixture.create(creatorId = creator.id!!, title = "원래 제목")
            )

            val updated = sharedWordbookBundleRepository.save(
                saved.updateMetadata("수정된 제목", "새로운 설명")
            )

            updated.title shouldBe "수정된 제목"
            updated.description shouldBe "새로운 설명"
        }
    }

    describe("findById") {
        it("ID로 번들을 조회한다.") {
            val creator = userRepository.save(UserFixture.create(username = "bndfnd1", email = "bndfnd1@test.com"))
            val saved = sharedWordbookBundleRepository.save(
                SharedWordbookBundleFixture.create(creatorId = creator.id!!, title = "조회 테스트 번들")
            )

            val found = sharedWordbookBundleRepository.findById(saved.id!!)

            found shouldNotBe null
            found!!.id shouldBe saved.id
            found.title shouldBe "조회 테스트 번들"
            found.creatorId shouldBe creator.id
        }

        it("존재하지 않는 ID로 조회하면 null을 반환한다.") {
            sharedWordbookBundleRepository.findById(999999L) shouldBe null
        }
    }

    describe("paginatePublicViews") {
        it("PUBLIC 상태의 번들만 조회된다.") {
            val creator = userRepository.save(UserFixture.create(username = "bndpub1", email = "bndpub1@test.com"))

            val public1 = sharedWordbookBundleRepository.save(
                SharedWordbookBundleFixture.create(creatorId = creator.id!!)
            ).let { sharedWordbookBundleRepository.save(it.updateSlug()) }

            val banned = sharedWordbookBundleRepository.save(
                SharedWordbookBundleFixture.create(creatorId = creator.id!!, sharedState = SharedState.BANNED)
            ).let { sharedWordbookBundleRepository.save(it.updateSlug()) }

            val deleted = sharedWordbookBundleRepository.save(
                SharedWordbookBundleFixture.create(creatorId = creator.id!!, sharedState = SharedState.DELETED_BY_OWNER)
            ).let { sharedWordbookBundleRepository.save(it.updateSlug()) }

            val result = sharedWordbookBundleRepository.paginatePublicViews(
                null, emptyList(), SharedWordbookBundleSortType.CREATED_AT_DESC, PageRequest.of(0, 10)
            )

            result.content.map { it.id } shouldContain public1.id!!
            result.content.map { it.id } shouldNotContain banned.id!!
            result.content.map { it.id } shouldNotContain deleted.id!!
        }

        it("title prefix로 번들을 검색한다.") {
            val creator = userRepository.save(UserFixture.create(username = "bndttl1", email = "bndttl1@test.com"))

            val english = sharedWordbookBundleRepository.save(
                SharedWordbookBundleFixture.create(creatorId = creator.id!!, title = "영어 번들")
            ).let { sharedWordbookBundleRepository.save(it.updateSlug()) }

            val japanese = sharedWordbookBundleRepository.save(
                SharedWordbookBundleFixture.create(creatorId = creator.id!!, title = "일본어 번들")
            ).let { sharedWordbookBundleRepository.save(it.updateSlug()) }

            val result = sharedWordbookBundleRepository.paginatePublicViews(
                "영어", emptyList(), SharedWordbookBundleSortType.CREATED_AT_DESC, PageRequest.of(0, 10)
            )

            result.content.map { it.id } shouldContain english.id!!
            result.content.map { it.id } shouldNotContain japanese.id!!
        }

        it("trustedLevel 필터로 해당 레벨만 조회된다.") {
            val creator = userRepository.save(UserFixture.create(username = "bndlvl1", email = "bndlvl1@test.com"))

            val official = sharedWordbookBundleRepository.save(
                SharedWordbookBundleFixture.create(creatorId = creator.id!!, trustedLevel = TrustedLevel.OFFICIAL)
            ).let { sharedWordbookBundleRepository.save(it.updateSlug()) }

            val basic = sharedWordbookBundleRepository.save(
                SharedWordbookBundleFixture.create(creatorId = creator.id!!, trustedLevel = TrustedLevel.BASIC)
            ).let { sharedWordbookBundleRepository.save(it.updateSlug()) }

            val result = sharedWordbookBundleRepository.paginatePublicViews(
                null, listOf(TrustedLevel.OFFICIAL), SharedWordbookBundleSortType.CREATED_AT_DESC, PageRequest.of(0, 10)
            )

            result.content.map { it.id } shouldContain official.id!!
            result.content.map { it.id } shouldNotContain basic.id!!
            result.content.all { it.trustedLevel == TrustedLevel.OFFICIAL } shouldBe true
        }

        it("trustedLevel 필터가 비어있으면 모든 레벨을 조회한다.") {
            val creator = userRepository.save(UserFixture.create(username = "bndlvl2", email = "bndlvl2@test.com"))

            val official = sharedWordbookBundleRepository.save(
                SharedWordbookBundleFixture.create(creatorId = creator.id!!, trustedLevel = TrustedLevel.OFFICIAL)
            ).let { sharedWordbookBundleRepository.save(it.updateSlug()) }

            val basic = sharedWordbookBundleRepository.save(
                SharedWordbookBundleFixture.create(creatorId = creator.id!!, trustedLevel = TrustedLevel.BASIC)
            ).let { sharedWordbookBundleRepository.save(it.updateSlug()) }

            val result = sharedWordbookBundleRepository.paginatePublicViews(
                null, emptyList(), SharedWordbookBundleSortType.CREATED_AT_DESC, PageRequest.of(0, 10)
            )

            result.content.map { it.id } shouldContain official.id!!
            result.content.map { it.id } shouldContain basic.id!!
        }

        it("페이지네이션이 동작한다.") {
            val creator = userRepository.save(UserFixture.create(username = "bndpg1", email = "bndpg1@test.com"))
            repeat(5) {
                sharedWordbookBundleRepository.save(
                    SharedWordbookBundleFixture.create(creatorId = creator.id!!)
                ).let { sharedWordbookBundleRepository.save(it.updateSlug()) }
            }

            val page1 = sharedWordbookBundleRepository.paginatePublicViews(
                null, emptyList(), SharedWordbookBundleSortType.CREATED_AT_DESC, PageRequest.of(0, 3)
            )
            val page2 = sharedWordbookBundleRepository.paginatePublicViews(
                null, emptyList(), SharedWordbookBundleSortType.CREATED_AT_DESC, PageRequest.of(1, 3)
            )

            page1.content.size shouldBe 3
            page2.content.size shouldBe 2
            page1.totalElements shouldBe 5
        }

        it("조회된 번들에 creator 정보와 단어장 개수가 포함된다.") {
            val creator = userRepository.save(UserFixture.create(username = "bndinfo1", email = "bndinfo1@test.com"))
            val wb1 = sharedWordbookRepository.save(SharedWordbookFixture.create(creatorId = creator.id!!))
                .let { sharedWordbookRepository.save(it.updateSlug()) }
            sharedWordbookRevisionRepository.save(SharedWordbookRevisionFixture.create(version = 1L), wb1.id!!)

            val wb2 = sharedWordbookRepository.save(SharedWordbookFixture.create(creatorId = creator.id!!))
                .let { sharedWordbookRepository.save(it.updateSlug()) }
            sharedWordbookRevisionRepository.save(SharedWordbookRevisionFixture.create(version = 1L), wb2.id!!)

            val bundle = sharedWordbookBundleRepository.save(
                SharedWordbookBundleFixture.create(creatorId = creator.id!!)
            ).let { sharedWordbookBundleRepository.save(it.updateSlug()) }
            sharedWordbookBundleRepository.addWordbook(bundle.id!!, wb1.id!!)
            sharedWordbookBundleRepository.addWordbook(bundle.id!!, wb2.id!!)

            val result = sharedWordbookBundleRepository.paginatePublicViews(
                null, emptyList(), SharedWordbookBundleSortType.CREATED_AT_DESC, PageRequest.of(0, 10)
            )

            val view = result.content.first { it.id == bundle.id!! }
            view.creator.id shouldBe creator.id
            view.wordbookCount shouldBe 2
            view.slug.endsWith("-${bundle.id}") shouldBe true
        }

        it("단어장이 없는 번들의 단어장 개수는 0이다.") {
            val creator = userRepository.save(UserFixture.create(username = "bndcnt1", email = "bndcnt1@test.com"))
            val bundle = sharedWordbookBundleRepository.save(
                SharedWordbookBundleFixture.create(creatorId = creator.id!!)
            ).let { sharedWordbookBundleRepository.save(it.updateSlug()) }

            val result = sharedWordbookBundleRepository.paginatePublicViews(
                null, emptyList(), SharedWordbookBundleSortType.CREATED_AT_DESC, PageRequest.of(0, 10)
            )

            val view = result.content.first { it.id == bundle.id!! }
            view.wordbookCount shouldBe 0
        }
    }

    describe("findDetailView") {
        it("번들 상세 정보를 조회한다.") {
            val creator = userRepository.save(UserFixture.create(username = "bnddet1", email = "bnddet1@test.com"))
            val saved = sharedWordbookBundleRepository.save(
                SharedWordbookBundleFixture.create(
                    creatorId = creator.id!!,
                    title = "상세 조회 번들",
                    description = "번들 설명입니다",
                )
            ).let { sharedWordbookBundleRepository.save(it.updateSlug()) }

            val detail = sharedWordbookBundleRepository.findDetailView(saved.id!!)

            detail shouldNotBe null
            detail!!.id shouldBe saved.id
            detail.title shouldBe "상세 조회 번들"
            detail.description shouldBe "번들 설명입니다"
            detail.creator.id shouldBe creator.id
            detail.wordbookCount shouldBe 0
        }

        it("번들에 포함된 단어장 개수가 정확히 반환된다.") {
            val creator = userRepository.save(UserFixture.create(username = "bnddet2", email = "bnddet2@test.com"))

            val wb1 = sharedWordbookRepository.save(SharedWordbookFixture.create(creatorId = creator.id!!))
                .let { sharedWordbookRepository.save(it.updateSlug()) }
            sharedWordbookRevisionRepository.save(SharedWordbookRevisionFixture.create(version = 1L), wb1.id!!)

            val wb2 = sharedWordbookRepository.save(SharedWordbookFixture.create(creatorId = creator.id!!))
                .let { sharedWordbookRepository.save(it.updateSlug()) }
            sharedWordbookRevisionRepository.save(SharedWordbookRevisionFixture.create(version = 1L), wb2.id!!)

            val bundle = sharedWordbookBundleRepository.save(
                SharedWordbookBundleFixture.create(creatorId = creator.id!!)
            ).let { sharedWordbookBundleRepository.save(it.updateSlug()) }
            sharedWordbookBundleRepository.addWordbook(bundle.id!!, wb1.id!!)
            sharedWordbookBundleRepository.addWordbook(bundle.id!!, wb2.id!!)

            val detail = sharedWordbookBundleRepository.findDetailView(bundle.id!!)

            detail!!.wordbookCount shouldBe 2
        }

        it("존재하지 않는 ID로 조회하면 null을 반환한다.") {
            sharedWordbookBundleRepository.findDetailView(999999L) shouldBe null
        }
    }

    describe("addWordbook / removeWordbook") {
        it("번들에 단어장을 추가하면 paginateWordbooksInBundle에서 조회된다.") {
            val creator = userRepository.save(UserFixture.create(username = "bndadd1", email = "bndadd1@test.com"))

            val wb = sharedWordbookRepository.save(SharedWordbookFixture.create(creatorId = creator.id!!))
                .let { sharedWordbookRepository.save(it.updateSlug()) }
            sharedWordbookRevisionRepository.save(SharedWordbookRevisionFixture.create(version = 1L), wb.id!!)

            val bundle = sharedWordbookBundleRepository.save(
                SharedWordbookBundleFixture.create(creatorId = creator.id!!)
            ).let { sharedWordbookBundleRepository.save(it.updateSlug()) }

            sharedWordbookBundleRepository.addWordbook(bundle.id!!, wb.id!!)

            val result = sharedWordbookBundleRepository.paginateWordbooksInBundle(bundle.id!!, PageRequest.of(0, 10))
            result.content.map { it.id } shouldContain wb.id!!
        }

        it("같은 단어장을 중복으로 추가해도 한 번만 포함된다.") {
            val creator = userRepository.save(UserFixture.create(username = "bnddup1", email = "bnddup1@test.com"))

            val wb = sharedWordbookRepository.save(SharedWordbookFixture.create(creatorId = creator.id!!))
                .let { sharedWordbookRepository.save(it.updateSlug()) }
            sharedWordbookRevisionRepository.save(SharedWordbookRevisionFixture.create(version = 1L), wb.id!!)

            val bundle = sharedWordbookBundleRepository.save(
                SharedWordbookBundleFixture.create(creatorId = creator.id!!)
            ).let { sharedWordbookBundleRepository.save(it.updateSlug()) }

            sharedWordbookBundleRepository.addWordbook(bundle.id!!, wb.id!!)
            sharedWordbookBundleRepository.addWordbook(bundle.id!!, wb.id!!)

            val result = sharedWordbookBundleRepository.paginateWordbooksInBundle(bundle.id!!, PageRequest.of(0, 10))
            result.content.size shouldBe 1
        }

        it("번들에서 단어장을 제거하면 조회되지 않는다.") {
            val creator = userRepository.save(UserFixture.create(username = "bndrm1", email = "bndrm1@test.com"))

            val wb = sharedWordbookRepository.save(SharedWordbookFixture.create(creatorId = creator.id!!))
                .let { sharedWordbookRepository.save(it.updateSlug()) }
            sharedWordbookRevisionRepository.save(SharedWordbookRevisionFixture.create(version = 1L), wb.id!!)

            val bundle = sharedWordbookBundleRepository.save(
                SharedWordbookBundleFixture.create(creatorId = creator.id!!)
            ).let { sharedWordbookBundleRepository.save(it.updateSlug()) }

            sharedWordbookBundleRepository.addWordbook(bundle.id!!, wb.id!!)
            sharedWordbookBundleRepository.removeWordbook(bundle.id!!, wb.id!!)

            val result = sharedWordbookBundleRepository.paginateWordbooksInBundle(bundle.id!!, PageRequest.of(0, 10))
            result.content.map { it.id } shouldNotContain wb.id!!
        }
    }

    describe("paginateWordbooksInBundle") {
        it("번들 내 단어장 목록을 최신 버전 정보와 함께 조회한다.") {
            val creator = userRepository.save(UserFixture.create(username = "bndwbl1", email = "bndwbl1@test.com"))

            val wb = sharedWordbookRepository.save(SharedWordbookFixture.create(creatorId = creator.id!!))
                .let { sharedWordbookRepository.save(it.updateSlug()) }
            sharedWordbookRevisionRepository.save(SharedWordbookRevisionFixture.create(version = 1L), wb.id!!)
            sharedWordbookRevisionRepository.save(SharedWordbookRevisionFixture.create(version = 2L), wb.id!!)

            val bundle = sharedWordbookBundleRepository.save(
                SharedWordbookBundleFixture.create(creatorId = creator.id!!)
            ).let { sharedWordbookBundleRepository.save(it.updateSlug()) }
            sharedWordbookBundleRepository.addWordbook(bundle.id!!, wb.id!!)

            val result = sharedWordbookBundleRepository.paginateWordbooksInBundle(bundle.id!!, PageRequest.of(0, 10))

            result.totalElements shouldBe 1
            val view = result.content.first()
            view.id shouldBe wb.id
            view.latestVersion shouldBe 2L
            view.creator.id shouldBe creator.id
        }

        it("PUBLIC 상태가 아닌 단어장들도 사용자 UX 개선을 위해 조회된다.") {
            val creator = userRepository.save(UserFixture.create(username = "bndwbl2", email = "bndwbl2@test.com"))

            val publicWb = sharedWordbookRepository.save(SharedWordbookFixture.create(creatorId = creator.id!!))
                .let { sharedWordbookRepository.save(it.updateSlug()) }
            sharedWordbookRevisionRepository.save(SharedWordbookRevisionFixture.create(version = 1L), publicWb.id!!)

            val bannedWb = sharedWordbookRepository.save(
                SharedWordbookFixture.create(creatorId = creator.id!!, sharedState = SharedState.BANNED)
            ).let { sharedWordbookRepository.save(it.updateSlug()) }
            sharedWordbookRevisionRepository.save(SharedWordbookRevisionFixture.create(version = 1L), bannedWb.id!!)

            val bundle = sharedWordbookBundleRepository.save(
                SharedWordbookBundleFixture.create(creatorId = creator.id!!)
            ).let { sharedWordbookBundleRepository.save(it.updateSlug()) }
            sharedWordbookBundleRepository.addWordbook(bundle.id!!, publicWb.id!!)
            sharedWordbookBundleRepository.addWordbook(bundle.id!!, bannedWb.id!!)

            val result = sharedWordbookBundleRepository.paginateWordbooksInBundle(bundle.id!!, PageRequest.of(0, 10))

            result.content.map { it.id } shouldContainAll listOf(publicWb.id!!, bannedWb.id!!)
        }

        it("페이지네이션이 동작한다.") {
            val creator = userRepository.save(UserFixture.create(username = "bndwpg1", email = "bndwpg1@test.com"))

            val bundle = sharedWordbookBundleRepository.save(
                SharedWordbookBundleFixture.create(creatorId = creator.id!!)
            ).let { sharedWordbookBundleRepository.save(it.updateSlug()) }

            repeat(5) {
                val wb = sharedWordbookRepository.save(SharedWordbookFixture.create(creatorId = creator.id!!))
                    .let { sharedWordbookRepository.save(it.updateSlug()) }
                sharedWordbookRevisionRepository.save(SharedWordbookRevisionFixture.create(version = 1L), wb.id!!)
                sharedWordbookBundleRepository.addWordbook(bundle.id!!, wb.id!!)
            }

            val page1 = sharedWordbookBundleRepository.paginateWordbooksInBundle(bundle.id!!, PageRequest.of(0, 2))
            val page2 = sharedWordbookBundleRepository.paginateWordbooksInBundle(bundle.id!!, PageRequest.of(1, 2))

            page1.content.size shouldBe 2
            page2.content.size shouldBe 2
            page1.totalElements shouldBe 5
        }

        it("번들이 비어있으면 빈 페이지를 반환한다.") {
            val creator = userRepository.save(UserFixture.create(username = "bndwem1", email = "bndwem1@test.com"))
            val bundle = sharedWordbookBundleRepository.save(
                SharedWordbookBundleFixture.create(creatorId = creator.id!!)
            ).let { sharedWordbookBundleRepository.save(it.updateSlug()) }

            val result = sharedWordbookBundleRepository.paginateWordbooksInBundle(bundle.id!!, PageRequest.of(0, 10))

            result.totalElements shouldBe 0
            result.content shouldBe emptyList()
        }
    }
})
