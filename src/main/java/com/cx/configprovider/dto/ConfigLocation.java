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
    private static final String DEFAULT_SEARCH_DIRECTORY = ".checkmarx";

    /**
     * Path to a directory. The directory will be scanned for config-as-code files.
     */
    @Builder.Default
    private String path = DEFAULT_SEARCH_DIRECTORY;

    private RemoteRepoLocation repoLocation;
}
