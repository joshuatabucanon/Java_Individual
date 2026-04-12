package com.bookshop.books.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;


/**
 * Request DTO for creating a new book.
 *
 * <p>
 * Contains all required information necessary to persist a new book
 * in the system. Validation constraints ensure data integrity.
 * </p>
 */
public class BookCreateRequestDto {


    /**
     * International Standard Book Number.
     * Must be unique and between 13 and 17 characters.
     */
    @NotBlank(message = "ISBN is required")
    @Size(min = 13, max = 17, message = "ISBN must be between 13 and 17 characters")
    private String isbn;


    /**
     * Title of the book.
     */
    @NotBlank(message = "Title is required")
    private String title;

    /**
     * Author of the book.
     */
    @NotBlank(message = "Author is required")
    private String author;

    /**
     * Publisher name (optional).
     */
    private String publisher;

    /**
     * Category tags for classification (comma-separated).
     */
    private String categoryTags;

    /**
     * Retail price of the book.
     * Must be zero or greater.
     */
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = true,
            message = "Price must be zero or greater")
    @Digits(integer = 8, fraction = 2,
            message = "Price must be a valid monetary value")
    private BigDecimal price;

    /**
     * Available stock quantity.
     * Must be zero or greater.
     */
    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity cannot be negative")
    private Integer stockQuantity;

    
    /* getters and setters */
    
	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public String getCategoryTags() {
		return categoryTags;
	}

	public void setCategoryTags(String categoryTags) {
		this.categoryTags = categoryTags;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public Integer getStockQuantity() {
		return stockQuantity;
	}

	public void setStockQuantity(Integer stockQuantity) {
		this.stockQuantity = stockQuantity;
	}

}