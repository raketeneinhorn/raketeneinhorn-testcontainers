package com.raketeneinhorn.testcontainers.actuate.env;

import com.raketeneinhorn.testcontainers.actuate.TestcontainersEndpoint;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Order
public class TestcontainersDevelopmentEnvironmentPostProcessor implements EnvironmentPostProcessor {

    private static final String EXPOSED_ENDPOINTS_PROPERTY_KEY = "management.endpoints.web.exposure.include";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        @SuppressWarnings("unchecked")
        Set<String> exposedEndpoints = (Set<String>) environment.getProperty(EXPOSED_ENDPOINTS_PROPERTY_KEY, Set.class);
        if (exposedEndpoints == null) {
            exposedEndpoints = new HashSet<>();
        }

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