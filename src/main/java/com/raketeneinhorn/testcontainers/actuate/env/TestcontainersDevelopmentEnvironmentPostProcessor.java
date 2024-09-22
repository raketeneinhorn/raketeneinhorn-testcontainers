package com.raketeneinhorn.testcontainers.actuate.env;

import com.raketeneinhorn.testcontainers.actuate.TestcontainersEndpoint;
import org.apache.commons.logging.Log;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.logging.DeferredLogFactory;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Order
public class TestcontainersDevelopmentEnvironmentPostProcessor implements EnvironmentPostProcessor {

    protected static final String EXPOSED_ENDPOINTS_PROPERTY_KEY = "management.endpoints.web.exposure.include";

    private final Log log;

    public TestcontainersDevelopmentEnvironmentPostProcessor(DeferredLogFactory deferredLogFactory) {
        log = deferredLogFactory.getLog(TestcontainersDevelopmentEnvironmentPostProcessor.class);
    }

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        @SuppressWarnings("unchecked")
        Set<String> exposedEndpoints = environment.getProperty(EXPOSED_ENDPOINTS_PROPERTY_KEY, Set.class);
        if (exposedEndpoints == null) {
            exposedEndpoints = new HashSet<>();
        }

        String exposureWarningMessage = String.format("Automatically adding '%s' to exposed endpoints.", TestcontainersEndpoint.ENDPOINT_ID);
        log.warn(exposureWarningMessage);
        exposedEndpoints.add(TestcontainersEndpoint.ENDPOINT_ID);

        PropertySource<Map<String,Object>> mapPropertySource = new MapPropertySource(
            TestcontainersDevelopmentEnvironmentPostProcessor.class.getName(),
            Map.of(
                EXPOSED_ENDPOINTS_PROPERTY_KEY, exposedEndpoints
            )
        );

        environment.getPropertySources().addFirst(mapPropertySource);
    }

}
