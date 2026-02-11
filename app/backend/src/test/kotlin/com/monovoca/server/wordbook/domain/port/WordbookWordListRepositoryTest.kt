package com.monovoca.server.wordbook.domain.port

import com.monovoca.server.common.domain.model.vocabulary.vo.components.Spelling
import com.monovoca.server.user.domain.port.UserRepository
import com.monovoca.server.user.fixture.UserFixture
import com.monovoca.server.wordbook.domain.model.command.UpdateWordCommand
import com.monovoca.server.wordbook.fixture.WordbookFixture
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@ApplyExtension(SpringExtension::class)
@Transactional
class WordbookWordListRepositoryTest(
    private val wordbookWordListRepository: WordbookWordListRepository,
    private val wordbookRepository: WordbookRepository,
    private val userRepository: UserRepository,
) : DescribeSpec({

    describe("findById") {
        it("wordbookId로 WordList를 조회한다.") {
            val owner = userRepository.save(
                UserFixture.create(username = "wlrepo1", email = "wlrepo1@test.com")
            )
            val wordbook = wordbookRepository.save(
                WordbookFixture.create(ownerId = owner.id!!)
            )

            val wordList = wordbookWordListRepository.findById(wordbook.id!!)

            wordList.wordbookId shouldBe wordbook.id
            wordList.wordList shouldHaveSize 0
        }
    }

    describe("save") {
        it("새로운 단어를 추가하면 저장된다.") {
            val owner = userRepository.save(
                UserFixture.create(username = "wlrepo2", email = "wlrepo2@test.com")
            )
            val wordbook = wordbookRepository.save(
                WordbookFixture.create(ownerId = owner.id!!)
            )
            var wordList = wordbookWordListRepository.findById(wordbook.id!!)
            wordList = wordList.newWord()

            val saved = wordbookWordListRepository.save(wordList)

            saved.wordList shouldHaveSize 1
            saved.wordList[0].displayOrder shouldBe 1
        }

        it("단어를 수정하면 변경 사항이 반영된다.") {
            val owner = userRepository.save(
                UserFixture.create(username = "wlrepo3", email = "wlrepo3@test.com")
            )
            val wordbook = wordbookRepository.save(
                WordbookFixture.create(ownerId = owner.id!!)
            )
            var wordList = wordbookWordListRepository.findById(wordbook.id!!)
            wordList = wordList.newWord()
            wordList = wordbookWordListRepository.save(wordList)
            val wordId = wordList.wordList[0].id!!

            val command = UpdateWordCommand(spelling = Spelling("updated"))
            wordList = wordList.updateWord(wordId, command)
            val saved = wordbookWordListRepository.save(wordList)

            saved.wordList[0].spelling shouldBe Spelling("updated")
        }

        it("뜻을 추가하면 저장된다.") {
            val owner = userRepository.save(
                UserFixture.create(username = "wlrepo4", email = "wlrepo4@test.com")
            )
            val wordbook = wordbookRepository.save(
                WordbookFixture.create(ownerId = owner.id!!)
            )
            var wordList = wordbookWordListRepository.findById(wordbook.id!!)
            wordList = wordList.newWord()
            wordList = wordbookWordListRepository.save(wordList)
            val wordId = wordList.wordList[0].id!!

            wordList = wordList.newSense(wordId)
            val saved = wordbookWordListRepository.save(wordList)

            saved.wordList[0].senses shouldHaveSize 1
        }

        it("예문을 추가하면 저장된다.") {
            val owner = userRepository.save(
                UserFixture.create(username = "wlrepo5", email = "wlrepo5@test.com")
            )
            val wordbook = wordbookRepository.save(
                WordbookFixture.create(ownerId = owner.id!!)
            )
            var wordList = wordbookWordListRepository.findById(wordbook.id!!)
            wordList = wordList.newWord()
            wordList = wordbookWordListRepository.save(wordList)
            val wordId = wordList.wordList[0].id!!
            wordList = wordList.newSense(wordId)
            wordList = wordbookWordListRepository.save(wordList)
            val senseId = wordList.wordList[0].senses[0].id!!

            wordList = wordList.newExample(wordId, senseId)
            val saved = wordbookWordListRepository.save(wordList)

            saved.wordList[0].senses[0].examples shouldHaveSize 1
        }
    }
})
