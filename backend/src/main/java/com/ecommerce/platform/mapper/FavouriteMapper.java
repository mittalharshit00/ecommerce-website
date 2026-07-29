package com.ecommerce.platform.mapper;

import com.ecommerce.platform.dto.response.FavouriteResponse;
import com.ecommerce.platform.entity.Favourite;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FavouriteMapper {

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "imageUrl", source = "product.imageUrl")
    @Mapping(target = "price", source = "product.price")
    FavouriteResponse toResponse(Favourite favourite);

}