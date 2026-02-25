package com.vocatree.server.library.domain.port

import com.vocatree.server.library.domain.model.enumerated.SharedState
import com.vocatree.server.library.domain.model.enumerated.TrustedLevel
import com.vocatree.server.library.domain.port.options.SharedWordbookSortType
import com.vocatree.server.library.fixture.SharedWordbookFixture
import com.vocatree.server.library.fixture.SharedWordbookRevisionFixture
import com.vocatree.server.user.domain.port.UserRepository
import com.vocatree.server.user.fixture.UserFixture
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@ApplyExtension(SpringExtension::class)
@Transactional
class SharedWordbookRepositoryTest(
    private val sharedWordbookRepository: SharedWordbookRepository,
    private val sharedWordbookRevisionRepository: SharedWordbookRevisionRepository,
    private val userRepository: UserRepository,
) : DescribeSpec({

    describe("save") {
        it("새로운 공유 단어장을 저장하면 ID가 할당된다.") {
            val creator = userRepository.save(
                UserFixture.create(username = "swsave1", email = "swsave1@test.com")
            )
            val wordbook = SharedWordbookFixture.create(creatorId = creator.id!!)

            val saved = sharedWordbookRepository.save(wordbook)

            saved.id shouldNotBe null
            saved.title shouldBe wordbook.title
            saved.creatorId shouldBe creator.id
            saved.sharedState shouldBe SharedState.PUBLIC
            saved.trustedLevel shouldBe TrustedLevel.BASIC
        }

        it("공유 단어장의 slug를 업데이트할 수 있다.") {
            val creator = userRepository.save(
                UserFixture.create(username = "swsave2", email = "swsave2@test.com")
            )
            val saved = sharedWordbookRepository.save(
                SharedWordbookFixture.create(creatorId = creator.id!!, slug = null)
            )
            val withSlug = saved.updateSlug()

            val updated = sharedWordbookRepository.save(withSlug)

            updated.slug shouldNotBe null
            updated.slug!!.endsWith("-${updated.id}") shouldBe true
        }

        it("공유 단어장의 메타데이터를 수정하면 변경된 내용이 반영된다.") {
            val creator = userRepository.save(
                UserFixture.create(username = "swsave3", email = "swsave3@test.com")
            )
            val saved = sharedWordbookRepository.save(
                SharedWordbookFixture.create(creatorId = creator.id!!, title = "원래 제목")
            )
            val updated = sharedWordbookRepository.save(
                saved.updateMetadata("수정된 제목", "새로운 설명")
            )

            updated.title shouldBe "수정된 제목"
            updated.description shouldBe "새로운 설명"
        }
    }

    describe("findById") {
        it("ID로 공유 단어장을 조회한다.") {
            val creator = userRepository.save(
                UserFixture.create(username = "swfind1", email = "swfind1@test.com")
            )
            val saved = sharedWordbookRepository.save(
                SharedWordbookFixture.create(creatorId = creator.id!!, title = "조회 테스트 단어장")
            )

            val found = sharedWordbookRepository.findById(saved.id!!)

            found shouldNotBe null
            found!!.id shouldBe saved.id
            found.title shouldBe "조회 테스트 단어장"
            found.creatorId shouldBe creator.id
        }

        it("존재하지 않는 ID로 조회하면 null을 반환한다.") {
            val found = sharedWordbookRepository.findById(999999L)

            found shouldBe null
        }
    }

    describe("paginatePublicViews") {
        it("PUBLIC 상태의 단어장만 조회된다.") {
            val creator = userRepository.save(
                UserFixture.create(username = "pvpub1", email = "pvpub1@test.com")
            )
            val public1 = sharedWordbookRepository.save(SharedWordbookFixture.create(creatorId = creator.id!!))
                .let { sharedWordbookRepository.save(it.updateSlug()) }
            sharedWordbookRevisionRepository.save(SharedWordbookRevisionFixture.create(version = 1L), public1.id!!)

            val banned = sharedWordbookRepository.save(
                SharedWordbookFixture.create(creatorId = creator.id!!, sharedState = SharedState.BANNED)
            ).let { sharedWordbookRepository.save(it.updateSlug()) }
            sharedWordbookRevisionRepository.save(SharedWordbookRevisionFixture.create(version = 1L), banned.id!!)

            val deleted = sharedWordbookRepository.save(
                SharedWordbookFixture.create(creatorId = creator.id!!, sharedState = SharedState.DELETED_BY_OWNER)
            ).let { sharedWordbookRepository.save(it.updateSlug()) }
            sharedWordbookRevisionRepository.save(SharedWordbookRevisionFixture.create(version = 1L), deleted.id!!)

            val result = sharedWordbookRepository.paginatePublicViews(
                null, emptyList(), SharedWordbookSortType.CREATED_AT_DESC, PageRequest.of(0, 10)
            )

            result.content.map { it.id } shouldContain public1.id!!
            result.content.map { it.id } shouldNotContain banned.id!!
            result.content.map { it.id } shouldNotContain deleted.id!!
        }

        it("title prefix로 단어장을 검색한다.") {
            val creator = userRepository.save(
                UserFixture.create(username = "pvtitle1", email = "pvtitle1@test.com")
            )
            val english = sharedWordbookRepository.save(
                SharedWordbookFixture.create(creatorId = creator.id!!, title = "영어 단어장")
            ).let { sharedWordbookRepository.save(it.updateSlug()) }
            sharedWordbookRevisionRepository.save(SharedWordbookRevisionFixture.create(version = 1L), english.id!!)

            val japanese = sharedWordbookRepository.save(
                SharedWordbookFixture.create(creatorId = creator.id!!, title = "일본어 단어장")
            ).let { sharedWordbookRepository.save(it.updateSlug()) }
            sharedWordbookRevisionRepository.save(SharedWordbookRevisionFixture.create(version = 1L), japanese.id!!)

            val result = sharedWordbookRepository.paginatePublicViews(
                "영어", emptyList(), SharedWordbookSortType.CREATED_AT_DESC, PageRequest.of(0, 10)
            )

            result.content.map { it.id } shouldContain english.id!!
            result.content.map { it.id } shouldNotContain japanese.id!!
        }

        it("trustedLevel 필터로 해당 레벨만 조회된다.") {
            val creator = userRepository.save(
                UserFixture.create(username = "pvlevel1", email = "pvlevel1@test.com")
            )
            val official = sharedWordbookRepository.save(
                SharedWordbookFixture.create(creatorId = creator.id!!, trustedLevel = TrustedLevel.OFFICIAL)
            ).let { sharedWordbookRepository.save(it.updateSlug()) }
            sharedWordbookRevisionRepository.save(SharedWordbookRevisionFixture.create(version = 1L), official.id!!)

            val basic = sharedWordbookRepository.save(
                SharedWordbookFixture.create(creatorId = creator.id!!, trustedLevel = TrustedLevel.BASIC)
            ).let { sharedWordbookRepository.save(it.updateSlug()) }
            sharedWordbookRevisionRepository.save(SharedWordbookRevisionFixture.create(version = 1L), basic.id!!)

            val result = sharedWordbookRepository.paginatePublicViews(
                null, listOf(TrustedLevel.OFFICIAL), SharedWordbookSortType.CREATED_AT_DESC, PageRequest.of(0, 10)
            )

            result.content.map { it.id } shouldContain official.id!!
            result.content.map { it.id } shouldNotContain basic.id!!
            result.content.all { it.trustedLevel == TrustedLevel.OFFICIAL } shouldBe true
        }

        it("trustedLevel 필터가 비어있으면 모든 레벨을 조회한다.") {
            val creator = userRepository.save(
                UserFixture.create(username = "pvlevel2", email = "pvlevel2@test.com")
            )
            val official = sharedWordbookRepository.save(
                SharedWordbookFixture.create(creatorId = creator.id!!, trustedLevel = TrustedLevel.OFFICIAL)
            ).let { sharedWordbookRepository.save(it.updateSlug()) }
            sharedWordbookRevisionRepository.save(SharedWordbookRevisionFixture.create(version = 1L), official.id!!)

            val basic = sharedWordbookRepository.save(
                SharedWordbookFixture.create(creatorId = creator.id!!, trustedLevel = TrustedLevel.BASIC)
            ).let { sharedWordbookRepository.save(it.updateSlug()) }
            sharedWordbookRevisionRepository.save(SharedWordbookRevisionFixture.create(version = 1L), basic.id!!)

            val result = sharedWordbookRepository.paginatePublicViews(
                null, emptyList(), SharedWordbookSortType.CREATED_AT_DESC, PageRequest.of(0, 10)
            )

            result.content.map { it.id } shouldContain official.id!!
            result.content.map { it.id } shouldContain basic.id!!
        }

        it("페이지네이션이 동작한다.") {
            val creator = userRepository.save(
                UserFixture.create(username = "pvpage1", email = "pvpage1@test.com")
            )
            repeat(5) {
                val wb = sharedWordbookRepository.save(SharedWordbookFixture.create(creatorId = creator.id!!))
                    .let { sharedWordbookRepository.save(it.updateSlug()) }
                sharedWordbookRevisionRepository.save(SharedWordbookRevisionFixture.create(version = 1L), wb.id!!)
            }

            val page1 = sharedWordbookRepository.paginatePublicViews(
                null, emptyList(), SharedWordbookSortType.CREATED_AT_DESC, PageRequest.of(0, 3)
            )
            val page2 = sharedWordbookRepository.paginatePublicViews(
                null, emptyList(), SharedWordbookSortType.CREATED_AT_DESC, PageRequest.of(1, 3)
            )

            page1.content.size shouldBe 3
            page2.content.size shouldBe 2
            page1.totalElements shouldBe 5
        }

        it("조회된 단어장에 creator 정보와 최신 버전이 포함된다.") {
            val creator = userRepository.save(
                UserFixture.create(username = "pvinfo1", email = "pvinfo1@test.com")
            )
            val wb = sharedWordbookRepository.save(SharedWordbookFixture.create(creatorId = creator.id!!))
                .let { sharedWordbookRepository.save(it.updateSlug()) }
            sharedWordbookRevisionRepository.save(SharedWordbookRevisionFixture.create(version = 1L), wb.id!!)
            sharedWordbookRevisionRepository.save(SharedWordbookRevisionFixture.create(version = 2L), wb.id!!)

            val result = sharedWordbookRepository.paginatePublicViews(
                null, emptyList(), SharedWordbookSortType.CREATED_AT_DESC, PageRequest.of(0, 10)
            )

            val view = result.content.first { it.id == wb.id!! }
            view.creator.id shouldBe creator.id
            view.latestVersion shouldBe 2L
            view.slug.endsWith("-${wb.id}") shouldBe true
        }
    }

    describe("findDetailView") {
        it("공유 단어장의 상세 정보를 조회한다.") {
            val creator = userRepository.save(
                UserFixture.create(username = "detview1", email = "detview1@test.com")
            )
            val saved = sharedWordbookRepository.save(
                SharedWordbookFixture.create(
                    creatorId = creator.id!!,
                    title = "상세 조회 단어장",
                    description = "단어장 설명입니다",
                )
            ).let { sharedWordbookRepository.save(it.updateSlug()) }
            val revision = sharedWordbookRevisionRepository.save(
                SharedWordbookRevisionFixture.create(version = 1L, versionNote = "초기 버전"), saved.id!!
            )

            val detail = sharedWordbookRepository.findDetailView(saved.id!!)

            detail shouldNotBe null
            detail!!.id shouldBe saved.id
            detail.title shouldBe "상세 조회 단어장"
            detail.description shouldBe "단어장 설명입니다"
            detail.creator.id shouldBe creator.id
            detail.latestVersion shouldBe 1L
            detail.latestRevisionId shouldBe revision.id!!
        }

        it("버전이 여러 개일 때 가장 높은 버전 정보를 반환한다.") {
            val creator = userRepository.save(
                UserFixture.create(username = "detview2", email = "detview2@test.com")
            )
            val saved = sharedWordbookRepository.save(SharedWordbookFixture.create(creatorId = creator.id!!))
                .let { sharedWordbookRepository.save(it.updateSlug()) }
            sharedWordbookRevisionRepository.save(SharedWordbookRevisionFixture.create(version = 1L), saved.id!!)
            sharedWordbookRevisionRepository.save(SharedWordbookRevisionFixture.create(version = 2L), saved.id!!)
            val latest = sharedWordbookRevisionRepository.save(
                SharedWordbookRevisionFixture.create(version = 3L), saved.id!!
            )

            val detail = sharedWordbookRepository.findDetailView(saved.id!!)

            detail!!.latestVersion shouldBe 3L
            detail.latestRevisionId shouldBe latest.id!!
        }

        it("존재하지 않는 ID로 조회하면 null을 반환한다.") {
            sharedWordbookRepository.findDetailView(999999L) shouldBe null
        }
    }
})
