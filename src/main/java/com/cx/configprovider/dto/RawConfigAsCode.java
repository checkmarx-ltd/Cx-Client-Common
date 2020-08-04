package com.cx.configprovider.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a non-parsed ("raw") config-as-code.
 */
@Builder
@Getter
@Setter
public class RawConfigAsCode {
    private String content;
}
