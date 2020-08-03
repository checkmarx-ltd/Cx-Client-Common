package com.cx.configprovider.services;

import com.cx.configprovider.dto.ConfigLocation;
import com.cx.configprovider.dto.RemoteRepoLocation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
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
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
class GitHubClient implements SourceControlClient {
    private static final String GET_CONTENTS_TEMPLATE = "/repos/%s/%s/contents/%s";
    private static final String REF_SPECIFIER = "ref";
    private static final String API_V3_HEADER = "application/vnd.github.v3+json";
    private static final String API_V3_RAW_CONTENTS_HEADER = "application/vnd.github.v3.raw";
    private static final String ACCEPT_HEADER = "Accept";

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String downloadFileContent(ConfigLocation configLocation, String filename) {
        String result = null;
        try {
            String combinedPath = Paths.get(configLocation.getPath(), filename).toString();
            combinedPath = FilenameUtils.normalize(combinedPath, true);

            URI uri = createContentsUri(configLocation, combinedPath);

            HttpResponse response = getContentResponse(uri, API_V3_RAW_CONTENTS_HEADER);
            result = getTextFrom(response);
        } catch (Exception e) {
            log.warn("Error downloading file contents.", e);
        }
        return result;
    }

    @Override
    public List<String> getDirectoryFilenames(ConfigLocation configLocation) {
        List<String> result = Collections.emptyList();
        try {
            URI uri = createContentsUri(configLocation, configLocation.getPath());
            HttpResponse response = getContentResponse(uri, API_V3_HEADER);
            result = getFilenamesFrom(response);
        } catch (Exception e) {
            log.warn("Error downloading directory contents.", e);
        }

        log.info("Files found: {}.", result);
        return result;
    }

    private static HttpResponse getContentResponse(URI uri, String acceptHeaderValue) throws IOException {
        log.info("Getting the contents from {}", uri);
        return Request.Get(uri)
                .addHeader(ACCEPT_HEADER, acceptHeaderValue)
                .execute()
                .returnResponse();
    }

    private List<String> getFilenamesFrom(HttpResponse response) throws IOException {
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
        ArrayNode contents = (ArrayNode) objectMapper.readTree(responseText);

        return StreamSupport.stream(contents.spliterator(), false)
                .filter(node -> node.get("type").asText().equals("file"))
                .map(node -> node.get("name").asText())
                .collect(Collectors.toList());
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