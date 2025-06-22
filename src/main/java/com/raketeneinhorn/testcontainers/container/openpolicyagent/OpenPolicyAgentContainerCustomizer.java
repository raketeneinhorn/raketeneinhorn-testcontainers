package com.raketeneinhorn.testcontainers.container.openpolicyagent;

@FunctionalInterface
public interface OpenPolicyAgentContainerCustomizer {

    void customize(OpenPolicyAgentContainer openPolicyAgentContainer);

}
