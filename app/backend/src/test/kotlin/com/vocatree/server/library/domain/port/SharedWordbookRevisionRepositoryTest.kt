package com.vocatree.server.library.domain.port

import com.vocatree.server.common.domain.model.vocabulary.vo.components.Spelling
import com.vocatree.server.library.fixture.SharedWordbookFixture
import com.vocatree.server.library.fixture.SharedWordSnapshotFixture
import com.vocatree.server.library.fixture.SharedWordbookRevisionFixture
import com.vocatree.server.user.domain.port.UserRepository
import com.vocatree.server.user.fixture.UserFixture
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@ApplyExtension(SpringExtension::class)
@Transactional
class SharedWordbookRevisionRepositoryTest(
    private val sharedWordbookRepository: SharedWordbookRepository,
    private val sharedWordbookRevisionRepository: SharedWordbookRevisionRepository,
    private val sharedWordSnapshotRepository: SharedWordSnapshotRepository,
    private val userRepository: UserRepository,
) : DescribeSpec({

    describe("save") {
        it("새로운 리비전을 저장하면 ID가 할당된다.") {
            val creator = userRepository.save(
                UserFixture.create(username = "swrsave1", email = "swrsave1@test.com")
            )
            val wordbook = sharedWordbookRepository.save(
                SharedWordbookFixture.create(creatorId = creator.id!!)
            )
            val revision = SharedWordbookRevisionFixture.create(version = 1L, versionNote = "첫 번째 배포")

            val saved = sharedWordbookRevisionRepository.save(revision, wordbook.id!!)

            saved.id shouldNotBe null
            saved.version shouldBe 1L
            saved.versionNote shouldBe "첫 번째 배포"
        }

        it("버전 노트 없이 리비전을 저장할 수 있다.") {
            val creator = userRepository.save(
                UserFixture.create(username = "swrsave2", email = "swrsave2@test.com")
            )
            val wordbook = sharedWordbookRepository.save(
                SharedWordbookFixture.create(creatorId = creator.id!!)
            )
            val revision = SharedWordbookRevisionFixture.create(version = 1L, versionNote = null)

            val saved = sharedWordbookRevisionRepository.save(revision, wordbook.id!!)

            saved.id shouldNotBe null
            saved.versionNote shouldBe null
        }
    }

    describe("findLatestByWordbookId") {
        it("가장 최신 버전의 리비전을 조회한다.") {
            val creator = userRepository.save(
                UserFixture.create(username = "swrfind1", email = "swrfind1@test.com")
            )
            val wordbook = sharedWordbookRepository.save(
                SharedWordbookFixture.create(creatorId = creator.id!!)
            )
            sharedWordbookRevisionRepository.save(SharedWordbookRevisionFixture.create(version = 1L), wordbook.id!!)
            sharedWordbookRevisionRepository.save(SharedWordbookRevisionFixture.create(version = 2L), wordbook.id!!)
            sharedWordbookRevisionRepository.save(SharedWordbookRevisionFixture.create(version = 3L), wordbook.id!!)

            val latest = sharedWordbookRevisionRepository.findLatestByWordbookId(wordbook.id!!)

            latest shouldNotBe null
            latest!!.version shouldBe 3L
        }

        it("리비전이 없는 단어장을 조회하면 null을 반환한다.") {
            val creator = userRepository.save(
                UserFixture.create(username = "swrfind2", email = "swrfind2@test.com")
            )
            val wordbook = sharedWordbookRepository.save(
                SharedWordbookFixture.create(creatorId = creator.id!!)
            )

            val latest = sharedWordbookRevisionRepository.findLatestByWordbookId(wordbook.id!!)

            latest shouldBe null
        }
    }

    describe("saveEntries") {
        it("리비전에 연결된 스냅샷을 findByRevisionId로 조회할 수 있다.") {
            val creator = userRepository.save(
                UserFixture.create(username = "swrentry1", email = "swrentry1@test.com")
            )
            val wordbook = sharedWordbookRepository.save(
                SharedWordbookFixture.create(creatorId = creator.id!!)
            )
            val revision = sharedWordbookRevisionRepository.save(
                SharedWordbookRevisionFixture.create(version = 1L), wordbook.id!!
            )
            val snapshot1 = sharedWordSnapshotRepository.findOrCreate(
                SharedWordSnapshotFixture.create(displayOrder = 1)
            )
            val snapshot2 = sharedWordSnapshotRepository.findOrCreate(
                SharedWordSnapshotFixture.create(
                    spelling = Spelling("banana"),
                    displayOrder = 2,
                    senses = listOf(
                        SharedWordSnapshotFixture.createSense(definition = "바나나")
                    )
                )
            )

            sharedWordbookRevisionRepository.saveEntries(revision.id!!, listOf(snapshot1.id!!, snapshot2.id!!))

            val words = sharedWordSnapshotRepository.findByRevisionId(revision.id!!)
            words.size shouldBe 2
            words.map { it.id } shouldContainExactlyInAnyOrder listOf(snapshot1.id!!, snapshot2.id!!)
        }
    }

    describe("findByWordbookIdAndVersion") {
        it("단어장 ID와 버전으로 리비전을 조회한다.") {
            val creator = userRepository.save(
                UserFixture.create(username = "swrver1", email = "swrver1@test.com")
            )
            val wordbook = sharedWordbookRepository.save(
                SharedWordbookFixture.create(creatorId = creator.id!!)
            )
            sharedWordbookRevisionRepository.save(SharedWordbookRevisionFixture.create(version = 1L), wordbook.id!!)
            sharedWordbookRevisionRepository.save(SharedWordbookRevisionFixture.create(version = 2L), wordbook.id!!)

            val found = sharedWordbookRevisionRepository.findByWordbookIdAndVersion(wordbook.id!!, 2L)

            found shouldNotBe null
            found!!.version shouldBe 2L
        }

        it("존재하지 않는 버전으로 조회하면 null을 반환한다.") {
            val creator = userRepository.save(
                UserFixture.create(username = "swrver2", email = "swrver2@test.com")
            )
            val wordbook = sharedWordbookRepository.save(
                SharedWordbookFixture.create(creatorId = creator.id!!)
            )
            sharedWordbookRevisionRepository.save(SharedWordbookRevisionFixture.create(version = 1L), wordbook.id!!)

            val found = sharedWordbookRevisionRepository.findByWordbookIdAndVersion(wordbook.id!!, 99L)

            found shouldBe null
        }
    }
})
