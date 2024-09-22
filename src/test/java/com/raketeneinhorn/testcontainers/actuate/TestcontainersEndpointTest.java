package com.raketeneinhorn.testcontainers.actuate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.support.StaticApplicationContext;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
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

    private <T extends GenericContainer<?>> T registerGenericContainerMockBean(Class<T> containerClass, String beanName, String dockerImageName, URL testHttpEntrypointURL) {
        T container = Mockito.mock(containerClass);
        Mockito.when(container.getDockerImageName()).thenReturn(dockerImageName);
        staticApplicationContext.registerBean(beanName, GenericContainer.class, () -> container);

        if (container instanceof TestcontainerInfoCustomizer testcontainerInfoCustomizer) {
            Mockito.doAnswer(invocationOnMock -> {
                Object testcontainerInfoRaw = invocationOnMock.getArgument(0);
                assertThat(testcontainerInfoRaw).isOfAnyClassIn(TestcontainerInfo.class);
                TestcontainerInfo testcontainerInfo = (TestcontainerInfo) testcontainerInfoRaw;
                testcontainerInfo.setHttpEntrypoint(testHttpEntrypointURL);
                return null;
            }).when(testcontainerInfoCustomizer).accept(Mockito.any());
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
            assertThat(testcontainerInfo.getHttpEntrypoint()).isNull();
        }

        @Test
        void providesMapWithSingleContainerWithHttpEntrypoint() throws MalformedURLException {
            this.providesMapWithSingleContainerWithHttpEntrypoint(1);
        }

        private void providesMapWithSingleContainerWithHttpEntrypoint(int expectedSize) throws MalformedURLException {
            final String beanName = "containerWithHttpEntrypoint";
            final String dockerImageName = "containerWithHttpEntrypoint-test-docker-image-name";
            final URL testHttpEntrypointURL = URI.create("http://example.com/raketeneinhorn").toURL();

            registerGenericContainerMockBean(GenericContainerWithHttpEntrypoint.class, beanName, dockerImageName, testHttpEntrypointURL);

            Map<String,TestcontainerInfo> testcontainers = testcontainersEndpointUnderTest.testcontainers();
            assertThat(testcontainers).hasSize(expectedSize);

            TestcontainerInfo testcontainerInfo = testcontainers.get(beanName);
            assertTestcontainerInfoBasics(testcontainerInfo, beanName, dockerImageName);

            assertThat(testcontainerInfo).extracting(TestcontainerInfo::getHttpEntrypoint)
                    .isNotNull()
                    .isEqualTo(testHttpEntrypointURL);
        }

        @Test
        void providesMapWithTwoContainers() throws MalformedURLException {
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
        public void accept(TestcontainerInfo testcontainerInfo) {
            throw new UnsupportedOperationException();
        }

    }

}
