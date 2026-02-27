package com.vocatree.server.library.domain.port

import com.vocatree.server.library.domain.model.SharedWordbook
import com.vocatree.server.library.domain.model.enumerated.TrustedLevel
import com.vocatree.server.library.domain.model.query.SharedWordbookDetailView
import com.vocatree.server.library.domain.model.query.SharedWordbookListView
import com.vocatree.server.library.domain.port.options.SharedWordbookSortType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface SharedWordbookRepository {
    fun findById(sharedWordbookId: Long): SharedWordbook?
    fun save(sharedWordbook: SharedWordbook): SharedWordbook
    fun paginatePublicViews(
        titlePrefix: String?,
        trustedLevels: List<TrustedLevel>,
        sortType: SharedWordbookSortType,
        pageable: Pageable,
    ): Page<SharedWordbookListView>
    fun paginateAllViews(titlePrefix: String?, pageable: Pageable): Page<SharedWordbookListView>
    fun findDetailView(sharedWordbookId: Long): SharedWordbookDetailView?
    fun paginateByCreatorId(creatorId: String, pageable: Pageable): Page<SharedWordbookListView>
}
