package com.raketeneinhorn.testcontainers.container.openpolicyagent;

import com.raketeneinhorn.testcontainers.actuate.TestcontainerInfo;
import com.raketeneinhorn.testcontainers.actuate.TestcontainerInfoCustomizer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.ClassPathResource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import java.net.URI;
import java.util.Map;

@Slf4j
@SuppressWarnings("java:S2160")
public class OpenPolicyAgentContainer extends GenericContainer<OpenPolicyAgentContainer> implements TestcontainerInfoCustomizer {

    private static final String IMAGE_NAME = "openpolicyagent/opa";
    private static final DockerImageName DEFAULT_IMAGE_NAME = DockerImageName.parse(IMAGE_NAME);

    private static final int OPEN_POLICY_AGENT_PORT = 8181;
    private static final String HEALTH_PATH = "/health";
    private static final String DATA_PATH = "/v1/data/"; // NOSONAR
    private static final String POLICIES_CONTAINER_PATH = "/policies";

    private ClassPathResource policiesClassPathResource = new ClassPathResource(POLICIES_CONTAINER_PATH);
    private LogLevel logLevel = LogLevel.INFO;

    public OpenPolicyAgentContainer() {
        super(DEFAULT_IMAGE_NAME);
    }

    @Override
    protected void doStart() {
        this.withCreateContainerCmdModifier(createContainerCmd ->
            createContainerCmd.withCmd(
                "run", "--server",
                "--log-level", logLevel.getCommandValue(),
                "--addr=0.0.0.0:" + OPEN_POLICY_AGENT_PORT,
                POLICIES_CONTAINER_PATH
            )
        ).waitingFor(
            Wait.forHttp(HEALTH_PATH)
                .forStatusCode(200)
        ).withExposedPorts(
            OPEN_POLICY_AGENT_PORT
        ).withCopyToContainer(
            MountableFile.forClasspathResource(policiesClassPathResource.getPath()),
            POLICIES_CONTAINER_PATH
        ).withLogConsumer(
            new Slf4jLogConsumer(log)
        );
        super.doStart();
    }

    public void withPoliciesClassPathResource(@NotNull ClassPathResource policiesClassPathResource) {
        this.policiesClassPathResource = policiesClassPathResource;
    }

    public void withLogLevel(@NotNull LogLevel logLevel) {
        this.logLevel = logLevel;
    }

    public String getDataUrl() {
        return this.buildUrl(DATA_PATH);
    }

    public String getHealthUrl() {
        return this.buildUrl(HEALTH_PATH);
    }

    private String buildUrl(String path) {
        return String.format("http://%s:%s%s", getHost(), getMappedPort(OPEN_POLICY_AGENT_PORT), path);
    }

    @Override
    public void customize(TestcontainerInfo testcontainerInfo) {
        testcontainerInfo.setHttpEntrypoints(Map.of(
            "dataUrl", URI.create(this.getDataUrl()),
            "healthUrl", URI.create(this.getHealthUrl())
        ));
    }

    @RequiredArgsConstructor
    public enum LogLevel {

        DEBUG("debug"),
        INFO("info"),
        ERROR("error");

        private final String commandValue;

        private String getCommandValue() {
            return commandValue;
        }

    }

}
