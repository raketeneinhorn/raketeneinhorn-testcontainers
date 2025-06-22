package com.raketeneinhorn.testcontainers.container.keycloak.configuration;

import com.raketeneinhorn.testcontainers.condition.NotOnKubernetesCondition;
import com.raketeneinhorn.testcontainers.container.keycloak.DasnikoKeycloakContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;

@Conditional(NotOnKubernetesCondition.class)
public class DasnikoKeycloakContainerConfiguration {

    @Bean
    public DasnikoKeycloakContainer keycloakContainer() {
        try (DasnikoKeycloakContainer dasnikoKeycloakContainer = new DasnikoKeycloakContainer()) {
            return dasnikoKeycloakContainer
                .withRealmImportFile("/testdata/container/keycloak/test-realm.json");
        }
    }

}
