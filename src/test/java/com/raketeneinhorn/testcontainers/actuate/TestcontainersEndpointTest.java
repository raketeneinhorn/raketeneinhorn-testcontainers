package com.raketeneinhorn.testcontainers.actuate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.support.StaticApplicationContext;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

import java.net.URI;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class TestcontainersEndpointTest {

    private StaticApplicationContext staticApplicationContext;
    private TestcontainersEndpoint testcontainersEndpointUnderTest;

    @BeforeEach
    void setUp() {
        staticApplicationContext = new StaticApplicationContext();
        testcontainersEndpointUnderTest = new TestcontainersEndpoint(staticApplicationContext);
    }

    private <T extends GenericContainer<?>> T registerGenericContainerMockBean(Class<T> containerClass, String beanName, String dockerImageName) {
        return this.registerGenericContainerMockBean(containerClass, beanName, dockerImageName, null);
    }

    private <T extends GenericContainer<?>> T registerGenericContainerMockBean(Class<T> containerClass, String beanName, String dockerImageName, URI testHttpEntrypointURI) {
        T container = Mockito.mock(containerClass);
        Mockito.when(container.getDockerImageName()).thenReturn(dockerImageName);
        staticApplicationContext.registerBean(beanName, GenericContainer.class, () -> container);

        if (container instanceof TestcontainerInfoCustomizer testcontainerInfoCustomizer) {
            Mockito.doAnswer(invocationOnMock -> {
                Object testcontainerInfoRaw = invocationOnMock.getArgument(0);
                assertThat(testcontainerInfoRaw).isOfAnyClassIn(TestcontainerInfo.class);
                TestcontainerInfo testcontainerInfo = (TestcontainerInfo) testcontainerInfoRaw;
                testcontainerInfo.setHttpEntrypoints(Map.of("testHttpEntrypoint", testHttpEntrypointURI));
                return null;
            }).when(testcontainerInfoCustomizer).customize(Mockito.any());
        }

        this.rebuildTestcontainersEndpointAfterRegisteringNewBean();

        return container;
    }

    private void rebuildTestcontainersEndpointAfterRegisteringNewBean() {
        testcontainersEndpointUnderTest = new TestcontainersEndpoint(staticApplicationContext);
    }

    static void assertTestcontainerInfoBasics(TestcontainerInfo testcontainerInfo, String beanName, String dockerImageName) {
        assertThat(testcontainerInfo).isNotNull();

        assertThat(testcontainerInfo.getBeanName()).isEqualTo(beanName);
        assertThat(testcontainerInfo.getDockerImageName()).isEqualTo(dockerImageName);
    }

    @Nested
    class Testcontainers {

        @Test
        void providesEmptyMapWhenNoContainersInApplicationContext() {
            Map<String,TestcontainerInfo> testcontainers = testcontainersEndpointUnderTest.testcontainers();
            assertThat(testcontainers).isEmpty();
        }

        @Test
        void providesMapWithSingleContainerWithoutHttpEntrypoint() {
            final String beanName = "containerWithoutHttpEntrypoint";
            final String dockerImageName = "containerWithoutHttpEntrypoint-test-docker-image-name";

            registerGenericContainerMockBean(GenericContainer.class, beanName, dockerImageName);

            Map<String,TestcontainerInfo> testcontainers = testcontainersEndpointUnderTest.testcontainers();
            assertThat(testcontainers).hasSize(1);

            TestcontainerInfo testcontainerInfo = testcontainers.get(beanName);
            assertTestcontainerInfoBasics(testcontainerInfo, beanName, dockerImageName);
            assertThat(testcontainerInfo.getHttpEntrypoints()).isNull();
        }

        @Test
        void providesMapWithSingleContainerWithHttpEntrypoint() {
            this.providesMapWithSingleContainerWithHttpEntrypoint(1);
        }

        private void providesMapWithSingleContainerWithHttpEntrypoint(int expectedSize) {
            final String beanName = "containerWithHttpEntrypoint";
            final String dockerImageName = "containerWithHttpEntrypoint-test-docker-image-name";
            final URI testHttpEntrypointURU = URI.create("http://example.com/raketeneinhorn");

            registerGenericContainerMockBean(GenericContainerWithHttpEntrypoint.class, beanName, dockerImageName, testHttpEntrypointURU);

            Map<String,TestcontainerInfo> testcontainers = testcontainersEndpointUnderTest.testcontainers();
            assertThat(testcontainers).hasSize(expectedSize);

            TestcontainerInfo testcontainerInfo = testcontainers.get(beanName);
            assertTestcontainerInfoBasics(testcontainerInfo, beanName, dockerImageName);

            assertThat(testcontainerInfo).extracting(TestcontainerInfo::getHttpEntrypoints)
                    .isNotNull()
                    .isEqualTo(Map.of("testHttpEntrypoint", testHttpEntrypointURU));
        }

        @Test
        void providesMapWithTwoContainers(){
            this.providesMapWithSingleContainerWithoutHttpEntrypoint();
            this.providesMapWithSingleContainerWithHttpEntrypoint(2);

            assertThat(testcontainersEndpointUnderTest.testcontainers()).hasSize(2);
        }

    }

    static class GenericContainerWithHttpEntrypoint extends GenericContainer<GenericContainerWithHttpEntrypoint> implements TestcontainerInfoCustomizer {

        public GenericContainerWithHttpEntrypoint() {
            super(DockerImageName.parse("this/is/ignored"));
        }

        @Override
        public void customize(TestcontainerInfo testcontainerInfo) {
            throw new UnsupportedOperationException();
        }

    }

}
