package com.raketeneinhorn.testcontainers.container.postgresql;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;
import org.testcontainers.utility.DockerImageName;

public class BitnamiPostgreSQLContainer<SELF extends BitnamiPostgreSQLContainer<SELF>> extends PostgreSQLContainer<SELF> {

    private static final String IMAGE_NAME = "bitnami/postgresql:16.4.0";
    private static final DockerImageName DEFAULT_IMAGE_NAME = DockerImageName.parse(IMAGE_NAME).asCompatibleSubstituteFor(PostgreSQLContainer.IMAGE);

    public BitnamiPostgreSQLContainer() {
        super(DEFAULT_IMAGE_NAME);

        this.setCommand("/opt/bitnami/scripts/postgresql/run.sh");
        ((LogMessageWaitStrategy) this.waitStrategy).withTimes(1);
    }

}
