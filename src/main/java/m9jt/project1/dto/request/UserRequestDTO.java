package m9jt.project1.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UserRequestDTO(
	    @NotBlank String name,
	    @NotBlank String username,
	    @NotBlank String password
	) {}
