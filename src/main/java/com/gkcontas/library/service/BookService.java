package com.gkcontas.library.service;

import com.gkcontas.library.exception.NotFoundException;
import com.gkcontas.library.model.Author;
import com.gkcontas.library.model.Book;
import com.gkcontas.library.repository.BookRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorService authorService;

    public BookService(BookRepository bookRepository, AuthorService authorService) {
        this.bookRepository = bookRepository;
        this.authorService = authorService;
    }

    public Book create(String title, Integer publicationYear, Long authorId) {
        Author author = authorService.findById(authorId);
        return bookRepository.save(new Book(title, publicationYear, author));
    }

    @Transactional(readOnly = true)
    public List<Book> search(Long authorId, String title) {
        return bookRepository.search(authorId, title);
    }

    @Transactional(readOnly = true)
    public List<Book> findByAuthorId(Long authorId) {
        return bookRepository.findByAuthorId(authorId);
    }

    @Transactional(readOnly = true)
    public Book findEntityById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Book", id));
    }
}
