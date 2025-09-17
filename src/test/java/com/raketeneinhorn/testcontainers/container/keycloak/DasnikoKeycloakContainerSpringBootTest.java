package com.raketeneinhorn.testcontainers.container.keycloak;

import com.raketeneinhorn.testcontainers.actuate.configuration.TestcontainersEndpointConfiguration;
import com.raketeneinhorn.testcontainers.container.keycloak.configuration.DasnikoKeycloakContainerConfiguration;
import dasniko.testcontainers.keycloak.ExtendableKeycloakContainer;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
@SpringBootTest
@EnableAutoConfiguration
@Import({
    TestcontainersEndpointConfiguration.class,
    DasnikoKeycloakContainerConfiguration.class
})
@AutoConfigureMockMvc
class DasnikoKeycloakContainerSpringBootTest {

    private final ExtendableKeycloakContainer<?> dasnikoKeycloakContainer;

    private final MockMvc mockMvc;

    @Test
    void dasnikoKeycloakContainerIsRunning() {
        assertTrue(dasnikoKeycloakContainer.isRunning());
        assertThat(dasnikoKeycloakContainer.getDockerImageName()).isEqualTo("quay.io/keycloak/keycloak:26.2");
    }

    @Test
    void testcontainersEndpointExposesKeycloakContainerInformation() throws Exception {
        this.mockMvc.perform(get("/actuator/testcontainers"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.keycloakContainer.dockerImageName").value(dasnikoKeycloakContainer.getDockerImageName()))
            .andExpect(jsonPath("$.keycloakContainer.httpEntrypoints.mgmtServerUrl").value(dasnikoKeycloakContainer.getMgmtServerUrl()))
            .andExpect(jsonPath("$.keycloakContainer.httpEntrypoints.authServerUrl").value(dasnikoKeycloakContainer.getAuthServerUrl()));
    }

    @SpringBootConfiguration
    public static class TestSpringBootConfiguration {

    }

}
