package com.cx.restclient.sca.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ASTConfig implements Serializable {
    private String apiUrl;
    private String token;
    private String preset;
    private String incremental;

}
