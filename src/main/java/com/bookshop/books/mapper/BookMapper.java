package com.bookshop.books.mapper;

import com.bookshop.books.dto.request.BookCreateRequestDto;
import com.bookshop.books.dto.request.BookUpdateRequestDto;
import com.bookshop.books.dto.response.BookResponseDto;
import com.bookshop.books.model.BookEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface BookMapper {


    // CREATE
    BookEntity toEntity(BookCreateRequestDto dto);

    // READ 
    BookResponseDto toResponseDto(BookEntity entity);

    // UPDATE

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntityFromDto(
        BookUpdateRequestDto dto,
        @MappingTarget BookEntity entity
    );


}