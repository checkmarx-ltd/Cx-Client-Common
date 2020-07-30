package com.cx.configprovider.services;

import com.cx.configprovider.dto.ConfigLocation;
import com.cx.configprovider.dto.RawConfigAsCode;
import com.cx.restclient.ast.dto.common.RemoteRepositoryInfo;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.Assert.assertNotNull;

class RemoteRepoConfigDownloaderTest {
    @Test
    public void getConfigAsCode() throws MalformedURLException {
        RemoteRepoConfigDownloader downloader = new RemoteRepoConfigDownloader();
        RemoteRepositoryInfo repoInfo = RemoteRepositoryInfo.builder()
                .url(new URL("https://github.com/cxflowtestuser/Cx-FlowRepo.git"))
                .build();

        ConfigLocation configLocation = ConfigLocation.builder()
                .path(".checkmarx")
                .repoInfo(repoInfo)
                .build();

        RawConfigAsCode config = downloader.getConfigAsCode(configLocation);
        assertNotNull("Config-as-code is null.", config);
    }
}