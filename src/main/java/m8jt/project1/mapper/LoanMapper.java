package m8jt.project1.mapper;

import org.mapstruct.Mapper;

import m8jt.project1.dto.response.LoanResponseDTO;
import m8jt.project1.model.LoanEntity;

@Mapper(componentModel = "spring", uses = {BookMapper.class, UserMapper.class})
public interface LoanMapper {
    LoanResponseDTO toResponse(LoanEntity entity);
}
