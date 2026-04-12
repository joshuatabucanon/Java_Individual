package m8jt.project1.dto.response;

public record LoanResponseDTO(
	    Integer loanId, BookResponseDTO book, UserResponseDTO user
	) {}
