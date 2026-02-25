package com.vocatree.server.library.domain.port

import com.vocatree.server.common.domain.model.vocabulary.vo.components.Spelling
import com.vocatree.server.library.fixture.SharedWordbookFixture
import com.vocatree.server.library.fixture.SharedWordbookRevisionFixture
import com.vocatree.server.library.fixture.SharedWordSnapshotFixture
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
class SharedWordSnapshotRepositoryTest(
    private val sharedWordSnapshotRepository: SharedWordSnapshotRepository,
    private val sharedWordbookRepository: SharedWordbookRepository,
    private val sharedWordbookRevisionRepository: SharedWordbookRevisionRepository,
    private val userRepository: UserRepository,
) : DescribeSpec({

    describe("findOrCreate") {
        it("새로운 스냅샷을 저장하면 ID가 할당된다.") {
            val snapshot = SharedWordSnapshotFixture.create()

            val saved = sharedWordSnapshotRepository.findOrCreate(snapshot)

            saved.id shouldNotBe null
            saved.spelling shouldBe snapshot.spelling
            saved.displayOrder shouldBe snapshot.displayOrder
            saved.senses.size shouldBe 1
        }

        it("동일한 내용의 스냅샷을 다시 저장하면 기존 스냅샷을 반환한다 (COW).") {
            val snapshot = SharedWordSnapshotFixture.create()

            val first = sharedWordSnapshotRepository.findOrCreate(snapshot)
            val second = sharedWordSnapshotRepository.findOrCreate(snapshot)

            second.id shouldBe first.id
        }

        it("내용이 다른 스냅샷은 별도로 저장된다.") {
            val snapshot1 = SharedWordSnapshotFixture.create(spelling = Spelling("apple"))
            val snapshot2 = SharedWordSnapshotFixture.create(spelling = Spelling("banana"))

            val saved1 = sharedWordSnapshotRepository.findOrCreate(snapshot1)
            val saved2 = sharedWordSnapshotRepository.findOrCreate(snapshot2)

            saved1.id shouldNotBe saved2.id
            saved1.spelling shouldBe Spelling("apple")
            saved2.spelling shouldBe Spelling("banana")
        }

        it("displayOrder 가 다르면 다른 스냅샷으로 취급된다.") {
            val snapshot1 = SharedWordSnapshotFixture.create(displayOrder = 1)
            val snapshot2 = SharedWordSnapshotFixture.create(displayOrder = 2)

            val saved1 = sharedWordSnapshotRepository.findOrCreate(snapshot1)
            val saved2 = sharedWordSnapshotRepository.findOrCreate(snapshot2)

            saved1.id shouldNotBe saved2.id
        }

        it("senses 내용이 다르면 다른 스냅샷으로 취급된다.") {
            val snapshot1 = SharedWordSnapshotFixture.create(
                senses = listOf(SharedWordSnapshotFixture.createSense(definition = "사과"))
            )
            val snapshot2 = SharedWordSnapshotFixture.create(
                senses = listOf(SharedWordSnapshotFixture.createSense(definition = "애플"))
            )

            val saved1 = sharedWordSnapshotRepository.findOrCreate(snapshot1)
            val saved2 = sharedWordSnapshotRepository.findOrCreate(snapshot2)

            saved1.id shouldNotBe saved2.id
        }
    }

    describe("findByRevisionId") {
        it("리비전에 연결된 스냅샷 목록을 조회한다.") {
            val creator = userRepository.save(
                UserFixture.create(username = "swsfind1", email = "swsfind1@test.com")
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
                SharedWordSnapshotFixture.create(spelling = Spelling("banana"), displayOrder = 2)
            )
            sharedWordbookRevisionRepository.saveEntries(revision.id!!, listOf(snapshot1.id!!, snapshot2.id!!))

            val words = sharedWordSnapshotRepository.findByRevisionId(revision.id!!)

            words.size shouldBe 2
            words.map { it.id } shouldContainExactlyInAnyOrder listOf(snapshot1.id!!, snapshot2.id!!)
        }

        it("연결된 스냅샷이 없는 리비전은 빈 목록을 반환한다.") {
            val creator = userRepository.save(
                UserFixture.create(username = "swsfind2", email = "swsfind2@test.com")
            )
            val wordbook = sharedWordbookRepository.save(
                SharedWordbookFixture.create(creatorId = creator.id!!)
            )
            val revision = sharedWordbookRevisionRepository.save(
                SharedWordbookRevisionFixture.create(version = 1L), wordbook.id!!
            )

            val words = sharedWordSnapshotRepository.findByRevisionId(revision.id!!)

            words shouldBe emptyList()
        }
    }
})
