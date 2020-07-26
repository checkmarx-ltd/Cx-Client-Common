package com.cx.restclient.general;

import com.cx.restclient.CxClientDelegator;
import com.cx.restclient.ast.dto.sast.AstSastResults;
import com.cx.restclient.ast.dto.sast.report.AstSastSummaryResults;
import com.cx.restclient.ast.dto.sast.AstSastConfig;
import com.cx.restclient.ast.dto.sast.report.Finding;
import com.cx.restclient.configuration.CxScanConfig;
import com.cx.restclient.dto.ScanResults;
import com.cx.restclient.dto.ScannerType;
import com.cx.restclient.dto.SourceLocationType;
import com.cx.restclient.ast.dto.common.RemoteRepositoryInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

@Slf4j
public class AstSastTests extends CommonClientTest {
    @Test
    public void scan_remotePublicRepo() throws MalformedURLException {
        CxScanConfig config = getScanConfig();

        CxClientDelegator client = new CxClientDelegator(config, log);
        try {
            client.init();
            ScanResults initialResults = client.initiateScan();
            validateInitialResults(initialResults);

            ScanResults finalResults = client.waitForScanResults();
            validateFinalResults(finalResults);
        } catch (Exception e) {
            failOnException(e);
        }
    }

    private void validateFinalResults(ScanResults finalResults) {
        Assert.assertNotNull("Final scan results are null.", finalResults);

        AstSastResults astSastResults = finalResults.getAstResults();
        Assert.assertNotNull("AST-SAST results are null.", astSastResults);
        Assert.assertTrue("Scan ID is missing.", StringUtils.isNotEmpty(astSastResults.getScanId()));

        validateFindings(astSastResults);
        validateSummary(astSastResults);
    }

    private void validateSummary(AstSastResults astSastResults) {
        AstSastSummaryResults summary = astSastResults.getSummary();
        Assert.assertNotNull("Summary is null.", summary);
        Assert.assertTrue("No medium-severity vulnerabilities.",
                summary.getMediumVulnerabilityCount() > 0);

        Assert.assertNotNull("Status counter list is null.", summary.getStatusCounters());
        Assert.assertFalse("No status counters.", summary.getStatusCounters().isEmpty());

        Assert.assertTrue("Expected total counter to be a positive value.", summary.getTotalCounter() > 0);

        int actualFindingCount = astSastResults.getFindings().size();
        Assert.assertEquals("Total finding count from summary doesn't correspond to the actual count.",
                actualFindingCount,
                summary.getTotalCounter());

        long actualFindingCountExceptInfo = astSastResults.getFindings()
                .stream()
                .filter(finding -> !StringUtils.equalsIgnoreCase(finding.getSeverity(), "info"))
                .count();

        int countFromSummaryExceptInfo = summary.getHighVulnerabilityCount()
                + summary.getMediumVulnerabilityCount()
                + summary.getLowVulnerabilityCount();

        Assert.assertEquals("Finding count from summary (excluding 'info') doesn't correspond to the actual count.",
                actualFindingCountExceptInfo,
                countFromSummaryExceptInfo);
    }

    private void validateFindings(AstSastResults astSastResults) {
        List<Finding> findings = astSastResults.getFindings();
        Assert.assertNotNull("Finding list is null.", findings);
        Assert.assertFalse("Finding list is empty.", findings.isEmpty());

        boolean someNodeListsAreEmpty = findings.stream().anyMatch(finding -> finding.getNodes().isEmpty());
        Assert.assertFalse("Some of the finding node lists are empty.", someNodeListsAreEmpty);
    }

    private void validateInitialResults(ScanResults initialResults) {
        Assert.assertNotNull("Initial scan results are null.", initialResults);
        Assert.assertNotNull("AST-SAST results are null.", initialResults.getAstResults());
        Assert.assertTrue("Scan ID is missing.", StringUtils.isNotEmpty(initialResults.getAstResults().getScanId()));
    }

    private static CxScanConfig getScanConfig() throws MalformedURLException {
        AstSastConfig astConfig = AstSastConfig.builder()
                .apiUrl(prop("astSast.apiUrl"))
                .sourceLocationType(SourceLocationType.REMOTE_REPOSITORY)
                .accessToken(prop("astSast.accessToken"))
                .build();

        RemoteRepositoryInfo repoInfo = new RemoteRepositoryInfo();
        URL repoUrl = new URL(prop("astSast.remoteRepoUrl.public"));
        repoInfo.setUrl(repoUrl);
        astConfig.setRemoteRepositoryInfo(repoInfo);
        astConfig.setResultsPageSize(10);
        astConfig.setPresetName("Default");

        CxScanConfig config = new CxScanConfig();
        config.setAstSastConfig(astConfig);
        config.setProjectName(prop("astSast.projectName"));
        config.addScannerType(ScannerType.AST_SAST);
        config.setOsaProgressInterval(5);
        return config;
    }
}
