package m7group6.project1.service;

import m7group6.project1.model.BookEntity;
import m7group6.project1.model.LoanEntity;
import m7group6.project1.model.UserEntity;
import m7group6.project1.service.dto.BookDto;
import m7group6.project1.service.dto.LoanDto;
import m7group6.project1.service.dto.UserDto;

/**
* Internal mappers from JPA entities -> DTOs.
* Keep this package-private (no public modifier) so only the service layer uses it.
*/
final class Mappers {
 private Mappers() {}

 static BookDto toDto(BookEntity e) {
     if (e == null) return null;
     // Adjust getters if your entity names differ
     return new BookDto(
         e.getId(),
         e.getTitle(),
         e.getAuthor(),
         Boolean.TRUE.equals(e.getIsAvailable())
     );
 }

 static UserDto toDto(UserEntity e) {
     if (e == null || e.getUserID() == null) return null;
     return new UserDto(e.getUserID(), e.getName());
 }

 static LoanDto toDto(LoanEntity e) {
     if (e == null) return null;
     return new LoanDto(
         e.getLoanId(),
         toDto(e.getBook()),
         toDto(e.getUser())
     );
 }
}
