package com.almousleck.ecombackend.product;

import com.almousleck.ecombackend.dto.CategoryDto;
import com.almousleck.ecombackend.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    @Override
    public CategoryDto getCategoryById(Long categoryId) {
        Category category = categoryRepository
                .findById(categoryId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Category with id: " + categoryId + " not found")
                );
        return modelMapper.map(category, CategoryDto.class);
    }

    @Override
    public Category getCategoryByName(String name) {
        return categoryRepository.findByName(name);
    }

    @Override
    public List<CategoryDto> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .map(category ->
                        modelMapper.map(category, CategoryDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto createCategory(CategoryDto categoryDto) {
        Category category = modelMapper.map(categoryDto, Category.class);
        Category createdCategory = categoryRepository.save(category);
        return modelMapper.map(createdCategory, CategoryDto.class);
    }

    @Override
    public CategoryDto updateCategory(Long categoryId, CategoryDto categoryDto) {
       Category existingCategory = categoryRepository.findById(categoryId)
               .orElseThrow(() -> new ResourceNotFoundException("Category with the given id does not exist"));
       existingCategory.setName(categoryDto.getName());
       Category updatedCategory = categoryRepository.save(existingCategory);
       return modelMapper.map(updatedCategory, CategoryDto.class);
    }

    @Override
    public void deleteCategory(Long categoryId) {
        categoryRepository.findById(categoryId)
                .ifPresentOrElse(categoryRepository::delete, () -> {
                    throw new ResourceNotFoundException("Category with the given id does not exist");
                });
    }
}
