package com.vocatree.server.wordbook.application

import com.vocatree.server.wordbook.domain.model.query.NodeResponseWithCountView
import com.vocatree.server.wordbook.domain.port.WordbookNodeRepository
import com.vocatree.server.wordbook.domain.port.options.WordbookNodeSortType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class WordbookNodeService(
    private val wordbookNodeRepository: WordbookNodeRepository,
) {

    @Transactional(readOnly = true)
    fun paginateNodesWithoutParent(
        userId: String,
        pageable: Pageable,
        wordbookNodeSortType: WordbookNodeSortType,
        starOnly: Boolean
    ): Page<NodeResponseWithCountView> =
        wordbookNodeRepository.paginateWithoutParent(userId, pageable, wordbookNodeSortType, starOnly)

    @Transactional(readOnly = true)
    fun paginateNodesInBundle(userId: String, bundleId: Long, pageable: Pageable): Page<NodeResponseWithCountView> =
        wordbookNodeRepository.paginateInBundle(userId, bundleId, pageable)
}