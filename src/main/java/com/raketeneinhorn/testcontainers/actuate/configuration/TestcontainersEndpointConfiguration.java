package com.raketeneinhorn.testcontainers.actuate.configuration;

import com.raketeneinhorn.testcontainers.actuate.TestcontainersEndpoint;
import com.raketeneinhorn.testcontainers.condition.NotOnKubernetesCondition;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;

@TestConfiguration(proxyBeanMethods = false)
@Conditional(NotOnKubernetesCondition.class)
public class TestcontainersEndpointConfiguration {

    @Bean
    public TestcontainersEndpoint testcontainersEndpoint(ApplicationContext applicationContext) {
        return new TestcontainersEndpoint(applicationContext);
    }

}
