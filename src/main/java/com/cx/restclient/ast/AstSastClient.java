package com.cx.restclient.ast;

import com.cx.restclient.ast.dto.common.*;
import com.cx.restclient.ast.dto.sast.AstSastConfig;
import com.cx.restclient.ast.dto.sast.SastScanConfigValue;
import com.cx.restclient.common.Scanner;
import com.cx.restclient.configuration.CxScanConfig;
import com.cx.restclient.dto.Results;
import com.cx.restclient.dto.ScanResults;
import com.cx.restclient.dto.ScannerType;
import com.cx.restclient.dto.SourceLocationType;
import com.cx.restclient.exception.CxClientException;
import com.cx.restclient.httpClient.CxHttpClient;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AUTH;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.Optional;

public class AstSastClient extends AstClient implements Scanner {
    private static final String ENGINE_TYPE_FOR_API = "sast";
    private static final String REF_TYPE_BRANCH = "branch";

    public AstSastClient(CxScanConfig config, Logger log) {
        super(config, log);

        AstSastConfig astConfig = this.config.getAstSastConfig();
        validate(astConfig);

        // Make sure we won't get URLs like "http://example.com//api/scans".
        String normalizedUrl = StringUtils.stripEnd(astConfig.getApiUrl(), "/");

        httpClient = createHttpClient(normalizedUrl);
    }

    @Override
    public void init() {
        log.debug(String.format("Initializing %s client.", getScannerDisplayName()));
        AstSastConfig astConfig = config.getAstSastConfig();
        httpClient.addCustomHeader(AUTH.WWW_AUTH_RESP, String.format("Bearer %s", astConfig.getAccessToken()));
    }

    @Override
    protected String getScannerDisplayName() {
        return ScannerType.AST_SAST.getDisplayName();
    }

    @Override
    public Results initiateScan() {
        log.info(String.format("----------------------------------- Initiating %s Scan:------------------------------------",
                getScannerDisplayName()));

        ASTResults astResults = new ASTResults();
        AstSastConfig astConfig = config.getAstSastConfig();
        try {
            SourceLocationType locationType = astConfig.getSourceLocationType();
            HttpResponse response;
            if (locationType == SourceLocationType.REMOTE_REPOSITORY) {
                response = submitSourcesFromRemoteRepo(astConfig, config.getProjectName());
            } else {
                throw new NotImplementedException("The upload flow is not yet supported.");
            }
            String scanId = extractScanIdFrom(response);
            astResults.setScanId(scanId);
            return astResults;
        } catch (IOException e) {
            throw new CxClientException("Error creating scan.", e);
        }
    }

    @Override
    protected ScanConfig getScanConfig() {
        boolean isIncremental = Boolean.TRUE.equals(config.getAstSastConfig().isIncremental());
        String presetName = StringUtils.defaultIfEmpty(config.getAstSastConfig().getPresetName(), "");

        ScanConfigValue configValue = SastScanConfigValue.builder()
                .incremental(Boolean.toString(isIncremental))
                .presetName(presetName)
                .build();

        return ScanConfig.builder()
                .type(ENGINE_TYPE_FOR_API)
                .value(configValue)
                .build();
    }

    @Override
    protected HandlerRef getBranchToScan(RemoteRepositoryInfo repoInfo) {
        // The HandlerRef object is mandatory for AST-SAST even if no branch is specified in repoInfo.
        return HandlerRef.builder()
                .type(REF_TYPE_BRANCH)
                .value(repoInfo.getBranch())
                .build();
    }

    @Override
    public ScanResults waitForScanResults() {
        return null;
    }

    @Override
    public ScanResults getLatestScanResults() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close() {
        Optional.ofNullable(httpClient).ifPresent(CxHttpClient::close);
    }

    private void validate(ASTConfig astSastConfig) {
        log.debug("Validating config.");
        String error = null;
        if (astSastConfig == null) {
            error = "%s config must be provided.";
        } else if (StringUtils.isBlank(astSastConfig.getApiUrl())) {
            error = "%s API URL must be provided.";
        }

        if (error != null) {
            throw new IllegalArgumentException(String.format(error, getScannerDisplayName()));
        }
    }
}
