# API GraphQL de Biblioteca

API de biblioteca (autores, livros, empréstimos) em Java com Spring Boot, exposta via GraphQL (Spring for GraphQL) sobre PostgreSQL, com resolução de `Book.author` via `DataLoader` para evitar o problema clássico de N+1.

## Status

✅ MVP implementado.

## Stack

- Java 17 + Spring Boot 3.3 (Spring for GraphQL + Spring Web)
- PostgreSQL + Spring Data JPA + Flyway
- Lombok (nas entidades JPA)
- Gradle (Kotlin DSL) + wrapper `gradlew`
- Testcontainers (testes de integração) + `GraphQlTester` + JUnit 5 + Mockito

## Como rodar

1. Suba o PostgreSQL:
   ```bash
   docker compose up -d
   ```
2. Rode a aplicação:
   ```bash
   ./gradlew bootRun
   ```
3. Acesse o playground GraphiQL em `http://localhost:8080/graphiql`, ou envie requisições `POST` para `http://localhost:8080/graphql`.

## Como rodar os testes

```bash
./gradlew test
```

- `service` — testes unitários, não precisam de Docker.
- `integration` — testes de integração executando queries/mutations reais via `GraphQlTester` contra um PostgreSQL real (Testcontainers), incluindo o teste específico de N+1 (`BookAuthorNPlusOneTest`).

> **Nota sobre o ambiente de desenvolvimento usado para este projeto**: neste sandbox específico, os testes de integração baseados em Testcontainers não executam pela mesma causa raiz observada nos projetos [3](../java-spring-kafka-pipeline-eventos-cliques) e [5](../java-spring-selenium-painel-tarefas) (o cliente Docker embutido no Testcontainers, usado diretamente via Spring Boot, não negocia a versão de API do daemon Docker deste ambiente). Os 4 testes unitários passam normalmente, tudo compila, e o comportamento foi validado manualmente rodando a aplicação (ver seção seguinte).

## Por que `DataLoader` e por que evita N+1

Sem `DataLoader`, resolver `author` para uma listagem de N livros dispararia uma consulta SQL por livro (N+1: 1 para buscar os livros + N para buscar cada autor individualmente). O `Book` guarda o `authorId` como uma coluna simples (mapeada lado a lado com a associação `@ManyToOne`, mas marcada `insertable = false, updatable = false`), então lê-lo nunca dispara o carregamento preguiçoso do relacionamento.

O resolver `Book.author` (`BookFieldController`) usa esse `authorId` para pedir o autor a um `DataLoader<Long, Author>` em vez de acessá-lo diretamente. O Spring for GraphQL acumula todos os `authorId` pedidos durante a mesma "rodada" de resolução de uma consulta (todos os livros da listagem) e só então executa o `BatchLoader` registrado em `DataLoaderConfig`, que faz **uma única** chamada `AuthorRepository.findAllById(ids)` para todos eles — não importa se a listagem tem 3 livros ou 300, sempre no máximo 2 consultas SQL no total (uma para os livros, uma para os autores distintos). Isso é verificado automaticamente pelo teste `BookAuthorNPlusOneTest`, que zera as estatísticas do Hibernate antes da consulta e garante que no máximo 2 `PreparedStatement`s foram executados.

## Exemplos de query/mutation

```graphql
mutation {
  createAuthor(name: "J.R.R. Tolkien") { id name }
}

mutation {
  createBook(title: "The Hobbit", publicationYear: 1937, authorId: 1) {
    id
    title
    author { name }
  }
}

query {
  books(title: "hobbit") {
    title
    publicationYear
    author { name }
  }
}

query {
  author(id: 1) {
    name
    books { title publicationYear }
  }
}

mutation {
  createLoan(bookId: 1, borrower: "Alice") {
    id
    loanDate
    book { title }
  }
}

query {
  activeLoans {
    id
    borrower
    loanDate
    book { title }
  }
}

mutation {
  returnLoan(loanId: 1) { id returnDate }
}
```

Todos os fluxos acima (criação de autor/livro/empréstimo, filtros de `books`, listagem/devolução de empréstimos, e os erros de "não encontrado" e "empréstimo já devolvido") foram validados manualmente via GraphiQL durante o desenvolvimento.
