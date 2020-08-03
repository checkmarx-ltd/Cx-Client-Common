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
public class RemoteRepoLocation {
    private String apiBaseUrl;
    private String namespace;
    private String repoName;
    private String ref;
}

