package com.cx.restclient.general;

import com.cx.restclient.CxClientDelegator;
import com.cx.restclient.configuration.CxScanConfig;
import com.cx.restclient.dto.DependencyScannerType;
import com.cx.restclient.dto.ScanResults;
import com.cx.restclient.exception.CxClientException;
import com.cx.restclient.sca.dto.RemoteRepositoryInfo;
import com.cx.restclient.sca.dto.SCAConfig;
import com.cx.restclient.sca.dto.SCAResults;
import com.cx.restclient.sca.dto.SourceLocationType;
import com.cx.restclient.sca.dto.report.Finding;
import com.cx.restclient.sca.dto.report.Package;
import com.cx.restclient.sca.dto.report.SCASummaryResults;
import com.cx.utility.TestingUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

@Slf4j
public class ScaScanTests extends CommonClientTest {

    // Storing the test project as an archive to avoid cluttering the current project
    // and also to prevent false positives during a vulnerability scan of the current project.
    private static final String PACKED_SOURCES_TO_SCAN = "sources-to-scan.zip";
    private static final String PUBLIC_REPO_PROP = "sca.remoteRepoUrl.public";
    private static final String PRIVATE_REPO_PROP = "sca.remoteRepoUrl.private";

    @Test
    public void scan_localDirUpload() throws IOException, CxClientException {
        CxScanConfig config = initScaConfig(false);
        config.setOsaThresholdsEnabled(true);
        config.getScaConfig().setSourceLocationType(SourceLocationType.LOCAL_DIRECTORY);

        Path sourcesDir = null;
        try {
            sourcesDir = extractTestProjectFromResources();
            config.setSourceDir(sourcesDir.toString());

            ScanResults scanResults = scanUsing(config);
            verifyScanResults(scanResults);
        } finally {
            deleteDir(sourcesDir);
        }
    }

    @Test
    public void scan_remotePublicRepo() throws MalformedURLException {
        scanRemoteRepo(PUBLIC_REPO_PROP, false);
    }

    @Test
    public void scan_remotePrivateRepo() throws MalformedURLException {
        scanRemoteRepo(PRIVATE_REPO_PROP, false);
    }

    @Test
    public void scan_onPremiseAuthentication() throws MalformedURLException {
        scanRemoteRepo(PUBLIC_REPO_PROP, true);
    }

    @Test
    @Ignore("Needs specific network configuration with a proxy.")
    public void runScaScanWithProxy() throws MalformedURLException, CxClientException {
        CxScanConfig config = initScaConfig(false);
        setProxy(config);
        ScanResults scanResults = scanUsing(config);
        verifyScanResults(scanResults);
    }

    private void scanRemoteRepo(String repoUrlProp, boolean useOnPremAuthentication) throws MalformedURLException {
        CxScanConfig config = initScaConfig(useOnPremAuthentication);
        config.getScaConfig().setSourceLocationType(SourceLocationType.REMOTE_REPOSITORY);
        RemoteRepositoryInfo repoInfo = new RemoteRepositoryInfo();

        URL repoUrl = new URL(props.getProperty(repoUrlProp));
        repoInfo.setUrl(repoUrl);

        config.getScaConfig().setRemoteRepositoryInfo(repoInfo);


        ScanResults scanResults = scanUsing(config);
        verifyScanResults(scanResults);
    }

    private Path extractTestProjectFromResources() {
        InputStream testProjectStream = getTestProjectStream();
        Path tempDirectory = createTempDirectory();
        extractResourceToDir(testProjectStream, tempDirectory);
        return tempDirectory;
    }

    private void extractResourceToDir(InputStream source, Path targetDir) {
        log.info("Unpacking sources into the temp dir.");
        int fileCount = 0;
        try (ArchiveInputStream inputStream = new ArchiveStreamFactory().createArchiveInputStream(source)) {
            ArchiveEntry entry;
            while ((entry = inputStream.getNextEntry()) != null) {
                if (!inputStream.canReadEntryData(entry)) {
                    throw new IOException(String.format("Unable to read entry: %s", entry));
                }
                Path fullTargetPath = targetDir.resolve(entry.getName());
                File targetFile = fullTargetPath.toFile();
                if (entry.isDirectory()) {
                    extractDirectory(targetFile);
                } else {
                    extractFile(inputStream, targetFile);
                    fileCount++;
                }
            }
        } catch (IOException | ArchiveException e) {
            failOnException(e);
        }
        log.info("Files extracted: {}", fileCount);
    }

    private static void extractFile(ArchiveInputStream inputStream, File targetFile) throws IOException {
        File parent = targetFile.getParentFile();
        extractDirectory(parent);
        try (OutputStream outputStream = Files.newOutputStream(targetFile.toPath())) {
            IOUtils.copy(inputStream, outputStream);
        }
    }

    private static void extractDirectory(File targetFile) throws IOException {
        if (!targetFile.isDirectory() && !targetFile.mkdirs()) {
            throw new IOException(String.format("Failed to create directory %s", targetFile));
        }
    }

    private static Path createTempDirectory() {
        String systemTempDir = FileUtils.getTempDirectoryPath();
        String subdir = String.format("common-client-tests-%s", UUID.randomUUID());
        Path result = Paths.get(systemTempDir, subdir);

        log.info("Creating a temp dir: {}", result);
        boolean success = result.toFile().mkdir();
        if (!success) {
            fail("Failed to create temp dir.");
        }
        return result;
    }

    private static void deleteDir(Path directory) {
        if (directory == null) {
            return;
        }

        log.info("Deleting '{}'", directory);
        try {
            FileUtils.deleteDirectory(directory.toFile());
        } catch (IOException e) {
            log.warn("Failed to delete temp dir.", e);
        }
    }

    private static InputStream getTestProjectStream() {
        String srcResourceName = ScaScanTests.PACKED_SOURCES_TO_SCAN;
        log.info("Getting resource stream from '{}'", srcResourceName);
        return Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream(srcResourceName);
    }

    private ScanResults scanUsing(CxScanConfig config) throws MalformedURLException, CxClientException {
        CxClientDelegator client = new CxClientDelegator(config, log);
        ScanResults results = null;
        try {
            client.init();
            client.createScan();
            results = client.waitForScanResults();
        } catch (Exception e) {
            failOnException(e);
        }
        return results;
    }

    private void verifyScanResults(ScanResults results) {
        assertNotNull("Scan results are null.", results);
        assertNull("OSA results are not null.", results.getDependencyScanResults().getOsaResults());

        SCAResults scaResults = results.getDependencyScanResults().getScaResults();
        assertNotNull("SCA results are null", scaResults);
        assertTrue("Scan ID is empty", StringUtils.isNotEmpty(scaResults.getScanId()));
        assertTrue("Web report link is empty", StringUtils.isNotEmpty(scaResults.getWebReportLink()));

        verifySummary(scaResults.getSummary());
        verifyPackages(scaResults);
        verifyFindings(scaResults);
    }

    private void verifySummary(SCASummaryResults summary) {
        assertNotNull("SCA summary is null", summary);
        assertTrue("SCA hasn't found any packages.", summary.getTotalPackages() > 0);

        boolean anyVulnerabilitiesDetected = summary.getHighVulnerabilityCount() > 0 ||
                summary.getMediumVulnerabilityCount() > 0 ||
                summary.getLowVulnerabilityCount() > 0;
        assertTrue("Expected that at least one vulnerability would be detected.", anyVulnerabilitiesDetected);
    }

    private void verifyPackages(SCAResults scaResults) {
        List<Package> packages = scaResults.getPackages();

        assertNotNull("Packages are null.", packages);
        assertFalse("Response contains no packages.", packages.isEmpty());

        assertEquals("Actual package count differs from package count in summary.",
                scaResults.getSummary().getTotalPackages(),
                packages.size());
    }

    private void verifyFindings(SCAResults scaResults) {
        List<Finding> findings = scaResults.getFindings();
        SCASummaryResults summary = scaResults.getSummary();
        assertNotNull("Findings are null", findings);
        assertFalse("Response contains no findings.", findings.isEmpty());

        // Special check due to a case-sensitivity issue.
        boolean allSeveritiesAreSpecified = findings.stream()
                .allMatch(finding -> finding.getSeverity() != null);

        assertTrue("Some of the findings have severity set to null.", allSeveritiesAreSpecified);
    }

    private static CxScanConfig initScaConfig(boolean useOnPremAuthentication) {
        CxScanConfig config = new CxScanConfig();
        config.setDependencyScannerType(DependencyScannerType.SCA);
        config.setSastEnabled(false);
        config.setProjectName(props.getProperty("sca.projectName"));

        SCAConfig sca = TestingUtils.getScaConfig(props, useOnPremAuthentication);
        config.setScaConfig(sca);

        return config;
    }
}
