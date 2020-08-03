package com.cx.configprovider.services;

import com.cx.configprovider.dto.ConfigLocation;
import com.cx.configprovider.dto.RawConfigAsCode;
import com.cx.configprovider.dto.RemoteRepoLocation;
import org.junit.Test;

import java.net.MalformedURLException;

import static org.junit.Assert.assertNotNull;

public class RemoteRepoConfigDownloaderTest {
    @Test
    public void getConfigAsCode() {
        RemoteRepoConfigDownloader downloader = new RemoteRepoConfigDownloader();
        RemoteRepoLocation repoLocation = RemoteRepoLocation.builder()
                .apiBaseUrl("https://api.github.com")
                .repoName("Cx-FlowRepo")
                .namespace("cxflowtestuser")
                .ref("master")
                .build();

        ConfigLocation configLocation = ConfigLocation.builder()
                .path(".checkmarx")
                .repoLocation(repoLocation)
                .build();

        RawConfigAsCode config = downloader.getConfigAsCode(configLocation);
        assertNotNull("Config-as-code is null.", config);
    }
}