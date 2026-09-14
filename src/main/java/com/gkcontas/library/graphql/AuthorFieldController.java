package com.gkcontas.library.graphql;

import com.gkcontas.library.model.Author;
import com.gkcontas.library.model.Book;
import com.gkcontas.library.service.BookService;
import java.util.List;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

@Controller
public class AuthorFieldController {

    private final BookService bookService;

    public AuthorFieldController(BookService bookService) {
        this.bookService = bookService;
    }

    @SchemaMapping(typeName = "Author", field = "books")
    public List<Book> books(Author author) {
        return bookService.findByAuthorId(author.getId());
    }
}
