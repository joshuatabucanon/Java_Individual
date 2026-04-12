package m8jt.project1.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UserRequestDTO(
	    @NotBlank String name
	) {}
