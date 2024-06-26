package com.cx.restclient.general;

import com.cx.restclient.ast.dto.common.RemoteRepositoryInfo;
import com.cx.restclient.ast.dto.sca.AstScaConfig;
import com.cx.restclient.ast.dto.sca.AstScaResults;
import com.cx.restclient.ast.dto.sca.report.AstScaSummaryResults;
import com.cx.restclient.ast.dto.sca.report.Finding;
import com.cx.restclient.ast.dto.sca.report.Package;
import com.cx.restclient.configuration.CxScanConfig;
import com.cx.restclient.dto.ScanResults;
import com.cx.restclient.dto.ScannerType;
import com.cx.restclient.dto.SourceLocationType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static org.junit.Assert.*;

@Slf4j
public  abstract class ScaTestsBase extends CommonClientTest {
    // Storing the test project as an archive to avoid cluttering the current project
    // and also to prevent false positives during a vulnerability scan of the current project.
    protected static final String PACKED_SOURCES_TO_SCAN = "sources-to-scan.zip";
    protected static final String PUBLIC_REPO_PROP = "astSca.remoteRepoUrl.public";
    protected static final String PRIVATE_REPO_PROP = "astSca.remoteRepoUrl.private";

    protected CxScanConfig initScaConfig(String repoUrlProp, boolean useOnPremAuthentication) throws MalformedURLException {
        CxScanConfig config = initScaConfig(useOnPremAuthentication);
        config.getAstScaConfig().setSourceLocationType(SourceLocationType.REMOTE_REPOSITORY);
        RemoteRepositoryInfo repoInfo = new RemoteRepositoryInfo();

        URL repoUrl = new URL(prop(repoUrlProp));
        repoInfo.setUrl(repoUrl);
        repoInfo.setUsername(prop("astSca.remoteRepo.private.token"));

        config.getAstScaConfig().setRemoteRepositoryInfo(repoInfo);
        return config;
    }

    protected CxScanConfig initScaConfig(boolean useOnPremAuthentication){
        CxScanConfig config = new CxScanConfig();
        config.addScannerType(ScannerType.AST_SCA);
        config.setSastEnabled(false);
        config.setProjectName(prop("astSca.projectName"));
        config.setOsaProgressInterval(5);
        AstScaConfig sca = getScaConfig(useOnPremAuthentication);
        config.setAstScaConfig(sca);

        return config;
    }

    protected void verifyScanResults(ScanResults results) {
        assertNotNull("Scan results are null.", results);
        assertNull("OSA results are not null.", results.getOsaResults());

        AstScaResults scaResults = results.getScaResults();
        assertNotNull("SCA results are null", scaResults);
        
        log.info("scanID " + scaResults.getScanId());
        assertTrue("Scan ID is empty", StringUtils.isNotEmpty(scaResults.getScanId()));
        assertTrue("Web report link is empty", StringUtils.isNotEmpty(scaResults.getWebReportLink()));

        verifySummary(scaResults.getSummary());
        verifyPackages(scaResults);
        verifyFindings(scaResults);
    }
    
    private void verifySummary(AstScaSummaryResults summary) {

        assertNotNull("SCA summary is null", summary);
        assertTrue("SCA hasn't found any packages.", summary.getTotalPackages() > 0);

        boolean anyVulnerabilitiesDetected = summary.getCriticalVulnerabilityCount() > 0 ||
        		summary.getHighVulnerabilityCount() > 0 ||
                summary.getMediumVulnerabilityCount() > 0 ||
                summary.getLowVulnerabilityCount() > 0;
        assertTrue("Expected that at least one vulnerability would be detected.", anyVulnerabilitiesDetected);
    }

    private void verifyPackages(AstScaResults scaResults) {
        List<Package> packages = scaResults.getPackages();

        assertNotNull("Packages are null.", packages);
        assertFalse("Response contains no packages.", packages.isEmpty());

        assertEquals("Actual package count differs from package count in summary.",
                scaResults.getSummary().getTotalPackages(),
                packages.size());
    }

    private void verifyFindings(AstScaResults scaResults) {
        List<Finding> findings = scaResults.getFindings();
        assertNotNull("Findings are null", findings);
        assertFalse("Response contains no findings.", findings.isEmpty());

        // Special check due to a case-sensitivity issue.
        boolean allSeveritiesAreSpecified = findings.stream()
                .allMatch(finding -> finding.getSeverity() != null);

        assertTrue("Some of the findings have severity set to null.", allSeveritiesAreSpecified);
    }
}
