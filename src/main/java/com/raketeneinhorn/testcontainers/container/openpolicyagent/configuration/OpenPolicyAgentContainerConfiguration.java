package com.raketeneinhorn.testcontainers.container.openpolicyagent.configuration;

import com.raketeneinhorn.testcontainers.condition.NotOnKubernetesCondition;
import com.raketeneinhorn.testcontainers.container.openpolicyagent.OpenPolicyAgentContainer;
import com.raketeneinhorn.testcontainers.container.openpolicyagent.OpenPolicyAgentContainerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;

import java.util.Optional;

@Conditional(NotOnKubernetesCondition.class)
public class OpenPolicyAgentContainerConfiguration {

    @Bean
    public OpenPolicyAgentContainer openPolicyAgentContainer(Optional<OpenPolicyAgentContainerCustomizer> openPolicyAgentContainerCustomizer) {
        try (OpenPolicyAgentContainer openPolicyAgentContainer = new OpenPolicyAgentContainer()) {
            openPolicyAgentContainerCustomizer.ifPresent(c -> c.customize(openPolicyAgentContainer));
            return openPolicyAgentContainer;
        }
    }

}
