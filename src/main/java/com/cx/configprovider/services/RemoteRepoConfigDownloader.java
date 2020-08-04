package com.cx.configprovider.services;

import com.cx.configprovider.dto.ConfigLocation;
import com.cx.configprovider.dto.RawConfigAsCode;
import com.cx.configprovider.dto.SourceProviderType;
import com.cx.configprovider.exceptions.ConfigProviderException;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.util.EnumMap;
import java.util.List;
import java.util.Objects;

@Slf4j
class RemoteRepoConfigDownloader implements ConfigLoader {
    private static final int SUPPORTED_FILE_COUNT = 1;

    private static final EnumMap<SourceProviderType, Class<? extends SourceControlClient>> sourceProviderMapping;

    static {
        sourceProviderMapping = new EnumMap<>(SourceProviderType.class);
        sourceProviderMapping.put(SourceProviderType.GITHUB, GitHubClient.class);
    }

    private ConfigLocation configLocation;

    @Override
    public RawConfigAsCode getConfigAsCode(ConfigLocation configLocation) {
        log.info("Searching for a config-as-code file in a remote git repo");
        validate(configLocation);

        this.configLocation = configLocation;

        SourceControlClient client = determineSourceControlClient();
        List<String> filenames = client.getDirectoryFilenames(configLocation);
        String content = getFileContent(client, filenames);

        return RawConfigAsCode.builder()
                .fileContent(content)
                .build();
    }

    private SourceControlClient determineSourceControlClient() {
        SourceProviderType providerType = configLocation.getRepoLocation().getSourceProviderType();
        log.debug("Determining the client for the {} source control provider", providerType);

        Class<? extends SourceControlClient> clientClass = getClientClass(providerType);
        SourceControlClient result = getClientInstance(clientClass);

        log.debug("Using {} to access the repo", result.getClass().getName());
        return result;
    }

    private static SourceControlClient getClientInstance(Class<? extends SourceControlClient> clientClass) {
        SourceControlClient result;
        try {
            result = clientClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            String message = String.format("Unable to create an instance of %s.",
                    SourceProviderType.class.getSimpleName());
            throw new ConfigProviderException(message, e);
        }
        return result;
    }

    private static Class<? extends SourceControlClient> getClientClass(SourceProviderType sourceProviderType) {
        Class<? extends SourceControlClient> clientClass = sourceProviderMapping.get(sourceProviderType);
        if (clientClass == null) {
            String message = String.format("The '%s' %s is not supported",
                    sourceProviderType,
                    SourceProviderType.class.getSimpleName());
            throw new ConfigProviderException(message);
        }
        return clientClass;
    }

    private String getFileContent(SourceControlClient client, List<String> filenames) {
        String result = "";
        if (filenames.size() == SUPPORTED_FILE_COUNT) {
            result = client.downloadFileContent(configLocation, filenames.get(0));
            log.info("Config-as-code was found with content length: {}", result.length());
        } else if (filenames.size() > SUPPORTED_FILE_COUNT) {
            throwInvalidCountException(filenames);
        } else {
            log.info("No config-as-code was found.");
        }
        return result;
    }

    private void throwInvalidCountException(List<String> filenames) {
        String message = String.format(
                "Found %d files in the '%s' directory. Only %d config-as-code file is currently supported.",
                filenames.size(),
                configLocation.getPath(),
                SUPPORTED_FILE_COUNT);
        throw new ConfigProviderException(message);
    }

    private static void validate(ConfigLocation configLocation) {
        Objects.requireNonNull(configLocation, "ConfigLocation must be provided.");
        Objects.requireNonNull(configLocation.getRepoLocation(), "Repository info must be specified.");
    }
}
