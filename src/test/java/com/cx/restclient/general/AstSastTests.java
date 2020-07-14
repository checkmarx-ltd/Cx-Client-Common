package com.cx.restclient.general;

import com.cx.restclient.CxClientDelegator;
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
    public void initiateScan_remotePublicRepo() throws MalformedURLException {
        AstSastConfig astConfig = AstSastConfig.builder()
                .apiUrl(prop("astSast.apiUrl"))
                .sourceLocationType(SourceLocationType.REMOTE_REPOSITORY)
                .accessToken(prop("astSast.accessToken"))
                .build();

        RemoteRepositoryInfo repoInfo = new RemoteRepositoryInfo();
        URL repoUrl = new URL(prop("astSast.remoteRepoUrl.public"));
        repoInfo.setUrl(repoUrl);
        astConfig.setRemoteRepositoryInfo(repoInfo);


        CxScanConfig config = new CxScanConfig();
        config.setAstSastConfig(astConfig);
        config.setProjectName(prop("astSast.projectName"));
        config.addScannerType(ScannerType.AST_SAST);

        CxClientDelegator client = new CxClientDelegator(config, log);
        try {
            client.init();
            ScanResults scanResults = client.initiateScan();
            Assert.assertNotNull("Scan results are null.", scanResults);
            Assert.assertNotNull("AST-SAST results are null.", scanResults.getAstResults());
            Assert.assertTrue("Scan ID is missing", StringUtils.isNotEmpty(scanResults.getAstResults().getScanId()));
        } catch (Exception e) {
            failOnException(e);
        }
    }
}
