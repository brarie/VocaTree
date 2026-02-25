package com.vocatree.server.library.domain.model.query

import com.vocatree.server.common.domain.model.query.SimpleUserView
import com.vocatree.server.library.domain.model.enumerated.SharedState
import com.vocatree.server.library.domain.model.enumerated.TrustedLevel
import java.time.LocalDateTime

data class SharedWordbookListView(
    val id: Long,
    val title: String,
    val slug: String,
    val sharedState: SharedState,
    val trustedLevel: TrustedLevel,
    val creator: SimpleUserView,
    val latestVersion: Long,
    val createdAt: LocalDateTime,
)

data class SharedWordbookDetailView(
    val id: Long,
    val title: String,
    val slug: String,
    val description: String?,
    val sharedState: SharedState,
    val trustedLevel: TrustedLevel,
    val creator: SimpleUserView,
    val createdAt: LocalDateTime,
    val latestVersion: Long,
    val latestRevisionId: Long,
)

data class SharedWordbookVersionView(
    val id: Long,
    val sharedWordbookId: Long,
    val version: Long,
    val versionNote: String?,
    val wordCount: Int,
    val revisedAt: LocalDateTime,
)
