package m8jt.project1.mapper;

import org.mapstruct.Mapper;

import m8jt.project1.dto.response.UserResponseDTO;
import m8jt.project1.model.UserEntity;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponseDTO toResponse(UserEntity entity);
}
