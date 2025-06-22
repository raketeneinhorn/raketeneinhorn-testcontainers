package com.raketeneinhorn.testcontainers.container.keycloak;

import com.raketeneinhorn.testcontainers.actuate.TestcontainerInfo;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class DasnikoKeycloakContainerTest {

    private final DasnikoKeycloakContainer dasnikoKeycloakContainerMock = mock(DasnikoKeycloakContainer.class, CALLS_REAL_METHODS);

    @Nested
    class Accept {

        @Test
        void throwsIllegalArgumentExceptionWhenInfoUrlIsMalformed() {
            doReturn("h:t:t:p:/malformed-url").when(dasnikoKeycloakContainerMock).getAuthServerUrl();
            assertThrows(IllegalArgumentException.class, () -> dasnikoKeycloakContainerMock.customize(TestcontainerInfo.builder().build()));
        }

    }

}