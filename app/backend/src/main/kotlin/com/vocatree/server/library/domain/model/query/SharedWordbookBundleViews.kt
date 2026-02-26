package com.vocatree.server.library.domain.model.query

import com.vocatree.server.common.domain.model.query.SimpleUserView
import com.vocatree.server.library.domain.model.enumerated.SharedState
import com.vocatree.server.library.domain.model.enumerated.TrustedLevel
import java.time.LocalDateTime

data class SharedWordbookBundleListView(
    val id: Long,
    val title: String,
    val slug: String,
    val sharedState: SharedState,
    val trustedLevel: TrustedLevel,
    val creator: SimpleUserView,
    val wordbookCount: Int,
    val createdAt: LocalDateTime,
)

data class SharedWordbookBundleDetailView(
    val id: Long,
    val title: String,
    val slug: String,
    val description: String?,
    val sharedState: SharedState,
    val trustedLevel: TrustedLevel,
    val creator: SimpleUserView,
    val createdAt: LocalDateTime,
    val wordbookCount: Int,
)
