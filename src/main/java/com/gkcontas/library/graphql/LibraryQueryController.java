package com.gkcontas.library.graphql;

import com.gkcontas.library.model.Author;
import com.gkcontas.library.model.Book;
import com.gkcontas.library.model.Loan;
import com.gkcontas.library.service.AuthorService;
import com.gkcontas.library.service.BookService;
import com.gkcontas.library.service.LoanService;
import java.util.List;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class LibraryQueryController {

    private final BookService bookService;
    private final AuthorService authorService;
    private final LoanService loanService;

    public LibraryQueryController(BookService bookService, AuthorService authorService, LoanService loanService) {
        this.bookService = bookService;
        this.authorService = authorService;
        this.loanService = loanService;
    }

    @QueryMapping
    public List<Book> books(@Argument Long authorId, @Argument String title) {
        return bookService.search(authorId, title);
    }

    @QueryMapping
    public Author author(@Argument Long id) {
        return authorService.findById(id);
    }

    @QueryMapping
    public List<Loan> activeLoans() {
        return loanService.findActiveLoans();
    }
}
