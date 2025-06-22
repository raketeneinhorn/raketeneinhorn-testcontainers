package com.raketeneinhorn.testcontainers.container.keycloak;

import com.raketeneinhorn.testcontainers.actuate.TestcontainerInfo;
import com.raketeneinhorn.testcontainers.actuate.TestcontainerInfoCustomizer;
import dasniko.testcontainers.keycloak.ExtendableKeycloakContainer;

import java.net.URI;
import java.util.Map;

public class DasnikoKeycloakContainer extends ExtendableKeycloakContainer<DasnikoKeycloakContainer> implements TestcontainerInfoCustomizer {

    @Override
    public void customize(TestcontainerInfo testcontainerInfo) {
        testcontainerInfo.setHttpEntrypoints(Map.of(
            "authServerUrl", URI.create(this.getAuthServerUrl()),
            "mgmtServerUrl", URI.create(this.getMgmtServerUrl())
        ));
    }

}
