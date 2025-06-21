package com.raketeneinhorn.testcontainers.container.postgresql;

import com.raketeneinhorn.testcontainers.container.postgresql.configuration.BitnamiPostgreSQLContainerConfiguration;
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
@ContextConfiguration(classes = BitnamiPostgreSQLContainerConfiguration.class)
class BitnamiPostgreSQLContainerSpringBootTest {

    private final PostgreSQLContainer<?> bitnamiPostgreSQLContainer;

    @Test
    void bitnamiPostgreSQLContainerIsRunning() {
        assertTrue(bitnamiPostgreSQLContainer.isRunning());
        assertThat(bitnamiPostgreSQLContainer.getDockerImageName()).isEqualTo("bitnami/postgresql:17.5.0");
    }

}
