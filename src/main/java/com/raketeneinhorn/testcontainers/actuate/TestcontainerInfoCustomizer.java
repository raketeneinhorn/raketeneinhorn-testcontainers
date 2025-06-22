package com.raketeneinhorn.testcontainers.actuate;

@FunctionalInterface
public interface TestcontainerInfoCustomizer {

    void customize(TestcontainerInfo testcontainerInfo);

}
