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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
            assertThat(mockEnvironment.getProperty(EXPOSED_ENDPOINTS_PROPERTY_KEY, Set.class)).isNullOrEmpty();

            testcontainersDevelopmentEnvironmentPostProcessor.postProcessEnvironment(mockEnvironment, springApplication);

            @SuppressWarnings("unchecked")
            Set<String> exposedEndpoints = (Set<String>) mockEnvironment.getProperty(EXPOSED_ENDPOINTS_PROPERTY_KEY, Set.class);
            assertThat(exposedEndpoints)
                .hasSize(1)
                .contains(TestcontainersEndpoint.ENDPOINT_ID);
        }

        @Test
        void addsTestcontainersEndpointToExistingExposedEndpoints() {
            mockEnvironment.setProperty(EXPOSED_ENDPOINTS_PROPERTY_KEY, "foobar");

            testcontainersDevelopmentEnvironmentPostProcessor.postProcessEnvironment(mockEnvironment, springApplication);

            @SuppressWarnings("unchecked")
            Set<String> exposedEndpoints = (Set<String>) mockEnvironment.getProperty(EXPOSED_ENDPOINTS_PROPERTY_KEY, Set.class);
            assertThat(exposedEndpoints)
                .hasSize(2)
                .contains("foobar", TestcontainersEndpoint.ENDPOINT_ID);
        }

        @Test
        void emitsWarningWhenExposingEndpoint() {
            testcontainersDevelopmentEnvironmentPostProcessor.postProcessEnvironment(mockEnvironment, springApplication);
            verify(log, times(1)).warn("Automatically adding 'testcontainers' to exposed endpoints.");
        }

    }

}