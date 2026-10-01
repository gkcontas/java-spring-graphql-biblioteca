package com.gkcontas.library.integration;

import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Base compartilhada pelos testes de integração.
 *
 * <p>O container segue o padrão <em>singleton container</em>: é iniciado num bloco
 * {@code static} e nunca é entregue à extensão {@code @Testcontainers} do JUnit. Aquela
 * extensão amarra o ciclo de vida de um container estático à <em>classe de teste</em> —
 * para o container ao fim da classe e sobe um novo, em outra porta, para a classe
 * seguinte. O Spring, por sua vez, cacheia o contexto entre classes com a mesma
 * configuração, então da segunda classe em diante o pool de conexões ainda aponta para o
 * container que acabou de ser destruído e os testes falham com <em>connection refused</em>.
 * Iniciar uma vez por JVM alinha os dois ciclos de vida. O Ryuk continua removendo o
 * container quando a JVM encerra.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureGraphQlTester
public abstract class IntegrationTestBase {

    @ServiceConnection
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine");

    static {
        POSTGRES.start();
    }
}
