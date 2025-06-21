package com.raketeneinhorn.testcontainers.container.keycloak;

import com.raketeneinhorn.testcontainers.container.keycloak.configuration.DasnikoKeycloakContainerConfiguration;
import dasniko.testcontainers.keycloak.ExtendableKeycloakContainer;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestConstructor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
@SpringBootTest
@SpringBootConfiguration
@ContextConfiguration(classes = DasnikoKeycloakContainerConfiguration.class)
class DasnikoKeycloakContainerSpringBootTest {

    private final ExtendableKeycloakContainer<?> dasnikoKeycloakContainer;

    @Test
    void dasnikoKeycloakContainerIsRunning() {
        assertTrue(dasnikoKeycloakContainer.isRunning());
        assertThat(dasnikoKeycloakContainer.getDockerImageName()).isEqualTo("quay.io/keycloak/keycloak:26.2");
    }

}
