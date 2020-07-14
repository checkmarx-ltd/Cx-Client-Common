package com.cx.restclient.ast;

import com.cx.restclient.ast.dto.common.*;
import com.cx.restclient.configuration.CxScanConfig;
import com.cx.restclient.dto.SourceLocationType;
import com.cx.restclient.exception.CxClientException;
import com.cx.restclient.httpClient.CxHttpClient;
import com.cx.restclient.httpClient.utils.ContentType;
import com.cx.restclient.httpClient.utils.HttpClientHelper;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.entity.StringEntity;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;

public abstract class AstClient {
    private static final String LOCATION_HEADER = "Location";
    private static final String CREDENTIAL_TYPE_PASSWORD = "password";

    protected final CxScanConfig config;
    protected final Logger log;

    protected CxHttpClient httpClient;

    public AstClient(CxScanConfig config, Logger log) {
        validate(config, log);
        this.config = config;
        this.log = log;
    }

    protected abstract String getScannerDisplayName();

    protected abstract ScanConfig getScanConfig();

    protected abstract HandlerRef getBranchToScan(RemoteRepositoryInfo repoInfo);

    protected CxHttpClient createHttpClient(String baseUrl) {
        log.debug("Creating HTTP client.");
        return new CxHttpClient(baseUrl,
                config.getCxOrigin(),
                config.isDisableCertificateValidation(),
                config.isUseSSOLogin(),
                null,
                config.getProxyConfig(),
                log);
    }

    private void validate(CxScanConfig config, Logger log) {
        if (config == null && log == null) {
            throw new CxClientException("Both scan config and log must be provided.");
        }
    }

    protected HttpResponse sendStartScanRequest(RemoteRepositoryInfo repoInfo,
                                                SourceLocationType sourceLocation,
                                                String projectId) throws IOException {
        log.debug("Constructing the 'start scan' request");

        ScanStartHandler handler = getScanStartHandler(repoInfo);

        ProjectToScan project = ProjectToScan.builder()
                .id(projectId)
                .type(sourceLocation.getApiValue())
                .handler(handler)
                .build();

        List<ScanConfig> apiScanConfig = Collections.singletonList(getScanConfig());

        StartScanRequest request = StartScanRequest.builder()
                .project(project)
                .config(apiScanConfig)
                .build();

        StringEntity entity = HttpClientHelper.convertToStringEntity(request);

        log.info("Sending a request to start scan.");
        return httpClient.postRequest(UrlPaths.CREATE_SCAN, ContentType.CONTENT_TYPE_APPLICATION_JSON, entity,
                HttpResponse.class, HttpStatus.SC_CREATED, "start the scan");
    }

    protected HttpResponse submitSourcesFromRemoteRepo(ASTConfig config, String projectId) throws IOException {
        log.info("Using remote repository flow.");
        RemoteRepositoryInfo repoInfo = config.getRemoteRepositoryInfo();
        validateRepoInfo(repoInfo);

        URL sanitizedUrl = sanitize(repoInfo.getUrl());
        log.info(String.format("Repository URL: %s", sanitizedUrl));
        return sendStartScanRequest(repoInfo, SourceLocationType.REMOTE_REPOSITORY, projectId);
    }

    /**
     * @param repoInfo may represent an actual git repo or a presigned URL of an uploaded archive.
     */
    private ScanStartHandler getScanStartHandler(RemoteRepositoryInfo repoInfo) {
        log.debug("Creating the handler object.");

        HandlerRef ref = getBranchToScan(repoInfo);

        GitCredentials credentials = GitCredentials.builder()
                .type(CREDENTIAL_TYPE_PASSWORD)
                .value(repoInfo.getPassword())
                .build();

        URL effectiveRepoUrl = getEffectiveRepoUrl(repoInfo);

        // The ref/username/credentials properties are mandatory even if not specified in repoInfo.
        return ScanStartHandler.builder()
                .ref(ref)
                .username(repoInfo.getUsername())
                .credentials(credentials)
                .url(effectiveRepoUrl.toString())
                .build();
    }

    protected URL getEffectiveRepoUrl(RemoteRepositoryInfo repoInfo) {
        return repoInfo.getUrl();
    }

    /**
     * Removes the userinfo part of the input URL (if present), so that the URL may be logged safely.
     * The URL may contain userinfo when a private repo is scanned.
     */
    private static URL sanitize(URL url) throws MalformedURLException {
        return new URL(url.getProtocol(), url.getHost(), url.getFile());
    }

    private void validateRepoInfo(RemoteRepositoryInfo repoInfo) {
        log.debug("Validating remote repository info.");
        if (repoInfo == null) {
            String message = String.format(
                    "%s must be provided in %s configuration when using source location of type %s.",
                    RemoteRepositoryInfo.class.getName(),
                    getScannerDisplayName(),
                    SourceLocationType.REMOTE_REPOSITORY.name());

            throw new CxClientException(message);
        }
    }

    protected String extractScanIdFrom(HttpResponse response) {
        String result = null;
        log.debug(String.format("Extracting scan ID from the '%s' response header.", LOCATION_HEADER));
        if (response != null && response.getLastHeader(LOCATION_HEADER) != null) {
            // Expecting values like
            //      /api/scans/1ecffa00-0e42-49b2-8755-388b9f6a9293
            //      /07e5b4b0-184a-458e-9d82-7f3da407f940
            String urlPathWithScanId = response.getLastHeader(LOCATION_HEADER).getValue();
            result = FilenameUtils.getName(urlPathWithScanId);
        }

        if (StringUtils.isNotEmpty(result)) {
            log.info(String.format("Scan started successfully. Scan ID: %s", result));
        } else {
            throw new CxClientException("Unable to get scan ID.");
        }
        return result;
    }
}
