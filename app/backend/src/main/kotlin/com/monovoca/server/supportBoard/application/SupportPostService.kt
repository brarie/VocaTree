package com.monovoca.server.supportBoard.application

import com.monovoca.server.common.domain.port.holder.LocalDateTimeHolder
import com.monovoca.server.supportBoard.domain.exception.SupportCategoryNotFoundException
import com.monovoca.server.supportBoard.domain.exception.SupportPostNotFoundException
import com.monovoca.server.supportBoard.domain.model.SupportPost
import com.monovoca.server.supportBoard.domain.port.SupportCategoryRepository
import com.monovoca.server.supportBoard.domain.port.SupportPostRepository
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SupportPostService(
    private val supportCategoryRepository: SupportCategoryRepository,
    private val supportPostRepository: SupportPostRepository,
    private val localDateTimeHolder: LocalDateTimeHolder
) {

    @Transactional(readOnly = true)
    fun paginateViewsByCategoryTree(categoryId: Long, pageable: Pageable) =
        supportPostRepository.paginateViewsByCategoryTree(
            categoryId,
            pageable
        )

    @Transactional
    fun create(userId: String, title: String, content: String, categoryId: Long): SupportPost {
        val category = supportCategoryRepository.findById(categoryId)
            ?: throw SupportCategoryNotFoundException()

        val post = SupportPost.create(
            title = title,
            content = content,
            authorId = userId,
            supportCategory = category,
            localDateTimeHolder = localDateTimeHolder
        ).let {
            supportPostRepository.save(it)
        }

        return post
    }

    @Transactional
    fun update(postId: Long, toTitle: String, toContent: String, toCategoryId: Long): SupportPost {
        val post = supportPostRepository.findById(postId)
            ?: throw SupportPostNotFoundException()

        val toCategory = supportCategoryRepository.findById(toCategoryId)
            ?: throw SupportCategoryNotFoundException()

        val updatedPost = post.update(
            toTitle = toTitle,
            toContent = toContent,
            supportCategory = toCategory
        ).let {
            supportPostRepository.save(it)
        }

        return updatedPost
    }

    @Transactional
    fun delete(postId: Long) {
        val post = supportPostRepository.findById(postId)
            ?: throw SupportPostNotFoundException()

        supportPostRepository.delete(post)
    }
}