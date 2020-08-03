package com.cx.configprovider.services;

import com.cx.configprovider.dto.ConfigLocation;
import com.cx.configprovider.dto.RawConfigAsCode;
import com.cx.configprovider.dto.RemoteRepoLocation;
import com.cx.configprovider.dto.SourceProviderType;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class RemoteRepoConfigDownloaderTest {
    @Test
    public void getConfigAsCode_directoryWithSingleFile() {
        RemoteRepoLocation repoLocation = RemoteRepoLocation.builder()
                .apiBaseUrl("https://api.github.com")
                .repoName("Cx-FlowRepo")
                .namespace("cxflowtestuser")
                .ref("master")
                .build();

        ConfigLocation configLocation = ConfigLocation.builder()
                .path(".checkmarx")
                .sourceProviderType(SourceProviderType.GITHUB)
                .repoLocation(repoLocation)
                .build();

        RemoteRepoConfigDownloader downloader = new RemoteRepoConfigDownloader();
        RawConfigAsCode config = downloader.getConfigAsCode(configLocation);
        assertNotNull("Config-as-code is null.", config);
        assertTrue("Config-as-code file content is empty.", StringUtils.isNotEmpty(config.getFileContent()));
    }
}