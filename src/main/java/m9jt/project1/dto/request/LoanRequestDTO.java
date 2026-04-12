package m9jt.project1.dto.request;

import jakarta.validation.constraints.Min;

public record LoanRequestDTO(
	    @Min(1) Integer bookId
	) {}