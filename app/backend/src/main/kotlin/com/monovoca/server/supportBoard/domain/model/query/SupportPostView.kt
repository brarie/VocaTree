package com.monovoca.server.supportBoard.domain.model.query

import com.monovoca.server.common.domain.model.query.SimpleUserView
import java.time.LocalDateTime

data class SupportPostView(
    val id: Long,
    val title: String,
    val content: String,
    val author: SimpleUserView,
    val supportCategoryId: Long,
    val supportCategoryName: String,
    val createdAt: LocalDateTime,
)