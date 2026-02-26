package com.vocatree.server.library.domain.port

import com.vocatree.server.library.domain.model.SharedWordbookBundle
import com.vocatree.server.library.domain.model.enumerated.TrustedLevel
import com.vocatree.server.library.domain.model.query.SharedWordbookBundleDetailView
import com.vocatree.server.library.domain.model.query.SharedWordbookBundleListView
import com.vocatree.server.library.domain.model.query.SharedWordbookListView
import com.vocatree.server.library.domain.port.options.SharedWordbookBundleSortType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface SharedWordbookBundleRepository {
    fun findById(bundleId: Long): SharedWordbookBundle?
    fun save(bundle: SharedWordbookBundle): SharedWordbookBundle
    fun paginatePublicViews(
        titlePrefix: String?,
        trustedLevels: List<TrustedLevel>,
        sortType: SharedWordbookBundleSortType,
        pageable: Pageable,
    ): Page<SharedWordbookBundleListView>
    fun findDetailView(bundleId: Long): SharedWordbookBundleDetailView?
    fun addWordbook(bundleId: Long, sharedWordbookId: Long)
    fun removeWordbook(bundleId: Long, sharedWordbookId: Long)
    fun paginateWordbooksInBundle(bundleId: Long, pageable: Pageable): Page<SharedWordbookListView>
}