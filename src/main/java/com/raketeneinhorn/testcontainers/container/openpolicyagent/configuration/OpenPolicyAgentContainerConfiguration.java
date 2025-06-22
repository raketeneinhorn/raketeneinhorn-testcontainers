package com.raketeneinhorn.testcontainers.container.openpolicyagent.configuration;

import com.raketeneinhorn.testcontainers.condition.NotOnKubernetesCondition;
import com.raketeneinhorn.testcontainers.container.openpolicyagent.OpenPolicyAgentContainer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;

import java.util.Optional;

@TestConfiguration(proxyBeanMethods = false)
@Conditional(NotOnKubernetesCondition.class)
public class OpenPolicyAgentContainerConfiguration {

    @Bean
    public OpenPolicyAgentContainer openPolicyAgentContainer(Optional<OpenPolicyAgentContainer.PoliciesClassPathResource> policiesClassPathResource) {
        try (OpenPolicyAgentContainer openPolicyAgentContainer = new OpenPolicyAgentContainer()) {
            policiesClassPathResource.ifPresent(openPolicyAgentContainer::withPoliciesClassPathResource);
            return openPolicyAgentContainer;
        }
    }

}
