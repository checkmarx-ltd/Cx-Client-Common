package com.cx.configprovider.services;

import com.cx.configprovider.dto.ConfigLocation;
import com.cx.configprovider.dto.RawConfigAsCode;
import com.cx.configprovider.dto.RemoteRepoLocation;
import com.cx.configprovider.dto.SourceProviderType;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import static org.junit.Assert.*;

public class RemoteRepoConfigDownloaderTest {
    @Test
    public void getConfigAsCode_directoryWithSingleFile() {
        RawConfigAsCode config = getConfigFromPath(".checkmarx");
        assertNotNull("Config-as-code object is null.", config);
        assertTrue("Config-as-code file content is empty.", StringUtils.isNotEmpty(config.getFileContent()));
    }

    @Test
    public void getConfigAsCode_nonExistingPath() {
        RawConfigAsCode config = getConfigFromPath("inexistence");
        assertNotNull("Config-as-code object is null.", config);
        assertNull("Config-as-code file content is not null.", config.getFileContent());
    }

    private RawConfigAsCode getConfigFromPath(String path) {
        RemoteRepoLocation repoLocation = RemoteRepoLocation.builder()
                .apiBaseUrl("https://api.github.com")
                .repoName("Cx-FlowRepo")
                .namespace("cxflowtestuser")
                .ref("master")
                .build();

        ConfigLocation location = ConfigLocation.builder()
                .path(path)
                .sourceProviderType(SourceProviderType.GITHUB)
                .repoLocation(repoLocation)
                .build();
        RemoteRepoConfigDownloader downloader = new RemoteRepoConfigDownloader();

        return downloader.getConfigAsCode(location);
    }
}