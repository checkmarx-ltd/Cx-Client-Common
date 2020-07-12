package com.cx.restclient.general;

import com.cx.restclient.configuration.CxScanConfig;
import com.cx.restclient.dto.ScanResults;
import com.cx.restclient.sca.dto.RemoteRepositoryInfo;
import com.cx.restclient.sca.dto.SCAResults;
import com.cx.restclient.sca.dto.SourceLocationType;
import com.cx.restclient.sca.dto.report.Finding;
import com.cx.restclient.sca.dto.report.Package;
import com.cx.restclient.sca.dto.report.SCASummaryResults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static org.junit.Assert.*;

@Slf4j
public  abstract class AbstractScaScanTests extends CommonClientTest {

    // Storing the test project as an archive to avoid cluttering the current project
    // and also to prevent false positives during a vulnerability scan of the current project.
    protected static final String PACKED_SOURCES_TO_SCAN = "sources-to-scan.zip";
    protected static final String PUBLIC_REPO_PROP = "sca.remoteRepoUrl.public";
    protected static final String PRIVATE_REPO_PROP = "sca.remoteRepoUrl.private";


    @Test
    public void scan_remotePublicRepo() throws MalformedURLException {
        scanRemoteRepo(PUBLIC_REPO_PROP, false);
    }

    @Test
    public void scan_remotePrivateRepo() throws MalformedURLException {
        scanRemoteRepo(PRIVATE_REPO_PROP, false);
    }

    protected abstract void scanRemoteRepo(String privateRepoProp, boolean b) throws MalformedURLException;


    
    protected CxScanConfig initScaConfig(String repoUrlProp, boolean useOnPremAuthentication) throws MalformedURLException {
        CxScanConfig config = initScaConfig(useOnPremAuthentication);
        config.getScaConfig().setSourceLocationType(SourceLocationType.REMOTE_REPOSITORY);
        RemoteRepositoryInfo repoInfo = new RemoteRepositoryInfo();

        URL repoUrl = new URL(props.getProperty(repoUrlProp));
        repoInfo.setUrl(repoUrl);

        config.getScaConfig().setRemoteRepositoryInfo(repoInfo);
        return config;
    }

    protected void verifyScanResults(ScanResults results) {
        
        assertNotNull("Scan results are null.", results);
        assertNull("OSA results are not null.", results.getOsaResults());

        SCAResults scaResults = results.getScaResults();
        assertNotNull("SCA results are null", scaResults);
        
        System.out.println("scanID " + scaResults.getScanId());
        assertTrue("Scan ID is empty", StringUtils.isNotEmpty(scaResults.getScanId()));
        assertTrue("Web report link is empty", StringUtils.isNotEmpty(scaResults.getWebReportLink()));

        
        verifySummary(scaResults.getSummary());
        verifyPackages(scaResults);
        verifyFindings(scaResults);
    }
    
    private void verifySummary(SCASummaryResults summary) {

        assertNotNull("SCA summary is null", summary);
        System.out.println("summary.getTotalPackages() " + summary.getTotalPackages());
        assertTrue("SCA hasn't found any packages.", summary.getTotalPackages() > 0);

        System.out.println("summary.getHighVulnerabilityCount() " + summary.getHighVulnerabilityCount());
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
}
