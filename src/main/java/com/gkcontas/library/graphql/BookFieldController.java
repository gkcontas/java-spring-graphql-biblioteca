package com.gkcontas.library.graphql;

import com.gkcontas.library.model.Author;
import com.gkcontas.library.model.Book;
import java.util.concurrent.CompletableFuture;
import org.dataloader.DataLoader;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

@Controller
public class BookFieldController {

    /**
     * Resolves {@code Book.author} through the batched "Author" DataLoader
     * (registered in {@link com.gkcontas.library.config.DataLoaderConfig})
     * instead of loading it eagerly here. When a query returns a list of
     * books, Spring for GraphQL collects every book's author id from all
     * books in the same batch window and resolves them with a single
     * {@code AuthorRepository.findAllById(...)} call — one query for N
     * books, not N queries.
     */
    @SchemaMapping(typeName = "Book", field = "author")
    public CompletableFuture<Author> author(Book book, DataLoader<Long, Author> authorDataLoader) {
        return authorDataLoader.load(book.getAuthorId());
    }
}
