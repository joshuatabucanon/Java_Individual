package com.bpi.m8activity15.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.bpi.m8activity15.dto.request.CreateBookRequestDto;
import com.bpi.m8activity15.dto.request.UpdateBookRequestDto;
import com.bpi.m8activity15.dto.response.BookResponseDto;
import com.bpi.m8activity15.model.Book;

@Mapper(componentModel = "spring")
public interface BookMapper {

    @Mapping(target = "id", ignore = true)
    Book toEntity(CreateBookRequestDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void updateEntity(UpdateBookRequestDto dto, @MappingTarget Book book);

    BookResponseDto toResponse(Book book);
}
