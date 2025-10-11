package com.raketeneinhorn.testcontainers.container.postgresql;

import com.raketeneinhorn.testcontainers.container.postgresql.configuration.PostgreSQLContainerConfiguration;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestConstructor;
import org.testcontainers.containers.PostgreSQLContainer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
@SpringBootTest
@SpringBootConfiguration
@ContextConfiguration(classes = PostgreSQLContainerConfiguration.class)
class PostgreSQLContainerSpringBootTest {

    private final PostgreSQLContainer<?> postgreSQLContainer;

    @Test
    void postgreSQLContainerIsRunning() {
        assertTrue(postgreSQLContainer.isRunning());
        assertThat(postgreSQLContainer.getDockerImageName()).isEqualTo("postgres:18");
    }

}
