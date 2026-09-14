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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "book")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(name = "publication_year")
    private Integer publicationYear;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false, insertable = false, updatable = false)
    private Author author;

    /**
     * Mirrors {@code author.id} as a plain column. The {@link #author}
     * association above is marked non-insertable/non-updatable and exists
     * only for JPA joins/queries; reading {@code authorId} here never
     * triggers a lazy load, which is what lets the GraphQL layer batch-load
     * authors via a DataLoader instead of issuing one query per book.
     */
    @Column(name = "author_id", nullable = false)
    private Long authorId;

    public Book(String title, Integer publicationYear, Author author) {
        this.title = title;
        this.publicationYear = publicationYear;
        this.author = author;
        this.authorId = author.getId();
    }
}
