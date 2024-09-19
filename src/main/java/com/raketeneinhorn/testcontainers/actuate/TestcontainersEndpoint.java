package com.raketeneinhorn.testcontainers.actuate;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.context.ApplicationContext;
import org.testcontainers.containers.GenericContainer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Endpoint(id = TestcontainersEndpoint.ENDPOINT_ID)
public class TestcontainersEndpoint {

    public static final String ENDPOINT_ID = "testcontainers";

    private final List<GenericContainer<?>> containers;
    private final ApplicationContext applicationContext;

    private final Map<GenericContainer<?>,TestcontainerInfo> containerInfoCache = new HashMap<>();

    @ReadOperation
    public Map<String,TestcontainerInfo> testcontainers() {
        return containers.stream()
            .map(container -> containerInfoCache.computeIfAbsent(container, this::buildTestcontainerInfo))
            .collect(Collectors.toMap(
                TestcontainerInfo::getBeanName,
                Function.identity()
            ));
    }

    private TestcontainerInfo buildTestcontainerInfo(GenericContainer<?> genericContainer) {
        TestcontainerInfo testcontainerInfo = TestcontainerInfo.builder()
            .beanName(this.readBeanName(genericContainer))
            .dockerImageName(genericContainer.getDockerImageName())
            .build();

        if (genericContainer instanceof TestcontainerInfoCustomizer testcontainerInfoCustomizer) {
            testcontainerInfoCustomizer.accept(testcontainerInfo);
        }

        return testcontainerInfo;
    }

    private String readBeanName(GenericContainer<?> genericContainer) {
        return applicationContext.getBeansOfType(GenericContainer.class).entrySet().stream()
            .filter(e -> e.getValue().equals(genericContainer))
            .map(Map.Entry::getKey)
            .findFirst()
            .orElseThrow(() -> new IllegalStateException(String.format("Container '%s' not found as Bean.", genericContainer)));
    }

}
