package com.gkcontas.library.repository;

import com.gkcontas.library.model.Loan;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    @Query("SELECT l FROM Loan l JOIN FETCH l.book WHERE l.returnDate IS NULL ORDER BY l.loanDate")
    List<Loan> findActiveLoans();
}
