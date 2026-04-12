package m9jt.project1.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import m9jt.project1.dto.response.LoanResponseDTO;
import m9jt.project1.model.LoanEntity;

@Mapper(componentModel = "spring", uses = BookMapper.class)
public interface LoanMapper {

    @Mapping(target = "borrowerName", source = "user.name")
    LoanResponseDTO toResponse(LoanEntity entity);
}