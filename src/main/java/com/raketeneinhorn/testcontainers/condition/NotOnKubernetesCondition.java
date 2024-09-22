package com.raketeneinhorn.testcontainers.condition;

import org.springframework.boot.autoconfigure.condition.ConditionalOnCloudPlatform;
import org.springframework.boot.autoconfigure.condition.NoneNestedConditions;
import org.springframework.boot.cloud.CloudPlatform;
import org.springframework.context.annotation.ConfigurationCondition;

public class NotOnKubernetesCondition extends NoneNestedConditions {

    public NotOnKubernetesCondition() {
        super(ConfigurationCondition.ConfigurationPhase.PARSE_CONFIGURATION);
    }

    @ConditionalOnCloudPlatform(CloudPlatform.KUBERNETES)
    static class KubernetesCondition {
    }

}
