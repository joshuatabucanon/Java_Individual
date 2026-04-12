package m9jt.project1.dto.response;

public record LoanResponseDTO(
    Integer loanId,
    BookResponseDTO book,
    String borrowerName
) {}
