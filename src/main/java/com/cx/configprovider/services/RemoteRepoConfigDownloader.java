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

    /**
     * Downloads config-as-code contents from a remote source control repo. Scans a remote directory
     * (specified by {@link ConfigLocation#getPath()} non-recursively. Each file in the directory is treated
     * as config-as-code. If the path is null or empty, the repo root directory is scanned.
     * <p>
     * Currently no more than 1 config-as-code file is supported in a given directory.
     *
     * @param configLocation specifies a directory to scan and repo access properties.
     * @return a non-null instance of {@link RawConfigAsCode} with config-as-code content.
     * If config-as-code was not found, the instance contains an empty string.
     * @throws ConfigProviderException if more than 1 config-as-code file is found in the specified directory
     * @throws NullPointerException if configLocation or its repoLocation is null
     */
    @Override
    public RawConfigAsCode getConfigAsCode(ConfigLocation configLocation) {
        log.info("Searching for a config-as-code file in a remote repo");
        validate(configLocation);

        this.configLocation = configLocation;

        SourceControlClient client = determineSourceControlClient();
        List<String> filenames = client.getDirectoryFilenames(configLocation);
        String content = getFileContent(client, filenames);

        return RawConfigAsCode.builder()
                .content(content)
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
        if (filenames.isEmpty()) {
            log.info("No config-as-code was found.");
        } else if (filenames.size() == SUPPORTED_FILE_COUNT) {
            result = client.downloadFileContent(configLocation, filenames.get(0));
            log.info("Config-as-code was found with content length: {}", result.length());
        } else {
            throwInvalidCountException(filenames);
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
        Objects.requireNonNull(configLocation, "ConfigLocation must not be null.");
        Objects.requireNonNull(configLocation.getRepoLocation(), "RepositoryLocation must not be null.");
    }
}
