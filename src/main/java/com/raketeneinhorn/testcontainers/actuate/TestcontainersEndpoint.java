package com.raketeneinhorn.testcontainers.actuate;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.context.ApplicationContext;
import org.testcontainers.containers.GenericContainer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Endpoint(id = TestcontainersEndpoint.ENDPOINT_ID)
public class TestcontainersEndpoint {

    public static final String ENDPOINT_ID = "testcontainers";

    private final ApplicationContext applicationContext;

    private Map<String,TestcontainerInfo> testcontainerInfoCache = new HashMap<>();

    public TestcontainersEndpoint(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.initTestcontainerInfoCache();
    }

    @ReadOperation
    public Map<String,TestcontainerInfo> testcontainers() {
        return testcontainerInfoCache;
    }

    private void initTestcontainerInfoCache() {
        this.testcontainerInfoCache = applicationContext.getBeansOfType(GenericContainer.class).values().stream()
            .map(this::buildTestcontainerInfo)
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
            testcontainerInfoCustomizer.customize(testcontainerInfo);
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
