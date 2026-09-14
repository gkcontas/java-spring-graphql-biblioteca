CREATE TABLE author (
    id      BIGSERIAL PRIMARY KEY,
    name    VARCHAR(150) NOT NULL
);

CREATE TABLE book (
    id                  BIGSERIAL PRIMARY KEY,
    title               VARCHAR(200) NOT NULL,
    publication_year    INTEGER,
    author_id           BIGINT NOT NULL REFERENCES author (id)
);

CREATE INDEX idx_book_author_id ON book (author_id);

CREATE TABLE loan (
    id              BIGSERIAL PRIMARY KEY,
    book_id         BIGINT NOT NULL REFERENCES book (id),
    borrower        VARCHAR(150) NOT NULL,
    loan_date       TIMESTAMP NOT NULL DEFAULT now(),
    return_date     TIMESTAMP
);

CREATE INDEX idx_loan_book_id ON loan (book_id);
CREATE INDEX idx_loan_return_date ON loan (return_date);
