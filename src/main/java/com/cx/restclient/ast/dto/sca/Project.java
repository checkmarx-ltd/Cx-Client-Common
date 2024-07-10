package com.cx.restclient.ast.dto.sca;

import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class Project {
    private String name;
    private String id;
    private Object Tags;
}
