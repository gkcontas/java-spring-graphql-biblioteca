package com.gkcontas.library.service;

import com.gkcontas.library.exception.NotFoundException;
import com.gkcontas.library.model.Book;
import com.gkcontas.library.model.Loan;
import com.gkcontas.library.repository.LoanRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LoanService {

    private final LoanRepository loanRepository;
    private final BookService bookService;

    public LoanService(LoanRepository loanRepository, BookService bookService) {
        this.loanRepository = loanRepository;
        this.bookService = bookService;
    }

    public Loan create(Long bookId, String borrower) {
        Book book = bookService.findEntityById(bookId);
        return loanRepository.save(new Loan(book, borrower));
    }

    public Loan returnLoan(Long loanId) {
        Loan loan = findEntityById(loanId);
        loan.returnBook();
        return loan;
    }

    @Transactional(readOnly = true)
    public List<Loan> findActiveLoans() {
        return loanRepository.findActiveLoans();
    }

    private Loan findEntityById(Long id) {
        return loanRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Loan", id));
    }
}
