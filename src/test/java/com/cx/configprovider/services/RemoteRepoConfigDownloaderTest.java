package com.cx.configprovider.services;

import com.cx.configprovider.dto.ConfigLocation;
import com.cx.configprovider.dto.RawConfigAsCode;
import com.cx.configprovider.dto.RemoteRepoLocation;
import com.cx.configprovider.dto.SourceProviderType;
import com.cx.configprovider.exceptions.ConfigProviderException;
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
        assertNonEmptyContent(config);
    }

    @Test
    public void getConfigAsCode_deepDirectory_hasContent() {
        RawConfigAsCode config = getConfigFromPath("deep/directory/structure");
        assertNonEmptyContent(config);
    }

    @Test
    public void getConfigAsCode_nonExistingPath_emptyContent() {
        RawConfigAsCode config = getConfigFromPath("inexistence");
        assertEmptyContent(config);
    }

    @Test
    public void getConfigAsCode_fileInsteadOfDirectory_emptyContent() {
        RawConfigAsCode config = getConfigFromPath(".checkmarx/config-as-code.yml");
        assertEmptyContent(config);
    }

    @Test
    public void getConfigAsCode_directoryWithoutFiles_emptyContent() {
        RawConfigAsCode config = getConfigFromPath("deep/directory");
        assertEmptyContent(config);
    }

    @Test
    public void getConfigAsCode_emptyFile_emptyContent() {
        RawConfigAsCode config = getConfigFromPath("directory-with-empty-file");
        assertEmptyContent(config);
    }

    @Test
    public void getConfigAsCode_directoryWithMultipleFiles_exception() {
        try {
            getConfigFromPath("config-as-code-test");
            fail("Expected an exception to be thrown.");
        } catch (Exception e) {
            log.info("Caught an exception.", e);
            assertEquals("Unexpected exception type.", ConfigProviderException.class, e.getClass());
        }
    }

    private static void assertEmptyContent(RawConfigAsCode config) {
        Assert.assertTrue("Expected Config-as-code file content to be empty.", config.getFileContent().isEmpty());
    }

    private static void assertNonEmptyContent(RawConfigAsCode config) {
        assertTrue("Config-as-code file content is empty.", StringUtils.isNotEmpty(config.getFileContent()));
    }

    private static RawConfigAsCode getConfigFromPath(String path) {
        RemoteRepoLocation repoLocation = RemoteRepoLocation.builder()
                .apiBaseUrl("https://api.github.com")
                .repoName("Cx-FlowRepo")
                .namespace("cxflowtestuser")
                .ref("master")
                .accessToken(props.getProperty("github.token"))
                .sourceProviderType(SourceProviderType.GITHUB)
                .build();

        ConfigLocation location = ConfigLocation.builder()
                .path(path)
                .repoLocation(repoLocation)
                .build();

        RemoteRepoConfigDownloader downloader = new RemoteRepoConfigDownloader();

        RawConfigAsCode result = downloader.getConfigAsCode(location);
        assertNotNull("Config-as-code object must always be non-null.", result);
        assertNotNull("File content must always be non-null.", result.getFileContent());

        return result;
    }
}