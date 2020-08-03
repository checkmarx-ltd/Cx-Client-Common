package com.cx.configprovider.services;

import com.cx.configprovider.dto.ConfigLocation;
import com.cx.configprovider.dto.RawConfigAsCode;
import com.cx.configprovider.dto.RemoteRepoLocation;
import com.cx.configprovider.dto.SourceProviderType;
import com.cx.restclient.exception.CxClientException;
import com.cx.utility.TestPropertyLoader;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Properties;

import static org.junit.Assert.*;

@Slf4j
public class RemoteRepoConfigDownloaderTest {
    static Properties props;

    @BeforeClass
    public static void loadProperties() {
        TestPropertyLoader propertyLoader = new TestPropertyLoader();
        props = propertyLoader.getProperties();
    }

    @Test
    public void getConfigAsCode_directoryWithSingleFile_hasContent() {
        RawConfigAsCode config = getConfigFromPath(".checkmarx");
        assertNonEmpty(config);
    }

    @Test
    public void getConfigAsCode_deepDirectory_hasContent() {
        RawConfigAsCode config = getConfigFromPath("deep/directory/structure");
        assertNonEmpty(config);
    }

    @Test
    public void getConfigAsCode_nonExistingPath_noContent() {
        RawConfigAsCode config = getConfigFromPath("inexistence");
        assertNull(config);
    }

    @Test
    public void getConfigAsCode_fileInsteadOfDirectory_noContent() {
        RawConfigAsCode config = getConfigFromPath(".checkmarx/config-as-code.yml");
        assertNull(config);
    }

    @Test
    public void getConfigAsCode_directoryWithoutFiles_noContent() {
        RawConfigAsCode config = getConfigFromPath("deep/directory");
        assertNull(config);
    }

    @Test
    public void getConfigAsCode_directoryWithMultipleFiles_exception() {
        try {
            getConfigFromPath("config-as-code-test");
            fail("Expected an exception to be thrown.");
        } catch (Exception e) {
            log.info("Caught an exception.", e);
            assertEquals("Unexpected exception type.", CxClientException.class, e.getClass());
        }
    }

    private static void assertNull(RawConfigAsCode config) {
        Assert.assertNull("Config-as-code file content is not null.", config.getFileContent());
    }

    private static void assertNonEmpty(RawConfigAsCode config) {
        assertTrue("Config-as-code file content is empty.", StringUtils.isNotEmpty(config.getFileContent()));
    }

    private static RawConfigAsCode getConfigFromPath(String path) {
        RemoteRepoLocation repoLocation = RemoteRepoLocation.builder()
                .apiBaseUrl("https://api.github.com")
                .repoName("Cx-FlowRepo")
                .namespace("cxflowtestuser")
                .ref("master")
                .accessToken(props.getProperty("github.token"))
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