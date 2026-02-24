package com.vocatree.server.supportBoard.infrastructure.web.request

data class CreateMainCategoryRequest(
    val categoryName: String
)

data class CreateSubCategoryRequest(
    val categoryName: String
)

data class UpdateCategoryRequest(
    val categoryName: String
)
