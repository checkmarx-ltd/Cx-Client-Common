package com.cx.configprovider.services;

import com.cx.configprovider.dto.ConfigLocation;
import com.cx.configprovider.dto.RawConfigAsCode;

import java.util.Objects;

public class RemoteRepoConfigDownloader {
    public RawConfigAsCode getConfigAsCode(ConfigLocation configLocation) {
        validate(configLocation);

        SourceDownloader downloader = new GitHubDownloader();
        String content = downloader.downloadFileContent(configLocation);

        return RawConfigAsCode.builder()
                .fileContent(content)
                .build();
    }

    private static void validate(ConfigLocation configLocation) {
        Objects.requireNonNull(configLocation, "ConfigLocation must be provided.");
        Objects.requireNonNull(configLocation.getRepoInfo(), "Repository info must be specified.");
    }
}
