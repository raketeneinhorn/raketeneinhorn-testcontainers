package com.raketeneinhorn.testcontainers.container.postgresql.configuration;

import com.raketeneinhorn.testcontainers.condition.NotOnKubernetesCondition;
import com.raketeneinhorn.testcontainers.container.postgresql.BitnamiPostgreSQLContainer;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.testcontainers.containers.PostgreSQLContainer;

@Conditional(NotOnKubernetesCondition.class)
public class BitnamiPostgreSQLContainerConfiguration {

    @Bean
    @ServiceConnection
    public PostgreSQLContainer<BitnamiPostgreSQLContainer> postgreSQLContainer() {
        try (PostgreSQLContainer<BitnamiPostgreSQLContainer> postgreSQLContainer = new BitnamiPostgreSQLContainer()) {
            return postgreSQLContainer
                .withReuse(true);
        }
    }

}
