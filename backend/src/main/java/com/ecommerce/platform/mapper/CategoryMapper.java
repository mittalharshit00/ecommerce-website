package com.ecommerce.platform.mapper;

import com.ecommerce.platform.dto.request.category.CreateCategoryRequest;
import com.ecommerce.platform.dto.response.CategoryResponse;
import com.ecommerce.platform.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "tenant", ignore = true)
    @Mapping(target = "products", ignore = true)
    Category toEntity(CreateCategoryRequest request);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    CategoryResponse toResponse(Category category);
}