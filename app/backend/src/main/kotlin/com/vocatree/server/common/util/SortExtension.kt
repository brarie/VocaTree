package com.vocatree.server.common.util

fun <T, R : Comparable<R>> Iterable<T>.isSortedBy(selector: (T) -> R): Boolean =
    this.asSequence()
        .map(selector)
        .zipWithNext { a, b -> a <= b }
        .all { it }