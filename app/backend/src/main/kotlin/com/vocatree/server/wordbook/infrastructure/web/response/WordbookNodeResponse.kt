package com.vocatree.server.wordbook.infrastructure.web.response

import com.vocatree.server.wordbook.domain.model.WordbookNode
import com.vocatree.server.wordbook.domain.model.enumerated.NodeType
import java.time.LocalDateTime

data class WordbookNodeResponse(
    val id: Long,
    val title: String,
    val isStarred: Boolean,
    val nodeType: NodeType,
    val createdAt: LocalDateTime,
) {
    companion object {
        fun from(wordbookNode: WordbookNode): WordbookNodeResponse =
            WordbookNodeResponse(
                id = wordbookNode.id!!,
                title = wordbookNode.title,
                isStarred = wordbookNode.isStarred,
                nodeType = wordbookNode.nodeType,
                createdAt = wordbookNode.createdAt
            )
    }
}