package com.raketeneinhorn.testcontainers.actuate;

import java.util.function.Consumer;

@FunctionalInterface
public interface TestcontainerInfoCustomizer extends Consumer<TestcontainerInfo> {
}
