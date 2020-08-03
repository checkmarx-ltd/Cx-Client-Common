package com.cx.configprovider.services;

import com.cx.configprovider.dto.ConfigLocation;
import com.cx.configprovider.dto.RawConfigAsCode;
import com.cx.configprovider.dto.SourceProviderType;
import com.cx.restclient.exception.CxClientException;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;

@Slf4j
class RemoteRepoConfigDownloader implements ConfigLoader {
    private static final int SUPPORTED_FILE_COUNT = 1;

    @Override
    public RawConfigAsCode getConfigAsCode(ConfigLocation configLocation) {
        log.info("Searching for a config-as-code file in a remote git repo");
        validate(configLocation);
        SourceControlClient client = determineSourceControlClient(configLocation.getSourceProviderType());
        List<String> filenames = client.getDirectoryFilenames(configLocation);
        String content = getFileContent(client, configLocation, filenames);

        if (content == null) {
            log.info("No config-as-code was found.");
        } else {
            log.info("Config-as-code was found with content length: {}", content.length());
        }

        return RawConfigAsCode.builder()
                .fileContent(content)
                .build();
    }

    private SourceControlClient determineSourceControlClient(SourceProviderType sourceProviderType) {
        SourceControlClient result;
        log.debug("Determining the client for the {} source control provider", sourceProviderType);
        // TODO: use a reflection-based mechanism.
        if (sourceProviderType == SourceProviderType.GITHUB) {
            result = new GitHubClient();
        } else {
            String message = String.format("The '%s' SourceProviderType is not supported", sourceProviderType);
            throw new CxClientException(message);
        }
        log.debug("Using {} to access the repo", result.getClass());
        return result;
    }

    private String getFileContent(SourceControlClient client, ConfigLocation configLocation, List<String> filenames) {
        String content = null;
        if (filenames.size() == SUPPORTED_FILE_COUNT) {
            content = client.downloadFileContent(configLocation, filenames.get(0));
        } else if (filenames.size() > SUPPORTED_FILE_COUNT) {
            throwInvalidCountException(configLocation, filenames);
        }
        else {
            log.info("No config-as-code was found.");
        }
        return content;
    }

    private static void throwInvalidCountException(ConfigLocation configLocation, List<String> filenames) {
        String message = String.format(
                "Found %d files in the '%s' directory. Only %d config-as-code file is currently supported.",
                filenames.size(),
                configLocation.getPath(),
                SUPPORTED_FILE_COUNT);
        throw new CxClientException(message);
    }

    private static void validate(ConfigLocation configLocation) {
        Objects.requireNonNull(configLocation, "ConfigLocation must be provided.");
        Objects.requireNonNull(configLocation.getRepoLocation(), "Repository info must be specified.");
    }
}
