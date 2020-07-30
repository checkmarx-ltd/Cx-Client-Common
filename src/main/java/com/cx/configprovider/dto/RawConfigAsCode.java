package com.cx.configprovider.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class RawConfigAsCode {
    private String fileContent;
}
