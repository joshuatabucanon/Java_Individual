
package com.bookshop.books.dto.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Response DTO representing a book resource.
 *
 * <p>
 * This DTO is returned by REST endpoints and reflects
 * the persisted state of a book.
 * </p>
 */
public class BookResponseDto {

    /**
     * Unique identifier of the book.
     */
    private UUID id;

    /**
     * International Standard Book Number.
     */
    private String isbn;

    /**
     * Title of the book.
     */
    private String title;

    /**
     * Author of the book.
     */
    private String author;

    /**
     * Publisher of the book.
     */
    private String publisher;

    /**
     * Category tags used for classification.
     */
    private String categoryTags;

    /**
     * Retail price of the book.
     */
    private BigDecimal price;

    /**
     * Current stock quantity.
     */
    private Integer stockQuantity;

    /**
     * Timestamp when the book was created.
     */
    private OffsetDateTime createdAt;

    
    /* getters and setters */
    
	public UUID getId() {
		return id;
	}
	public void setId(UUID id) {
		this.id = id;
	}
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
	public OffsetDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(OffsetDateTime createdAt) {
		this.createdAt = createdAt;
	}
    
    
}