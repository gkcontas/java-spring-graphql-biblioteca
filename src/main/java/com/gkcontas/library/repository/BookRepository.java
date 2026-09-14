package com.gkcontas.library.repository;

import com.gkcontas.library.model.Book;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findByAuthorId(Long authorId);

    @Query("""
            SELECT b FROM Book b
            WHERE (:authorId IS NULL OR b.authorId = :authorId)
              AND (:title IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', CAST(:title AS string), '%')))
            ORDER BY b.title
            """)
    List<Book> search(@Param("authorId") Long authorId, @Param("title") String title);
}
