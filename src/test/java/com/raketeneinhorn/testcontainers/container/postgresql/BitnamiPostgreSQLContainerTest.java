package com.raketeneinhorn.testcontainers.container.postgresql;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BitnamiPostgreSQLContainerTest {

    @Test
    void bitnamiPostgreSQLContainerCanBeStarted() {
        try (BitnamiPostgreSQLContainer<?> container = new BitnamiPostgreSQLContainer<>()) {
            assertThat(container.getDockerImageName()).isEqualTo("bitnami/postgresql:16.4.0");
            container.start();
        }
    }

}
