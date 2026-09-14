package com.gkcontas.library.config;

import com.gkcontas.library.model.Author;
import com.gkcontas.library.repository.AuthorRepository;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.BatchLoaderRegistry;
import reactor.core.publisher.Mono;

@Configuration
public class DataLoaderConfig {

    public DataLoaderConfig(BatchLoaderRegistry registry, AuthorRepository authorRepository) {
        registry.forTypePair(Long.class, Author.class).registerMappedBatchLoader((authorIds, environment) -> {
            List<Author> authors = authorRepository.findAllById(authorIds);
            Map<Long, Author> byId = authors.stream().collect(Collectors.toMap(Author::getId, Function.identity()));
            return Mono.just(byId);
        });
    }
}
