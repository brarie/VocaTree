package com.vocatree.server.common.util

fun generateSlug(title: String, id: Long): String {
    val sanitized = title
        .lowercase()
        .trim()
        .replace(Regex("[^a-z0-9가-힣\\s\\-]"), "")
        .replace(Regex("\\s+"), "-")
        .replace(Regex("-+"), "-")
        .trim('-')

    return "$sanitized-$id"
}
