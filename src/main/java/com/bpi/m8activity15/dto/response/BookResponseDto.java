package com.bpi.m8activity15.dto.response;

public class BookResponseDto {

    private final Integer id;
    private final String title;
    private final String author;

    public BookResponseDto(Integer id, String title, String author) {
        this.id = id;
        this.title = title;
        this.author = author;
    }

    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }
}