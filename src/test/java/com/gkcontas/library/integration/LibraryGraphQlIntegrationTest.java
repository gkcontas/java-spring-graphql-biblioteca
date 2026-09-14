package com.gkcontas.library.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.test.tester.GraphQlTester;

class LibraryGraphQlIntegrationTest extends IntegrationTestBase {

    @Autowired
    private GraphQlTester graphQlTester;

    @Test
    void shouldCreateAuthorAndBookAndResolveNestedAuthor() {
        String authorId = graphQlTester.document("mutation { createAuthor(name: \"J.R.R. Tolkien\") { id name } }")
                .execute()
                .path("createAuthor.name").entity(String.class).isEqualTo("J.R.R. Tolkien")
                .path("createAuthor.id").entity(String.class).get();

        graphQlTester.document(
                        "mutation($authorId: ID!) { createBook(title: \"The Hobbit\", publicationYear: 1937, authorId: $authorId) { id title author { name } } }")
                .variable("authorId", authorId)
                .execute()
                .path("createBook.title").entity(String.class).isEqualTo("The Hobbit")
                .path("createBook.author.name").entity(String.class).isEqualTo("J.R.R. Tolkien");
    }

    @Test
    void shouldFilterBooksByTitleAndByAuthor() {
        String authorId = createAuthor("George Orwell");
        createBook("1984", 1949, authorId);
        createBook("Animal Farm", 1945, authorId);

        List<String> byTitle = graphQlTester.document("query($title: String) { books(title: $title) { title } }")
                .variable("title", "1984")
                .execute()
                .path("books[*].title").entityList(String.class).get();
        assertThat(byTitle).containsExactly("1984");

        List<String> byAuthor = graphQlTester.document("query($authorId: ID) { books(authorId: $authorId) { title } }")
                .variable("authorId", authorId)
                .execute()
                .path("books[*].title").entityList(String.class).get();
        assertThat(byAuthor).containsExactlyInAnyOrder("1984", "Animal Farm");
    }

    @Test
    void shouldCreateLoanListActiveLoansAndReturnIt() {
        String authorId = createAuthor("Aldous Huxley");
        String bookId = createBook("Brave New World", 1932, authorId);

        String loanId = graphQlTester.document(
                        "mutation($bookId: ID!) { createLoan(bookId: $bookId, borrower: \"Alice\") { id borrower returnDate } }")
                .variable("bookId", bookId)
                .execute()
                .path("createLoan.borrower").entity(String.class).isEqualTo("Alice")
                .path("createLoan.returnDate").valueIsNull()
                .path("createLoan.id").entity(String.class).get();

        graphQlTester.document("query { activeLoans { id borrower } }")
                .execute()
                .path("activeLoans[*].id").entityList(String.class).contains(loanId);

        graphQlTester.document("mutation($loanId: ID!) { returnLoan(loanId: $loanId) { id returnDate } }")
                .variable("loanId", loanId)
                .execute()
                .path("returnLoan.returnDate").entity(String.class).satisfies(value -> assertThat(value).isNotBlank());

        graphQlTester.document("query { activeLoans { id } }")
                .execute()
                .path("activeLoans[*].id").entityList(String.class).doesNotContain(loanId);
    }

    @Test
    void shouldReturnNotFoundErrorForMissingAuthor() {
        graphQlTester.document("query { author(id: 999999) { name } }")
                .execute()
                .errors()
                .expect(error -> error.getMessage().contains("not found"))
                .verify()
                .path("author").valueIsNull();
    }

    @Test
    void shouldRejectReturningAnAlreadyReturnedLoan() {
        String authorId = createAuthor("Ray Bradbury");
        String bookId = createBook("Fahrenheit 451", 1953, authorId);
        String loanId = graphQlTester.document(
                        "mutation($bookId: ID!) { createLoan(bookId: $bookId, borrower: \"Bob\") { id } }")
                .variable("bookId", bookId)
                .execute()
                .path("createLoan.id").entity(String.class).get();

        graphQlTester.document("mutation($loanId: ID!) { returnLoan(loanId: $loanId) { id } }")
                .variable("loanId", loanId)
                .execute()
                .errors().verify();

        graphQlTester.document("mutation($loanId: ID!) { returnLoan(loanId: $loanId) { id } }")
                .variable("loanId", loanId)
                .execute()
                .errors()
                .expect(error -> error.getMessage().contains("already been returned"))
                .verify();
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
