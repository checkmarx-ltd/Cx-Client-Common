package com.cx.configprovider.services;

import com.cx.configprovider.dto.ConfigLocation;
import com.cx.configprovider.dto.RawConfigAsCode;
import com.cx.configprovider.dto.RemoteRepoLocation;
import com.cx.configprovider.dto.SourceProviderType;
import com.cx.restclient.exception.CxClientException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import static org.junit.Assert.*;

@Slf4j
public class RemoteRepoConfigDownloaderTest {
    @Test
    public void getConfigAsCode_directoryWithSingleFile_hasContent() {
        RawConfigAsCode config = getConfigFromPath(".checkmarx");
        assertTrue("Config-as-code file content is empty.", StringUtils.isNotEmpty(config.getFileContent()));
    }

    @Test
    public void getConfigAsCode_nonExistingPath_noContent() {
        RawConfigAsCode config = getConfigFromPath("inexistence");
        assertNull("Config-as-code file content is not null.", config.getFileContent());
    }

    @Test
    public void getConfigAsCode_fileInsteadOfDirectory_noContent() {
        RawConfigAsCode config = getConfigFromPath(".checkmarx/config-as-code.yml");
        assertNull("Config-as-code file content is not null.", config.getFileContent());
    }

    @Test
    public void getConfigAsCode_directoryWithMultipleFiles_exception() {
        try {
            RawConfigAsCode config = getConfigFromPath("config-as-code-test");
            fail("Expected an exception to be thrown.");
        } catch (Exception e) {
            log.info("Caught an exception.", e);
            assertEquals("Unexpected exception type.", CxClientException.class, e.getClass());
        }
    }

    private static RawConfigAsCode getConfigFromPath(String path) {
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

        RawConfigAsCode result = downloader.getConfigAsCode(location);
        assertNotNull("Config-as-code object is null.", result);

        return result;
    }
}