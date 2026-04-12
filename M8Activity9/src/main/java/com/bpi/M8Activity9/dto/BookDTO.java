package com.bpi.M8Activity9.dto;

public class BookDTO {
    private Integer id;
    private String title;
    private String author;

    public BookDTO() {}

    public BookDTO(Integer id, String title, String author) {
        this.id = id;
        this.title = title;
        this.author = author;
    }

    // Getters & setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
}

