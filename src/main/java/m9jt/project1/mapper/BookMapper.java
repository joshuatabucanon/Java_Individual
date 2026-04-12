package m9jt.project1.mapper;

import org.mapstruct.Mapper;

import m9jt.project1.dto.response.BookResponseDTO;
import m9jt.project1.model.BookEntity;

@Mapper(componentModel = "spring")
public interface BookMapper {
    BookResponseDTO toResponse(BookEntity entity);
}
