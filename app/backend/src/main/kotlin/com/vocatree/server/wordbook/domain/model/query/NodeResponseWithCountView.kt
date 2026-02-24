package com.vocatree.server.wordbook.domain.model.query

import com.vocatree.server.wordbook.domain.model.enumerated.NodeType
import java.time.LocalDateTime

data class NodeResponseWithCountView(
    val id: Long,
    val title: String,
    val isStarred: Boolean,
    val nodeType: NodeType,
    val createdAt: LocalDateTime,
    val count: Int,
)