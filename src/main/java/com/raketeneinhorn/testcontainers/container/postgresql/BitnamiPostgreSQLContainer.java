package com.raketeneinhorn.testcontainers.container.postgresql;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;
import org.testcontainers.utility.DockerImageName;

public class BitnamiPostgreSQLContainer extends PostgreSQLContainer<BitnamiPostgreSQLContainer> {

    private static final String IMAGE_NAME = "bitnami/postgresql:17.5.0";
    private static final DockerImageName DEFAULT_IMAGE_NAME = DockerImageName.parse(IMAGE_NAME).asCompatibleSubstituteFor(PostgreSQLContainer.IMAGE);

    public BitnamiPostgreSQLContainer() {
        super(DEFAULT_IMAGE_NAME);

        this.setCommand("/opt/bitnami/scripts/postgresql/run.sh");
        ((LogMessageWaitStrategy) this.waitStrategy).withTimes(1);
    }

}
