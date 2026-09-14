package com.gkcontas.library.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "loan")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(nullable = false, length = 150)
    private String borrower;

    @Column(name = "loan_date", nullable = false)
    private Instant loanDate;

    @Column(name = "return_date")
    private Instant returnDate;

    public Loan(Book book, String borrower) {
        this.book = book;
        this.borrower = borrower;
        this.loanDate = Instant.now();
    }

    public void returnBook() {
        if (this.returnDate != null) {
            throw new IllegalStateException("Loan %d has already been returned".formatted(this.id));
        }
        this.returnDate = Instant.now();
    }

    public boolean isActive() {
        return this.returnDate == null;
    }
}
