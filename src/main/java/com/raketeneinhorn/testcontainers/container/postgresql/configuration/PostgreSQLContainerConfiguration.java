package com.raketeneinhorn.testcontainers.container.postgresql.configuration;

import com.raketeneinhorn.testcontainers.condition.NotOnKubernetesCondition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.utility.DockerImageName;

@Conditional(NotOnKubernetesCondition.class)
public class PostgreSQLContainerConfiguration {

    private static final Logger CONTAINER_LOGGER = LoggerFactory.getLogger(PostgreSQLContainer.class);

    @Bean
    @ServiceConnection
    public PostgreSQLContainer<?> postgreSQLContainer() {
        DockerImageName dockerImageName = DockerImageName.parse(PostgreSQLContainer.IMAGE).withTag("18");
        try (PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>(dockerImageName)) {
            postgreSQLContainer
                .withLogConsumer(new Slf4jLogConsumer(CONTAINER_LOGGER));

            return postgreSQLContainer
                .withReuse(true);
        }
    }

}
