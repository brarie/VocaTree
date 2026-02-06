package com.monovoca.server.wordbook.domain.port

import com.monovoca.server.wordbook.domain.model.query.NodeResponseWithCountView
import com.monovoca.server.wordbook.domain.port.options.WordbookNodeSortType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface WordbookNodeRepository {

    fun paginateWithoutParent(
        ownerId: String,
        pageable: Pageable,
        wordbookNodeSortType: WordbookNodeSortType,
        starOnly: Boolean
    ): Page<NodeResponseWithCountView>

    fun paginateInBundle(ownerId: String, bundleId: Long, pageable: Pageable): Page<NodeResponseWithCountView>
}