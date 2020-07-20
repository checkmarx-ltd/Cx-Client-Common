package com.cx.restclient.general;

import com.cx.restclient.CxClientDelegator;
import com.cx.restclient.ast.dto.common.ASTSummaryResults;
import com.cx.restclient.ast.dto.sast.AstSastConfig;
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

@Slf4j
public class AstSastTests extends CommonClientTest {
    @Test
    public void scan_remotePublicRepo() throws MalformedURLException {
        CxScanConfig config = getScanConfig();

        CxClientDelegator client = new CxClientDelegator(config, log);
        try {
            client.init();
            ScanResults initialResults = client.initiateScan();
            Assert.assertNotNull("Initial scan results are null.", initialResults);
            Assert.assertNotNull("AST-SAST results are null.", initialResults.getAstResults());
            Assert.assertTrue("Scan ID is missing.", StringUtils.isNotEmpty(initialResults.getAstResults().getScanId()));

            ScanResults finalResults = client.waitForScanResults();
            Assert.assertNotNull("Final scan results are null.", finalResults);
            Assert.assertNotNull("AST-SAST results are null.", finalResults.getAstResults());

            ASTSummaryResults summary = finalResults.getAstResults().getSummary();
            Assert.assertNotNull("Summary is null.", summary);

            Assert.assertTrue("No medium-severity vulnerabilities.",
                    summary.getMediumVulnerabilityCount() > 0);
        } catch (Exception e) {
            failOnException(e);
        }
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
        repoInfo.setBranch("master");
        astConfig.setRemoteRepositoryInfo(repoInfo);

        CxScanConfig config = new CxScanConfig();
        config.setAstSastConfig(astConfig);
        config.setProjectName(prop("astSast.projectName"));
        config.addScannerType(ScannerType.AST_SAST);
        config.setPresetName("Default");
        config.setOsaProgressInterval(5);
        return config;
    }
}
