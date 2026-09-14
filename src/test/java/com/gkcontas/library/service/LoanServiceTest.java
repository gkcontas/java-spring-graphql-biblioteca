package com.gkcontas.library.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.gkcontas.library.exception.NotFoundException;
import com.gkcontas.library.model.Author;
import com.gkcontas.library.model.Book;
import com.gkcontas.library.model.Loan;
import com.gkcontas.library.repository.LoanRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private BookService bookService;

    private LoanService loanService;
    private Book book;

    @BeforeEach
    void setUp() {
        loanService = new LoanService(loanRepository, bookService);
        book = new Book("The Hobbit", 1937, new Author("J.R.R. Tolkien"));
    }

    @Test
    void shouldCreateLoanForExistingBook() {
        when(bookService.findEntityById(1L)).thenReturn(book);
        when(loanRepository.save(any(Loan.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Loan loan = loanService.create(1L, "Alice");

        assertThat(loan.getBorrower()).isEqualTo("Alice");
        assertThat(loan.getBook()).isEqualTo(book);
        assertThat(loan.isActive()).isTrue();
    }

    @Test
    void shouldReturnAnActiveLoan() {
        Loan loan = new Loan(book, "Alice");
        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));

        loanService.returnLoan(1L);

        assertThat(loan.isActive()).isFalse();
    }

    @Test
    void shouldThrowWhenReturningAnAlreadyReturnedLoan() {
        Loan loan = new Loan(book, "Alice");
        loan.returnBook();
        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));

        assertThatThrownBy(() -> loanService.returnLoan(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already been returned");
    }

    @Test
    void shouldThrowNotFoundWhenReturningMissingLoan() {
        when(loanRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> loanService.returnLoan(99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("99");
    }
}
