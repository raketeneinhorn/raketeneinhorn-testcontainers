package com.raketeneinhorn.testcontainers.actuate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.net.URL;

@Builder
@Getter
public class TestcontainerInfo {

    @JsonIgnore
    private String beanName;
    private String dockerImageName;

    @Setter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private URL httpEntrypoint;

}
