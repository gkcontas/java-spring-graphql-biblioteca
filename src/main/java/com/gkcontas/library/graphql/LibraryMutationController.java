package com.gkcontas.library.graphql;

import com.gkcontas.library.model.Author;
import com.gkcontas.library.model.Book;
import com.gkcontas.library.model.Loan;
import com.gkcontas.library.service.AuthorService;
import com.gkcontas.library.service.BookService;
import com.gkcontas.library.service.LoanService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
public class LibraryMutationController {

    private final AuthorService authorService;
    private final BookService bookService;
    private final LoanService loanService;

    public LibraryMutationController(AuthorService authorService, BookService bookService, LoanService loanService) {
        this.authorService = authorService;
        this.bookService = bookService;
        this.loanService = loanService;
    }

    @MutationMapping
    public Author createAuthor(@Argument String name) {
        return authorService.create(name);
    }

    @MutationMapping
    public Book createBook(@Argument String title, @Argument Integer publicationYear, @Argument Long authorId) {
        return bookService.create(title, publicationYear, authorId);
    }

    @MutationMapping
    public Loan createLoan(@Argument Long bookId, @Argument String borrower) {
        return loanService.create(bookId, borrower);
    }

    @MutationMapping
    public Loan returnLoan(@Argument Long loanId) {
        return loanService.returnLoan(loanId);
    }
}
