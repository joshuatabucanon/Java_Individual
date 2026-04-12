package m7group6.project1.service.dto;

public record BookDto(
    int bookId,
    String title,
    String author,
    boolean isAvailable
) {}