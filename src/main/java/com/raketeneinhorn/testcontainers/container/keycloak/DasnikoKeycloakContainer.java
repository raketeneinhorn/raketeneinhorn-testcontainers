package com.raketeneinhorn.testcontainers.container.keycloak;

import com.raketeneinhorn.testcontainers.actuate.TestcontainerInfo;
import com.raketeneinhorn.testcontainers.actuate.TestcontainerInfoCustomizer;
import dasniko.testcontainers.keycloak.ExtendableKeycloakContainer;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class DasnikoKeycloakContainer extends ExtendableKeycloakContainer<DasnikoKeycloakContainer> implements TestcontainerInfoCustomizer {

    @Override
    public void accept(TestcontainerInfo testcontainerInfo) {
        Map<String,URL> httpEntrypoints = new HashMap<>();
        try {
            httpEntrypoints.put("authServerUrl", URI.create(this.getAuthServerUrl()).toURL());
            httpEntrypoints.put("mgmtServerUrl", URI.create(this.getMgmtServerUrl()).toURL());
        } catch (MalformedURLException ex) {
            throw new IllegalArgumentException(ex);
        }

        testcontainerInfo.setHttpEntrypoints(httpEntrypoints);
    }

}
