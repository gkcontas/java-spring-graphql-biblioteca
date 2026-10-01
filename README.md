# API GraphQL de Biblioteca

API de biblioteca — autores, livros e empréstimos — exposta via GraphQL em vez de REST.

Além do básico de schema, queries e mutations, o projeto resolve o problema mais comum de GraphQL sobre banco relacional: o N+1. O campo `Book.author` é resolvido por `DataLoader`, que junta todos os autores pedidos em uma rodada de consulta e os busca em uma única query — a listagem custa duas consultas SQL, tenha ela 3 ou 300 livros.

## Tecnologias e bibliotecas

| | |
|---|---|
| Linguagem | Java 17 |
| Framework | Spring Boot 3.3, Spring for GraphQL |
| Persistência | Spring Data JPA, PostgreSQL 16 |
| Migrations | Flyway |
| Build | Gradle Kotlin DSL (wrapper `gradlew`) |
| Testes | JUnit 5, Mockito, `GraphQlTester`, Testcontainers |
| Apoio | Lombok |

## Pré-requisitos

- JDK 17 ou superior
- Docker

## Como rodar

```bash
docker compose up -d
```

```bash
./gradlew bootRun
```

- Playground: `http://localhost:8080/graphiql`
- Endpoint: `POST http://localhost:8080/graphql`

## O schema

```graphql
type Query {
  "Lista livros, com filtro opcional por autor e/ou título"
  books(authorId: ID, title: String): [Book!]!
  "Busca um autor pelo id, junto com seus livros"
  author(id: ID!): Author
  "Lista os empréstimos ainda não devolvidos"
  activeLoans: [Loan!]!
}

type Mutation {
  createAuthor(name: String!): Author
  createBook(title: String!, publicationYear: Int, authorId: ID!): Book
  createLoan(bookId: ID!, borrower: String!): Loan
  returnLoan(loanId: ID!): Loan
}
```

Os tipos `Author`, `Book` e `Loan` estão em [`schema.graphqls`](src/main/resources/graphql/schema.graphqls).

## Exemplos de uso

Pelo GraphiQL, ou por `curl`:

```bash
curl -s localhost:8080/graphql -H "Content-Type: application/json" \
  -d '{"query": "mutation { createAuthor(name: \"J.R.R. Tolkien\") { id name } }"}'
```

```bash
curl -s localhost:8080/graphql -H "Content-Type: application/json" \
  -d '{"query": "mutation { createBook(title: \"The Hobbit\", publicationYear: 1937, authorId: 1) { id title author { name } } }"}'
```

```bash
curl -s localhost:8080/graphql -H "Content-Type: application/json" \
  -d '{"query": "{ books(title: \"hobbit\") { title publicationYear author { name } } }"}'
```

```bash
curl -s localhost:8080/graphql -H "Content-Type: application/json" \
  -d '{"query": "mutation { createLoan(bookId: 1, borrower: \"Alice\") { id loanDate book { title } } }"}'
```

```bash
curl -s localhost:8080/graphql -H "Content-Type: application/json" \
  -d '{"query": "{ activeLoans { id borrower loanDate book { title } } }"}'
```

```bash
curl -s localhost:8080/graphql -H "Content-Type: application/json" \
  -d '{"query": "mutation { returnLoan(loanId: 1) { id returnDate } }"}'
```

## Testes

```bash
./gradlew test
```

10 testes: 4 unitários e 6 de integração, que executam queries e mutations reais contra um PostgreSQL em container. Um deles conta os statements preparados pelo Hibernate para garantir que a listagem de livros continua custando no máximo duas consultas, independentemente do número de livros.
