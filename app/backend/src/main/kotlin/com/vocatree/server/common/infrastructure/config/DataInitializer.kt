package com.vocatree.server.common.infrastructure.config

import com.vocatree.server.common.domain.model.vo.EncryptedPassword
import com.vocatree.server.common.domain.model.vocabulary.vo.components.Spelling
import com.vocatree.server.common.domain.model.vo.Username
import com.vocatree.server.common.domain.model.vocabulary.vo.components.PartOfSpeech
import com.vocatree.server.common.domain.model.vo.enumerated.UserRole
import com.vocatree.server.dictionary.application.DictionaryWordService
import com.vocatree.server.dictionary.domain.model.word.command.CreateExampleCommand
import com.vocatree.server.dictionary.domain.model.word.command.CreateSenseCommand
import com.vocatree.server.supportBoard.application.SupportCategoryService
import com.vocatree.server.supportBoard.application.SupportPostService
import com.vocatree.server.user.domain.model.User
import com.vocatree.server.user.domain.model.vo.Email
import com.vocatree.server.user.domain.model.vo.Nickname
import com.vocatree.server.user.domain.port.UserRepository
import com.vocatree.server.wordbook.application.WordbookBundleService
import com.vocatree.server.wordbook.application.WordbookService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@ConditionalOnProperty(
    prefix = "app.data",
    name = ["init-enabled"],
    havingValue = "true",
    matchIfMissing = false
)
class DataInitializer(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val supportCategoryService: SupportCategoryService,
    private val supportPostService: SupportPostService,
    private val dictionaryWordService: DictionaryWordService,
    private val wordbookService: WordbookService,
    private val wordbookBundleService: WordbookBundleService
) : ApplicationRunner {

    private val klogger = KotlinLogging.logger {}

    @Transactional
    override fun run(args: ApplicationArguments) {
        klogger.info { "===== 초기 데이터 삽입 시작 =====" }
        /////////////////// 사용자 계정 초기화 ///////////////////
        var adminAccount = User(
            username = Username("admin"),
            email = Email("prarie77@gmail.com"),
            nickname = Nickname("prarie"),
            password = EncryptedPassword.encrypt("12121212", passwordEncoder),
            role = UserRole.ADMIN
        )
        var userAccount = User(
            username = Username("user"),
            email = Email("example@example.com"),
            nickname = Nickname("모노보카"),
            password = EncryptedPassword.encrypt("12121212", passwordEncoder),
            role = UserRole.USER
        )
        adminAccount = userRepository.save(adminAccount)
        userAccount = userRepository.save(userAccount)


        //////////////////// 카테고리 초기화 //////////////////////////
        val gongji = supportCategoryService.createMainCategory("공지사항")
        val faq = supportCategoryService.createMainCategory("FAQ")

        val sub1 = supportCategoryService.createSubCategory("신규 서비스 안내", gongji.id!!)
        val sub2 = supportCategoryService.createSubCategory("이벤트", gongji.id)
        val sub3 = supportCategoryService.createSubCategory("작업 안내", gongji.id)

        val sub4 = supportCategoryService.createSubCategory("회원", faq.id!!)
        val sub5 = supportCategoryService.createSubCategory("단어장", faq.id)
        val sub6 = supportCategoryService.createSubCategory("문의", faq.id)

        ///////////////// 게시글 초기화 ////////////////////////
        supportPostService.create(
            adminAccount.id!!,
            "서비스 오픈 안내",
            "모노보카 서비스가 오픈하였습니다. 많은 이용 부탁드립니다.",
            sub1.id!!
        )

        supportPostService.create(
            adminAccount.id,
            "단어장 공유 기능 추가",
            "단어장 공유 기능이 추가되었습니다. 이제 친구들과 단어장을 공유해보세요!",
            sub1.id
        )

        supportPostService.create(
            adminAccount.id,
            "회원 탈퇴 방법",
            "회원 탈퇴는 설정 페이지에서 가능합니다. 탈퇴 시 모든 데이터가 삭제되니 신중히 결정해주세요.",
            sub4.id!!
        )

        supportPostService.create(
            adminAccount.id,
            "문의 방법",
            "문의는 prarie77@gmail.com 으로 보내주시기 바랍니다.",
            sub6.id!!
        )

        /* 공용 사전 초기화 */
        val word1 = dictionaryWordService.createWord("exist")
        val word2 = dictionaryWordService.createWord("existence")
        val word3 = dictionaryWordService.createWord("apple")
        val word4 = dictionaryWordService.createWord("mango")

        val sense1 = dictionaryWordService.createSense(
            1,
            CreateSenseCommand(definition = "존재하다", partOfSpeech = PartOfSpeech.VERB)
        )
        val sense2 = dictionaryWordService.createSense(
            1, CreateSenseCommand(
                definition = "생존하다",
                partOfSpeech = PartOfSpeech.VERB,
                engDefinition = "to live or continue to be alive",
                britishPhonetic = "ɪɡˈzɪst",
                americanPhonetic = "ɪɡˈzɪst",
                synonyms = listOf(Spelling("live"), Spelling("survive")),
                antonyms = listOf(Spelling("perish"), Spelling("die"))
            )
        )
        val ex1 = dictionaryWordService.createExample(
            1,
            1,
            CreateExampleCommand(
                sentence = "God exists.",
                translation = "신은 존재한다.",
                isOfficial = true,
                sourceInfo = "Bible"
            )
        )
        val ex2 = dictionaryWordService.createExample(
            1,
            1,
            CreateExampleCommand(
                sentence = "I want to exist in this world.",
                translation = "나는 이 세상에 존재하고 싶다.",
                isOfficial = false,
                sourceInfo = "Literature"
            )
        )
        val ex3 = dictionaryWordService.createExample(
            1,
            2,
            CreateExampleCommand(
                sentence = "Dinosaurs existed millions of years ago.",
                translation = "공룡은 수백만 년 전에 존재했다.",
                isOfficial = false,
                sourceInfo = "Science Book"
            )
        )

        val sense3 = dictionaryWordService.createSense(
            2,
            CreateSenseCommand(definition = "존재", partOfSpeech = PartOfSpeech.NOUN)
        )
        val sense4 = dictionaryWordService.createSense(
            2, CreateSenseCommand(
                definition = "실재",
                partOfSpeech = PartOfSpeech.NOUN,
                engDefinition = "the state or fact of being real",
                britishPhonetic = "ɪɡˈzɪstəns",
                americanPhonetic = "ɪɡˈzɪstəns",
                synonyms = listOf(Spelling("reality"), Spelling("being")),
                antonyms = listOf(Spelling("nonexistence"), Spelling("void"))
            )
        )

        val ex4 = dictionaryWordService.createExample(
            2,
            3,
            CreateExampleCommand(
                sentence = "The existence of life on other planets is still unknown.",
                translation = "다른 행성에 생명체의 존재는 아직 알려지지 않았다.",
                isOfficial = false,
                sourceInfo = "Science Journal"
            )
        )
        val ex5 = dictionaryWordService.createExample(
            2,
            4,
            CreateExampleCommand(
                sentence = "Her existence was a mystery to everyone.",
                translation = "그녀의 존재는 모두에게 미스터리였다.",
                isOfficial = false,
                sourceInfo = "Novel"
            )
        )
        val sense5 = dictionaryWordService.createSense(
            3,
            CreateSenseCommand(definition = "사과", partOfSpeech = PartOfSpeech.NOUN)
        )
        val ex6 = dictionaryWordService.createExample(
            3,
            5,
            CreateExampleCommand(
                sentence = "An apple a day keeps the doctor away.",
                translation = "하루에 사과 하나면 의사를 멀리한다.",
                isOfficial = true,
                sourceInfo = "Proverb"
            )
        )
        val sense6 = dictionaryWordService.createSense(
            4,
            CreateSenseCommand(definition = "망고", partOfSpeech = PartOfSpeech.NOUN)
        )
        val ex7 = dictionaryWordService.createExample(
            4,
            6,
            CreateExampleCommand(
                sentence = "Mangoes are my favorite fruit.",
                translation = "망고는 내가 가장 좋아하는 과일이다.",
                isOfficial = false,
                sourceInfo = "Personal Statement"
            )
        )

        /* Wordbook & Wordbook Bundle 초기화 */
        val b1 = wordbookBundleService.create(adminAccount.id, "영어 기초 학습 모음집")
        val b2 = wordbookBundleService.create(adminAccount.id, "비즈니스 영어 완전 정복 시리즈")
        val b3 = wordbookBundleService.create(adminAccount.id, "TOEIC 900점 목표 단어 모음")

        val w1 = wordbookService.create(adminAccount.id, "TOEIC 900점 단어 - 숙어")
        val w2 = wordbookService.create(adminAccount.id, "TOEIC 900점 단어 - 자주 출제되는 단어들")
        val w3 = wordbookService.create(adminAccount.id, "TOEIC 900점 단어 - 필수 암기 단어")
        val w4 = wordbookService.create(adminAccount.id, "비즈니스 이메일 작성 필수 단어")
        val w5 = wordbookService.create(adminAccount.id, "비즈니스 영어 100단어")
        val w6 = wordbookService.create(adminAccount.id, "판타지 소설 단어")
        val w7 = wordbookService.create(adminAccount.id, "과학 논문 단어")

        wordbookService.move(adminAccount.id, w1.id!!, b3.id)
        wordbookService.move(adminAccount.id, w2.id!!, b3.id)
        wordbookService.move(adminAccount.id, w3.id!!, b3.id)
        wordbookService.move(adminAccount.id, w4.id!!, b2.id)
        wordbookService.move(adminAccount.id, w5.id!!, b2.id)

        klogger.info { "===== 초기 데이터 삽입 완료 =====" }
    }
}
