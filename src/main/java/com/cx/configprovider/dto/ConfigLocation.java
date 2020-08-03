package com.cx.configprovider.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigLocation {
    private SourceProviderType sourceProviderType;
    private String path;
    private RemoteRepoLocation repoLocation;
}
