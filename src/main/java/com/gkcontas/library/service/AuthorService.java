package com.gkcontas.library.service;

import com.gkcontas.library.exception.NotFoundException;
import com.gkcontas.library.model.Author;
import com.gkcontas.library.repository.AuthorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthorService {

    private final AuthorRepository authorRepository;

    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    public Author create(String name) {
        return authorRepository.save(new Author(name));
    }

    @Transactional(readOnly = true)
    public Author findById(Long id) {
        return authorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Author", id));
    }
}
