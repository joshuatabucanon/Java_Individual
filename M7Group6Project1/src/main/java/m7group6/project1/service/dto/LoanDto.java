package m7group6.project1.service.dto;

public record LoanDto(
    int loanId,
    BookDto book,
    UserDto user
) {}