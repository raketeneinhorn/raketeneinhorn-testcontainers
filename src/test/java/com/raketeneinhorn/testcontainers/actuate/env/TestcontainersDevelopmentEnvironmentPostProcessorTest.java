package com.raketeneinhorn.testcontainers.actuate.env;

import com.raketeneinhorn.testcontainers.actuate.TestcontainersEndpoint;
import org.apache.commons.logging.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.logging.DeferredLogFactory;
import org.springframework.mock.env.MockEnvironment;

import java.util.Set;

import static com.raketeneinhorn.testcontainers.actuate.env.TestcontainersDevelopmentEnvironmentPostProcessor.EXPOSED_ENDPOINTS_PROPERTY_KEY;
import static org.assertj.core.api.Assertions.assertThat;

class TestcontainersDevelopmentEnvironmentPostProcessorTest {

    private MockEnvironment mockEnvironment;
    private SpringApplication springApplication;

    private Log log;

    private TestcontainersDevelopmentEnvironmentPostProcessor testcontainersDevelopmentEnvironmentPostProcessor;

    @BeforeEach
    void setUp() {
        mockEnvironment = new MockEnvironment();
        springApplication = Mockito.mock();

        log = Mockito.mock(Log.class);

        DeferredLogFactory deferredLogFactory = Mockito.mock(DeferredLogFactory.class);
        Mockito.when(deferredLogFactory.getLog(TestcontainersDevelopmentEnvironmentPostProcessor.class)).thenReturn(log);

        testcontainersDevelopmentEnvironmentPostProcessor = new TestcontainersDevelopmentEnvironmentPostProcessor(deferredLogFactory);
    }

    @Nested
    class PostProcessEnvironment {

        @Test
        void addsTestcontainersEndpointToExposedEndpointsWhenNoneSet() {
            @SuppressWarnings("unchecked")
            Set<String> exposedEndpoints = (Set<String>) mockEnvironment.getProperty(EXPOSED_ENDPOINTS_PROPERTY_KEY, Set.class);
            assertThat(exposedEndpoints).isNullOrEmpty();

            testcontainersDevelopmentEnvironmentPostProcessor.postProcessEnvironment(mockEnvironment, springApplication);
            assertThat(mockEnvironment.getProperty(EXPOSED_ENDPOINTS_PROPERTY_KEY, Set.class)).contains(TestcontainersEndpoint.ENDPOINT_ID);
        }

        @Test
        void addsTestcontainersEndpointToExistingExposedEndpoints() {
            mockEnvironment.setProperty(EXPOSED_ENDPOINTS_PROPERTY_KEY, "health");

            testcontainersDevelopmentEnvironmentPostProcessor.postProcessEnvironment(mockEnvironment, springApplication);
            assertThat(mockEnvironment.getProperty(EXPOSED_ENDPOINTS_PROPERTY_KEY, Set.class)).contains("health", TestcontainersEndpoint.ENDPOINT_ID);
        }

    }

}