package com.bookshop.books.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for filtering book search results.
 *
 * <p>
 * Each field represents an optional filter criterion.
 * Fields not provided will be ignored.
 * </p>
 */
public class BookFilterRequestDto {


    // ---------- Identifiers ----------

    /**
     * Book identifier filter.
     */
    private String id;

    /**
     * ISBN filter (partial or full match).
     */
	@Size(min = 1, message = "ISBN filter must not be blank")
    private String isbn;

    // ---------- Text filters ----------

    /**
     * Title filter (partial match).
     */
    @Size(min = 1, message = "Title filter must not be blank")
    private String title;

    /**
     * Author filter (partial match).
     */
    @Size(min = 1, message = "Author filter must not be blank")
    private String author;

    /**
     * Publisher filter (partial match).
     */
    @Size(min = 1, message = "Publisher filter must not be blank")
    private String publisher;

    /**
     * Category filter.
     */
    @Size(min = 1, message = "Category filter must not be blank")
    private String category;

    // ---------- Numeric filters ----------

    /**
     * Minimum price filter.
     */
    @DecimalMin(value = "0.0", message = "Minimum price must be zero or greater")
    private BigDecimal minPrice;

    /**
     * Maximum price filter.
     */
    @DecimalMin(value = "0.0", message = "Maximum price must be zero or greater")
    private BigDecimal maxPrice;

    /**
     * Minimum stock quantity filter.
     */
    @Min(value = 0, message = "Minimum stock must be zero or greater")
    private Integer minStock;

    /**
     * Maximum stock quantity filter.
     */
    @Min(value = 0, message = "Maximum stock must be zero or greater")
    private Integer maxStock;

    /**
     * Creation date filter (ISO-8601 string).
     */
    @Size(min = 4, message = "createdAt filter must not be blank")
    private String createdAt;
    
    
    /* getters and setters */
    
	public String getId() {
		return id;
	}
	public void setId(String id) {
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
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public BigDecimal getMinPrice() {
		return minPrice;
	}
	public void setMinPrice(BigDecimal minPrice) {
		this.minPrice = minPrice;
	}
	public BigDecimal getMaxPrice() {
		return maxPrice;
	}
	public void setMaxPrice(BigDecimal maxPrice) {
		this.maxPrice = maxPrice;
	}
	public Integer getMinStock() {
		return minStock;
	}
	public void setMinStock(Integer minStock) {
		this.minStock = minStock;
	}
	public Integer getMaxStock() {
		return maxStock;
	}
	public void setMaxStock(Integer maxStock) {
		this.maxStock = maxStock;
	}
	public String getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}


}
