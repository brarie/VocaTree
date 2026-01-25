package com.monovoca.server.supportBoard.domain.exception

class SupportPostNotFoundException : RuntimeException()
class CannotDeleteCategoryWithSubCategoriesException : RuntimeException()
class CannotDeleteCategoryWithPostsException : RuntimeException()