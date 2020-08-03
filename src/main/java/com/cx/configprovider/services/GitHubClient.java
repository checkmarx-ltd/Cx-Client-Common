package com.cx.configprovider.services;

import com.cx.configprovider.dto.ConfigLocation;
import com.cx.configprovider.dto.RemoteRepoLocation;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.utils.URIBuilder;

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
            log.debug("Downloading file content from {}", uri);

            result = Request.Get(uri)
                    .addHeader(ACCEPT_HEADER, API_V3_RAW_CONTENTS_HEADER)
                    .execute()
                    .returnContent()
                    .asString(StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.warn("Error downloading file contents.", e);
        }
        return result;
    }

    @Override
    public List<String> getDirectoryFilenames(ConfigLocation configLocation) {
        List<String> result;
        try {
            URI uri = createContentsUri(configLocation, configLocation.getPath());
            log.debug("Downloading directory contents from {}", uri);

            HttpResponse response = Request.Get(uri)
                    .addHeader(ACCEPT_HEADER, API_V3_HEADER)
                    .execute()
                    .returnResponse();

            InputStream responseStream = response.getEntity().getContent();
            ArrayNode contents = (ArrayNode) objectMapper.readTree(responseStream);
            log.trace("Directory content response: {}", contents);

            result = StreamSupport.stream(contents.spliterator(), false)
                    .filter(node -> node.get("type").asText().equals("file"))
                    .map(node -> node.get("name").asText())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("Error downloading directory contents.", e);
            result = Collections.emptyList();
        }
        log.debug("Files found: {}.", result);
        return result;
    }

    private static URI createContentsUri(ConfigLocation configLocation, String directoryPath) throws URISyntaxException {
        RemoteRepoLocation repo = configLocation.getRepoLocation();

        String path = String.format(GET_CONTENTS_TEMPLATE,
                repo.getNamespace(), repo.getRepoName(), directoryPath);

        return new URIBuilder(repo.getApiBaseUrl())
                .setPath(path)
                .setParameter(REF_SPECIFIER, repo.getRef())
                .build();
    }
}
