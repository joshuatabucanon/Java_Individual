package com.bookshop.books.repository;

import java.math.BigDecimal;

import org.springframework.data.jpa.domain.Specification;

import com.bookshop.books.dto.request.BookFilterRequestDto;
import com.bookshop.books.model.BookEntity;

public final class BookSpecifications {

    private BookSpecifications() {
        // utility class
    }

    /* ==================================================
       Public entry point for service layer
       ================================================== */

    public static Specification<BookEntity> filterBooks(BookFilterRequestDto filter) {
        if (filter == null) {
            return Specification.allOf();
        }

        return filterBooks(
            filter.getId(),
            filter.getIsbn(),
            filter.getTitle(),
            filter.getAuthor(),
            filter.getPublisher(),
            filter.getCategory(),
            filter.getMinPrice(),
            filter.getMaxPrice(),
            filter.getMinStock(),
            filter.getMaxStock(),
            filter.getCreatedAt()
        );
    }

    /* ==================================================
       Internal builder using primitive values
       ================================================== */
    private static Specification<BookEntity> filterBooks(
    	    String id,
    	    String isbn,
    	    String title,
    	    String author,
    	    String publisher,
    	    String category,
    	    BigDecimal minPrice,
    	    BigDecimal maxPrice,
    	    Integer minStock,
    	    Integer maxStock,
    	    String createdAt
    	) {
    	    Specification<BookEntity> spec = Specification.allOf();

    	    if (id != null && !id.isBlank()) {
    	        spec = spec.and(idEquals(id));
    	    }
    	    if (isbn != null && !isbn.isBlank()) {
    	        spec = spec.and(isbnContains(isbn));
    	    }
    	    if (title != null && !title.isBlank()) {
    	        spec = spec.and(titleContains(title));
    	    }
    	    if (author != null && !author.isBlank()) {
    	        spec = spec.and(authorContains(author));
    	    }
    	    if (publisher != null && !publisher.isBlank()) {
    	        spec = spec.and(publisherContains(publisher));
    	    }
    	    if (category != null && !category.isBlank()) {
    	        spec = spec.and(categoryContains(category));
    	    }
    	    if (minPrice != null) {
    	        spec = spec.and(priceGreaterThanOrEqual(minPrice));
    	    }
    	    if (maxPrice != null) {
    	        spec = spec.and(priceLessThanOrEqual(maxPrice));
    	    }
    	    if (minStock != null) {
    	        spec = spec.and(stockGreaterThanOrEqual(minStock));
    	    }
    	    if (maxStock != null) {
    	        spec = spec.and(stockLessThanOrEqual(maxStock));
    	    }
    	    if (createdAt != null && !createdAt.isBlank()) {
    	        spec = spec.and(createdAtLike(createdAt));
    	    }

    	    return spec;
    	}

    /* ==================================================
       Individual specification building blocks
       ================================================== */
    
	private static Specification<BookEntity> idEquals(String id) {
	    return (root, query, cb) ->
	        cb.equal(root.get("id"), java.util.UUID.fromString(id));
	}
	
	private static Specification<BookEntity> isbnContains(String isbn) {
	    return (root, query, cb) ->
	        cb.like(cb.lower(root.get("isbn")), "%" + isbn.toLowerCase() + "%");
	}

    private static Specification<BookEntity> titleContains(String title) {
        return (root, query, cb) ->
                cb.like(
                        cb.lower(root.get("title")),
                        "%" + title.toLowerCase() + "%"
                );
    }
    
    private static Specification<BookEntity> authorContains(String author) {
        return (root, query, cb) ->
                cb.like(
                        cb.lower(root.get("author")),
                        "%" + author.toLowerCase() + "%"
                );
    }

    private static Specification<BookEntity> categoryContains(String category) {
        return (root, query, cb) ->
                cb.like(
                        cb.lower(root.get("categoryTags")),
                        "%" + category.toLowerCase() + "%"
                );
    }

    private static Specification<BookEntity> priceGreaterThanOrEqual(BigDecimal min) {
        return (root, query, cb) ->
                cb.greaterThanOrEqualTo(root.get("price"), min);
    }

    private static Specification<BookEntity> priceLessThanOrEqual(BigDecimal max) {
        return (root, query, cb) ->
                cb.lessThanOrEqualTo(root.get("price"), max);
    }

    private static Specification<BookEntity> stockGreaterThanOrEqual(Integer minStock) {
        return (root, query, cb) ->
                cb.greaterThanOrEqualTo(root.get("stockQuantity"), minStock);
    }

    private static Specification<BookEntity> stockLessThanOrEqual(Integer maxStock) {
        return (root, query, cb) ->
                cb.lessThanOrEqualTo(root.get("stockQuantity"), maxStock);
    }
    
	private static Specification<BookEntity> publisherContains(String publisher) {
	    return (root, query, cb) ->
	        cb.like(cb.lower(root.get("publisher")), "%" + publisher.toLowerCase() + "%");
	}
	
	private static Specification<BookEntity> createdAtLike(String value) {
	    return (root, query, cb) ->
	        cb.like(
	            cb.function(
	                "to_char",
	                String.class,
	                root.get("createdAt"),
	                cb.literal("YYYY-MM-DD\"T\"HH24:MI")
	            ),
	            "%" + value + "%"
	        );
	}

}