package com.cx.configprovider.services;

import com.cx.configprovider.dto.ConfigLocation;
import com.cx.configprovider.dto.RemoteRepoLocation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AUTH;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.utils.URIBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Spliterator;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
class GitHubClient implements SourceControlClient {
    private static final String GET_CONTENTS_TEMPLATE = "/repos/%s/%s/contents/%s";
    private static final String REF_SPECIFIER = "ref";
    private static final String ACCEPT_HEADER = "Accept";

    // Allows to get directory content response as an object with the 'entries' field (instead of just an array
    // of entries). This simplifies response handling.
    private static final String API_V3_OBJECT_HEADER = "application/vnd.github.v3.object";

    // Allows to get file contents as is (without any JSON wrapper).
    private static final String API_V3_RAW_CONTENTS_HEADER = "application/vnd.github.v3.raw";

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String downloadFileContent(ConfigLocation configLocation, String filename) {
        String result = null;
        log.info("Downloading file content for '{}'", filename);
        try {
            String combinedPath = Paths.get(configLocation.getPath(), filename).toString();
            combinedPath = FilenameUtils.normalize(combinedPath, true);

            URI uri = createContentsUri(configLocation, combinedPath);

            HttpResponse response = getContentResponse(uri, API_V3_RAW_CONTENTS_HEADER, configLocation);
            result = getTextFrom(response);
        } catch (Exception e) {
            log.warn("Error downloading file contents", e);
        }
        return result;
    }

    @Override
    public List<String> getDirectoryFilenames(ConfigLocation configLocation) {
        List<String> result = Collections.emptyList();
        String effectivePath = normalize(configLocation.getPath());
        try {
            URI uri = createContentsUri(configLocation, effectivePath);
            HttpResponse response = getContentResponse(uri, API_V3_OBJECT_HEADER, configLocation);
            result = getFilenamesFrom(response);
        } catch (Exception e) {
            log.warn("Error downloading directory contents", e);
        }

        log.info("Files found: {}", result);
        return result;
    }

    private static String normalize(String path) {
        String result = StringUtils.defaultString(path);
        if (result.isEmpty()) {
            log.info("Getting filenames from the root directory");
        } else {
            log.info("Getting filenames from the '{}' directory", result);
        }
        return result;
    }

    private static HttpResponse getContentResponse(URI uri, String acceptHeaderValue, ConfigLocation configLocation)
            throws IOException {
        log.debug("Getting the contents from {}", uri);

        Request request = getRequestWithAuth(configLocation, Request.Get(uri));

        return request.addHeader(ACCEPT_HEADER, acceptHeaderValue)
                .execute()
                .returnResponse();
    }

    private static Request getRequestWithAuth(ConfigLocation configLocation, Request request) {
        String accessToken = configLocation.getRepoLocation().getAccessToken();
        if (StringUtils.isNotEmpty(accessToken)) {
            log.debug("Using an access token");
            String authHeaderValue = String.format("token %s", accessToken);
            request.addHeader(AUTH.WWW_AUTH_RESP, authHeaderValue);
        } else {
            log.debug("Access token is not specified");
        }
        return request;
    }

    private static List<String> getFilenamesFrom(HttpResponse response) throws IOException {
        List<String> result = Collections.emptyList();

        int statusCode = response.getStatusLine().getStatusCode();
        String responseText = getTextFrom(response);

        if (statusCode == HttpStatus.SC_OK) {
            result = extractFilenamesFromJson(responseText);
        } else {
            log.warn("Error loading filenames. The response status is '{}'. Make sure that namespace, repo name " +
                    "and path are correct and that you have access to the repo.", response.getStatusLine());
        }
        return result;
    }

    private static List<String> extractFilenamesFromJson(String responseText) throws JsonProcessingException {
        List<String> result;
        JsonNode content = objectMapper.readTree(responseText);
        String contentType = content.get("type").asText();
        boolean contentIsDirectory = contentType.equals("dir");
        if (contentIsDirectory) {
            // Expecting the following response structure:
            // { "type": "dir", "entries": [<one entry per file>], ... }        //NOSONAR
            Spliterator<JsonNode> directoryEntries = content.get("entries").spliterator();

            result = StreamSupport.stream(directoryEntries, false)
                    .filter(node -> node.get("type").asText().equals("file"))
                    .map(node -> node.get("name").asText())
                    .collect(Collectors.toList());
        } else {
            log.warn("The specified path doesn't refer to a directory. Please provide a valid directory path");
            result = Collections.emptyList();
        }
        return result;
    }

    private static URI createContentsUri(ConfigLocation configLocation, String directoryPath) throws
            URISyntaxException {
        RemoteRepoLocation repo = configLocation.getRepoLocation();

        String path = String.format(GET_CONTENTS_TEMPLATE,
                repo.getNamespace(), repo.getRepoName(), directoryPath);

        return new URIBuilder(repo.getApiBaseUrl())
                .setPath(path)
                .setParameter(REF_SPECIFIER, repo.getRef())
                .build();
    }

    private static String getTextFrom(HttpResponse response) throws IOException {
        String result = null;
        InputStream contentStream = response.getEntity() != null ? response.getEntity().getContent() : null;
        if (contentStream != null) {
            result = IOUtils.toString(contentStream, StandardCharsets.UTF_8);
        }
        log.trace("Response body: {}", result);
        return result;
    }
}