package com.gkcontas.library.integration;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.context.TestPropertySource;

/**
 * Confirms that resolving {@code Book.author} for a listing of many books
 * does not degrade into one SQL query per book (the classic N+1 problem):
 * the DataLoader batches every author id seen in the response into a single
 * {@code findAllById} call, regardless of how many books are returned.
 */
@TestPropertySource(properties = "spring.jpa.properties.hibernate.generate_statistics=true")
class BookAuthorNPlusOneTest extends IntegrationTestBase {

    @Autowired
    private GraphQlTester graphQlTester;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Test
    void shouldResolveAuthorsForManyBooksWithoutOnePerBookQuery() {
        List<String> authorIds = List.of(
                createAuthor("Author One"),
                createAuthor("Author Two"),
                createAuthor("Author Three")
        );
        for (int i = 0; i < 9; i++) {
            createBook("Book " + i, 2000 + i, authorIds.get(i % authorIds.size()));
        }

        Statistics statistics = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        statistics.clear();

        List<String> authorNames = graphQlTester.document("query { books { title author { name } } }")
                .execute()
                .path("books[*].author.name").entityList(String.class).get();

        assertThat(authorNames).hasSize(9);

        long queryCount = statistics.getPrepareStatementCount();
        // One query to list the books, one batched query to load the (at
        // most 3 distinct) authors via the DataLoader — never 9.
        assertThat(queryCount).isLessThanOrEqualTo(2);
    }

    private String createAuthor(String name) {
        return graphQlTester.document("mutation($name: String!) { createAuthor(name: $name) { id } }")
                .variable("name", name)
                .execute()
                .path("createAuthor.id").entity(String.class).get();
    }

    private String createBook(String title, int publicationYear, String authorId) {
        return graphQlTester.document(
                        "mutation($title: String!, $year: Int, $authorId: ID!) { createBook(title: $title, publicationYear: $year, authorId: $authorId) { id } }")
                .variable("title", title)
                .variable("year", publicationYear)
                .variable("authorId", authorId)
                .execute()
                .path("createBook.id").entity(String.class).get();
    }
}
