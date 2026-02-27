package com.vocatree.server.common.infrastructure.config

import com.vocatree.server.common.domain.model.vo.EncryptedPassword
import com.vocatree.server.common.domain.model.vo.Username
import com.vocatree.server.common.domain.model.vo.enumerated.UserRole
import com.vocatree.server.common.domain.model.vocabulary.vo.components.PartOfSpeech
import com.vocatree.server.common.domain.model.vocabulary.vo.components.Spelling
import com.vocatree.server.dictionary.application.DictionaryWordService
import com.vocatree.server.dictionary.domain.model.word.command.CreateExampleCommand
import com.vocatree.server.dictionary.domain.model.word.command.CreateSenseCommand
import com.vocatree.server.library.application.SharedWordbookBundleService
import com.vocatree.server.library.application.SharedWordbookService
import com.vocatree.server.library.domain.model.enumerated.TrustedLevel
import com.vocatree.server.supportBoard.application.SupportCategoryService
import com.vocatree.server.supportBoard.application.SupportPostService
import com.vocatree.server.user.domain.model.User
import com.vocatree.server.user.domain.model.vo.Email
import com.vocatree.server.user.domain.model.vo.Nickname
import com.vocatree.server.user.domain.port.UserRepository
import com.vocatree.server.wordbook.application.WordbookBundleService
import com.vocatree.server.wordbook.application.WordbookService
import com.vocatree.server.wordbook.application.WordbookWordListService
import com.vocatree.server.wordbook.domain.model.command.UpdateSenseCommand
import com.vocatree.server.wordbook.domain.model.command.UpdateWordCommand
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
    private val wordbookWordListService: WordbookWordListService,
    private val wordbookBundleService: WordbookBundleService,
    private val sharedWordbookService: SharedWordbookService,
    private val sharedWordbookBundleService: SharedWordbookBundleService,
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
            nickname = Nickname("보카트리"),
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
        val w1_word1 = wordbookWordListService.createWordSeries(adminAccount.id, w1.id!!)
        wordbookWordListService.updateWord(adminAccount.id, w1.id, w1_word1.wordList[0].id!!, UpdateWordCommand(Spelling("look up")))
        wordbookWordListService.updateSense(adminAccount.id, w1.id, w1_word1.wordList[0].id!!, w1_word1.wordList[0].senses[0].id!!,
            UpdateSenseCommand(
                definition = "찾아보다",
                partOfSpeech = PartOfSpeech.IDIOM,
                engDefinition = "to search for information in a reference book or database",
                britishPhonetic = "lʊk ʌp",
                americanPhonetic = "lʊk ʌp",
                synonyms = listOf(Spelling("search"), Spelling("consult")),
                antonyms = listOf(Spelling("ignore"), Spelling("overlook"))
            )
        )
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

        /* 공유 단어장 초기화 */
        val userW1 = wordbookService.create(userAccount.id!!, "일상 회화 표현")
        val userW2 = wordbookService.create(userAccount.id, "영화 속 영어 표현")

        val sw1 = sharedWordbookService.shareOwnWordbook(
            userId = adminAccount.id!!,
            title = "TOEIC 900점 숙어 모음",
            description = "TOEIC에서 자주 출제되는 숙어 표현을 정리한 단어장입니다.",
            myWordbookId = w1.id!!,
            versionNote = "초기 버전",
        )
        sharedWordbookService.updateTrustedLevel(sw1.id!!, TrustedLevel.OFFICIAL)

        val sw2 = sharedWordbookService.shareOwnWordbook(
            userId = adminAccount.id,
            title = "TOEIC 자주 출제 단어",
            description = "TOEIC 시험에서 빈출되는 필수 단어를 모았습니다.",
            myWordbookId = w2.id!!,
            versionNote = "첫 번째 배포",
        )
        sharedWordbookService.updateTrustedLevel(sw2.id!!, TrustedLevel.OFFICIAL)

        val sw3 = sharedWordbookService.shareOwnWordbook(
            userId = adminAccount.id,
            title = "TOEIC 필수 암기 단어",
            description = "TOEIC 목표 점수 달성을 위한 필수 암기 단어 모음입니다.",
            myWordbookId = w3.id!!,
            versionNote = "v1",
        )
        sharedWordbookService.updateTrustedLevel(sw3.id!!, TrustedLevel.VERIFIED)

        val sw4 = sharedWordbookService.shareOwnWordbook(
            userId = adminAccount.id,
            title = "비즈니스 이메일 필수 단어",
            description = "업무 이메일 작성 시 활용할 수 있는 비즈니스 영어 단어 모음입니다.",
            myWordbookId = w4.id!!,
            versionNote = "초기 배포",
        )
        sharedWordbookService.updateTrustedLevel(sw4.id!!, TrustedLevel.VERIFIED)

        val sw5 = sharedWordbookService.shareOwnWordbook(
            userId = adminAccount.id,
            title = "비즈니스 영어 핵심 100단어",
            description = "직장인이라면 꼭 알아야 할 비즈니스 영어 100단어입니다.",
            myWordbookId = w5.id!!,
            versionNote = "v1.0",
        )

        val sw6 = sharedWordbookService.shareOwnWordbook(
            userId = adminAccount.id,
            title = "판타지 소설 단어 모음",
            description = "판타지 장르 소설에서 자주 등장하는 영어 단어들을 모았습니다.",
            myWordbookId = w6.id!!,
            versionNote = "초기 버전",
        )

        val sw7 = sharedWordbookService.shareOwnWordbook(
            userId = adminAccount.id,
            title = "과학 논문 영어 단어",
            description = "영어 과학 논문을 읽기 위한 전문 어휘를 수록했습니다.",
            myWordbookId = w7.id!!,
            versionNote = "v1",
        )

        val sw8 = sharedWordbookService.shareOwnWordbook(
            userId = userAccount.id,
            title = "일상 회화 영어 표현",
            description = "실생활에서 자주 쓰이는 영어 회화 표현을 모았습니다.",
            myWordbookId = userW1.id!!,
            versionNote = "첫 배포",
        )

        val sw9 = sharedWordbookService.shareOwnWordbook(
            userId = userAccount.id,
            title = "영화로 배우는 영어 표현",
            description = "미국 영화에서 자주 나오는 생생한 영어 표현 모음입니다.",
            myWordbookId = userW2.id!!,
            versionNote = "v1",
        )

        // sw1에 새 버전 추가
        sharedWordbookService.makeNewVersion(
            userId = adminAccount.id,
            sharedWordbookId = sw1.id!!,
            myWordbookId = w1.id,
            versionNote = "단어 추가 업데이트",
        )

        /* 공유 단어장 번들 초기화 */
        val bundle1 = sharedWordbookBundleService.makeSharedBundle(
            userId = adminAccount.id,
            title = "TOEIC 완전 정복 시리즈",
            description = "TOEIC 고득점을 위한 단어장 모음입니다. 숙어부터 빈출 단어까지 체계적으로 학습하세요.",
        )
        sharedWordbookBundleService.updateTrustedLevel(bundle1.id!!, TrustedLevel.OFFICIAL)
        sharedWordbookBundleService.addSharedWordbookToBundle(adminAccount.id, bundle1.id!!, sw1.id!!)
        sharedWordbookBundleService.addSharedWordbookToBundle(adminAccount.id, bundle1.id!!, sw2.id!!)
        sharedWordbookBundleService.addSharedWordbookToBundle(adminAccount.id, bundle1.id!!, sw3.id!!)

        val bundle2 = sharedWordbookBundleService.makeSharedBundle(
            userId = adminAccount.id,
            title = "비즈니스 영어 마스터",
            description = "직장에서 바로 쓸 수 있는 비즈니스 영어 단어장 모음입니다.",
        )
        sharedWordbookBundleService.updateTrustedLevel(bundle2.id!!, TrustedLevel.VERIFIED)
        sharedWordbookBundleService.addSharedWordbookToBundle(adminAccount.id, bundle2.id!!, sw4.id!!)
        sharedWordbookBundleService.addSharedWordbookToBundle(adminAccount.id, bundle2.id!!, sw5.id!!)

        val bundle3 = sharedWordbookBundleService.makeSharedBundle(
            userId = adminAccount.id,
            title = "다양한 주제 영어 단어 컬렉션",
            description = "일상, 과학, 문학 등 다양한 분야의 영어 단어장을 한곳에 모았습니다.",
        )
        sharedWordbookBundleService.addSharedWordbookToBundle(adminAccount.id, bundle3.id!!, sw6.id!!)
        sharedWordbookBundleService.addSharedWordbookToBundle(adminAccount.id, bundle3.id!!, sw7.id!!)
        sharedWordbookBundleService.addSharedWordbookToBundle(adminAccount.id, bundle3.id!!, sw8.id!!)
        sharedWordbookBundleService.addSharedWordbookToBundle(adminAccount.id, bundle3.id!!, sw9.id!!)

//        sharedWordbookService.banSharedWordbook(sw8.id!!)
//        sharedWordbookService.deleteMySharedWordbook(userAccount.id, sw9.id!!)

        klogger.info { "===== 초기 데이터 삽입 완료 =====" }
    }
}
