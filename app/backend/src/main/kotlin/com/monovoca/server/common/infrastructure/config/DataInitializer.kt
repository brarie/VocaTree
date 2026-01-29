package com.monovoca.server.common.infrastructure.config

import com.monovoca.server.common.domain.model.vo.EncryptedPassword
import com.monovoca.server.common.domain.model.vo.enumerated.UserRole
import com.monovoca.server.common.domain.model.vo.Username
import com.monovoca.server.supportBoard.application.SupportCategoryService
import com.monovoca.server.supportBoard.application.SupportPostService
import com.monovoca.server.user.domain.model.User
import com.monovoca.server.user.domain.model.vo.Email
import com.monovoca.server.user.domain.model.vo.Nickname
import com.monovoca.server.user.domain.port.UserCommandRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(
    prefix = "app.data",
    name = ["init-enabled"],
    havingValue = "true",
    matchIfMissing = false
)
class DataInitializer(
    private val userCommandRepository: UserCommandRepository,
    private val passwordEncoder: PasswordEncoder,
    private val supportCategoryService: SupportCategoryService,
    private val supportPostService: SupportPostService,
) : ApplicationRunner {

    private val klogger = KotlinLogging.logger {}

    override fun run(args: ApplicationArguments) {
        klogger.info { "===== 초기 데이터 삽입 시작 =====" }

        transaction {
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
            adminAccount = userCommandRepository.save(adminAccount)
            userAccount = userCommandRepository.save(userAccount)


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
                "문의는 monovoca0@gmail.com 으로 보내주시기 바랍니다.",
                sub6.id!!
            )

        }

        klogger.info { "===== 초기 데이터 삽입 완료 =====" }
    }
}
