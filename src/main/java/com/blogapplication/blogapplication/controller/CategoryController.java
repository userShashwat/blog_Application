package com.blogapplication.blogapplication.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blogapplication.blogapplication.payload.CategoryDto;
import com.blogapplication.blogapplication.service.CategoryService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/category")
public class CategoryController {

    @Autowired
    CategoryService categoryService;

    @PostMapping
    // FIXED: was hasRole('ADMIN') — standardised to hasRole('ADMIN') which Spring prefixes to ROLE_ADMIN
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    public CategoryDto addCategory(@RequestBody CategoryDto categoryDto) {
        return categoryService.addCategory(categoryDto);
    }

    @GetMapping
    public List<CategoryDto> getCategoryList() {
        return categoryService.getCategoryList();
    }

    @GetMapping("/{categoryId}")
    public CategoryDto getCategoryById(@PathVariable("categoryId") Long categoryId) {
        return categoryService.getCategoryById(categoryId);
    }

    @PutMapping("/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    public CategoryDto updateCategoryById(
            @PathVariable("categoryId") Long categoryId,
            @RequestBody CategoryDto categoryDto
    ) {
        return categoryService.updateCategoryById(categoryId, categoryDto);
    }

    @DeleteMapping("/{categoryId}")
    // FIXED: was hasRole('ROLE_ADMIN') which resolves to ROLE_ROLE_ADMIN — changed to hasRole('ADMIN')
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    // FIXED: removed unnecessary @RequestBody CategoryDto — DELETE endpoints don't take a body
    public CategoryDto deleteCategoryById(@PathVariable("categoryId") Long categoryId) {
        return categoryService.deleteCategoryById(categoryId);
    }
}