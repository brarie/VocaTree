package com.vocatree.server.supportBoard.domain.port

import com.vocatree.server.supportBoard.domain.model.SupportPost
import com.vocatree.server.supportBoard.domain.model.query.SupportPostView
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface SupportPostRepository {

    fun save(supportPost: SupportPost): SupportPost
    fun delete(supportPost: SupportPost)
    fun findById(supportPostId: Long): SupportPost?

    /**
     * 대분류가 들어오면 대분류 아래의 중분류 게시글 전체를 가져옵니다.
     * 만든 시간을 기준으로 내림차순 정렬합니다.
    */
    fun paginateViewsByCategoryTree(categoryId: Long, pageable: Pageable): Page<SupportPostView>
}