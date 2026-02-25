package com.vocatree.server.library.domain.port

import com.vocatree.server.library.domain.model.enumerated.SharedState
import com.vocatree.server.library.domain.model.enumerated.TrustedLevel
import com.vocatree.server.library.fixture.SharedWordbookFixture
import com.vocatree.server.user.domain.port.UserRepository
import com.vocatree.server.user.fixture.UserFixture
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@ApplyExtension(SpringExtension::class)
@Transactional
class SharedWordbookRepositoryTest(
    private val sharedWordbookRepository: SharedWordbookRepository,
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
})
