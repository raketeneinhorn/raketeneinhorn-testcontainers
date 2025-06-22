package com.raketeneinhorn.testcontainers.container.openpolicyagent;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class OpenPolicyAgentContainerTest {

    private final OpenPolicyAgentContainer openPolicyAgentContainer = mock(OpenPolicyAgentContainer.class, CALLS_REAL_METHODS);

    @Nested
    class Accept {

        @Test
        void throwsIllegalArgumentExceptionWhenInfoUrlIsMalformed() {
            doReturn(1234).when(openPolicyAgentContainer).getMappedPort(8181);
            doReturn("h:t:t:p:/malformed-url").when(openPolicyAgentContainer).getHealthUrl();
            assertThrows(IllegalArgumentException.class, () -> openPolicyAgentContainer.accept(null));
        }

    }

}