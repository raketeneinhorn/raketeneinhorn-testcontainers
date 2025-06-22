package com.raketeneinhorn.testcontainers.container.openpolicyagent;

import com.raketeneinhorn.testcontainers.actuate.configuration.TestcontainersEndpointConfiguration;
import com.raketeneinhorn.testcontainers.container.openpolicyagent.configuration.OpenPolicyAgentContainerConfiguration;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
@SpringBootTest
@EnableAutoConfiguration
@Import({
    TestcontainersEndpointConfiguration.class,
    OpenPolicyAgentContainerConfiguration.class
})
@AutoConfigureMockMvc
class OpenPolicyAgentContainerSpringBootTest {

    private final OpenPolicyAgentContainer openPolicyAgentContainer;

    private final MockMvc mockMvc;

    private final RestTemplateBuilder restTemplateBuilder;

    @TestConfiguration
    public static class TestPoliciesConfiguration {

        @Bean
        public OpenPolicyAgentContainer.PoliciesClassPathResource policiesClassPathResource() {
            return new OpenPolicyAgentContainer.PoliciesClassPathResource("/openpolicyagent/policies");
        }

    }

    @Test
    void openPolicyAgentContainerIsRunning() {
        assertTrue(openPolicyAgentContainer.isRunning());
        assertThat(openPolicyAgentContainer.getDockerImageName()).isEqualTo("openpolicyagent/opa:latest");
    }

    @Test
    void testcontainersEndpointExposesKeycloakContainerInformation() throws Exception {
        this.mockMvc.perform(get("/actuator/testcontainers"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.openPolicyAgentContainer.dockerImageName").value(openPolicyAgentContainer.getDockerImageName()))
            .andExpect(jsonPath("$.openPolicyAgentContainer.httpEntrypoints.dataUrl").value(openPolicyAgentContainer.getDataUrl()))
            .andExpect(jsonPath("$.openPolicyAgentContainer.httpEntrypoints.healthUrl").value(openPolicyAgentContainer.getHealthUrl()));
    }

    @Test
    void healthEndpointHasHttpStatusOKAndReturnsEmptyObject() {
        ResponseEntity<String> healthEntity = restTemplateBuilder.build()
                .getForEntity(openPolicyAgentContainer.getHealthUrl(), String.class);

        assertThat(healthEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(healthEntity.getBody().trim()).isEqualTo("{}");
    }


    private static class RuleInputAndResultArgumentsProvider implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                Arguments.of("admin", true),
                Arguments.of("bob", false)
            );
        }
    }

    @ParameterizedTest
    @ArgumentsSource(RuleInputAndResultArgumentsProvider.class)
    void dataEndpointEvaluatesRule(String user, boolean result) {
        String testInput = """
                {"input": {"user": "%s"}}
            """.formatted(user).trim();
        String testResult = """
                {"result":%s}
            """.formatted(result).trim();

        HttpEntity<String> request = new HttpEntity<>(testInput);
        ResponseEntity<String> response = restTemplateBuilder.build()
            .postForEntity(openPolicyAgentContainer.getDataUrl() + "example/allow", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().trim()).isEqualTo(testResult);
    }

    @SpringBootConfiguration
    public static class TestSpringBootConfiguration {

    }

}
