package com.monovoca.server.supportBoard.infrastructure.web

import com.monovoca.server.supportBoard.application.SupportCategoryService
import com.monovoca.server.supportBoard.infrastructure.web.request.CreateMainCategoryRequest
import com.monovoca.server.supportBoard.infrastructure.web.request.CreateSubCategoryRequest
import com.monovoca.server.supportBoard.infrastructure.web.request.UpdateCategoryRequest
import com.monovoca.server.supportBoard.infrastructure.web.response.SupportCategoryResponse
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/support-categories")
class SupportCategoryController(
    private val supportCategoryService: SupportCategoryService
) {

    @GetMapping("/all")
    fun getAllCategories(): ResponseEntity<List<SupportCategoryResponse>> {
        val categories = supportCategoryService.findAllCategories()
            .map { SupportCategoryResponse.from(it) }
        return ResponseEntity.ok(categories)
    }

    @GetMapping
    fun getMainCategories(): ResponseEntity<List<SupportCategoryResponse>> {
        val categories = supportCategoryService.findMainCategories()
            .map { SupportCategoryResponse.from(it) }
        return ResponseEntity.ok(categories)
    }

    @GetMapping("/{categoryId}/sub-categories")
    fun getSubCategories(@PathVariable categoryId: Long): ResponseEntity<List<SupportCategoryResponse>> {
        val categories = supportCategoryService.findSubCategoriesById(categoryId)
            .map { SupportCategoryResponse.from(it) }
        return ResponseEntity.ok(categories)
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    fun createMainCategory(@RequestBody request: CreateMainCategoryRequest): ResponseEntity<SupportCategoryResponse> {
        val category = supportCategoryService.createMainCategory(request.categoryName)
        return ResponseEntity.ok(SupportCategoryResponse.from(category))
    }

    @PostMapping("/{parentCategoryId}/sub-categories")
    @PreAuthorize("hasRole('ADMIN')")
    fun createSubCategory(
        @PathVariable parentCategoryId: Long,
        @RequestBody request: CreateSubCategoryRequest
    ): ResponseEntity<SupportCategoryResponse> {
        val category = supportCategoryService.createSubCategory(request.categoryName, parentCategoryId)
        return ResponseEntity.ok(SupportCategoryResponse.from(category))
    }

    @PutMapping("/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    fun updateCategory(
        @PathVariable categoryId: Long,
        @RequestBody request: UpdateCategoryRequest
    ): ResponseEntity<SupportCategoryResponse> {
        val category = supportCategoryService.update(categoryId, request.categoryName)
        return ResponseEntity.ok(SupportCategoryResponse.from(category))
    }

    @DeleteMapping("/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    fun deleteCategory(@PathVariable categoryId: Long): ResponseEntity<Unit> {
        supportCategoryService.delete(categoryId)
        return ResponseEntity.ok().build()
    }
}