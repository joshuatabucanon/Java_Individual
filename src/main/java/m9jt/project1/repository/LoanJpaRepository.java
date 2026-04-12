package m9jt.project1.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import m9jt.project1.model.LoanEntity;

@Repository
public interface LoanJpaRepository extends JpaRepository<LoanEntity, Integer> {
    int countByUser_UserId(int userId);
    
    @Query("""
    	    select l.loanId
    	    from LoanEntity l
    	    where l.book.id = :bookId
    	""")
    Optional<Integer> findLoanIdByBookId(@Param("bookId") int bookId);
}
