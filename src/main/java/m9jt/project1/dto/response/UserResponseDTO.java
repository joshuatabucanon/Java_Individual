package m9jt.project1.dto.response;

import java.util.List;

public record UserResponseDTO(
    Integer userId,
    String name,
    List<String> roles,
    String username
) {}