package m9jt.project1.dto.request;

import static m9jt.project1.util.DbFieldLimits.BOOK_AUTHOR_MAX;
import static m9jt.project1.util.DbFieldLimits.BOOK_ID_MIN;
import static m9jt.project1.util.DbFieldLimits.BOOK_TITLE_MAX;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record BookRequestDTO(
	    @Min(BOOK_ID_MIN) Integer bookId,
	    @Size(max = BOOK_TITLE_MAX) String title,
	    @Size(max = BOOK_AUTHOR_MAX) String author
	) {}
