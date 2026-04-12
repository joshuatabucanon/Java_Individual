
package com.bookshop.books.dto.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * Request DTO for partially updating an existing book.
 *
 * <p>
 * All fields are optional. Only non-null values will be applied
 * to the existing book record.
 * </p>
 */
public class BookUpdateRequestDto {

    /**
     * Updated ISBN value.
     * Must be between 13 and 17 characters if provided.
     */
    @Size(min = 13, max = 17)
    private String isbn;

    /**
     * Updated book title.
     * Must not be blank if provided.
     */
    @Size(min = 1, message = "Title must not be blank")
    private String title;

    /**
     * Updated author name.
     * Must not be blank if provided.
     */
    @Size(min = 1, message = "Author must not be blank")
    private String author;

    /**
     * Updated publisher name.
     */
    private String publisher;

    /**
     * Updated category tags (comma-separated).
     */
    private String categoryTags;

    /**
     * Updated price of the book.
     * Must be zero or greater if provided.
     */
    @DecimalMin(
        value = "0.0",
        inclusive = true,
        message = "Price must be zero or greater"
    )
    @Digits(
        integer = 8,
        fraction = 2,
        message = "Price must be a valid monetary value"
    )
    private BigDecimal price;

    /**
     * Updated stock quantity.
     * Must be zero or greater if provided.
     */
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
