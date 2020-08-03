package com.cx.configprovider.services;

import com.cx.configprovider.dto.ConfigLocation;
import com.cx.configprovider.dto.RemoteRepoLocation;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.utils.URIBuilder;

import java.io.InputStream;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
class GitHubClient implements SourceControlClient {
    private static final String GET_CONTENTS_TEMPLATE = "/repos/%s/%s/contents/%s";
    private static final String REF_SPECIFIER = "ref";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String downloadFileContent(ConfigLocation configLocation, String filename) {
        return "content stub";
    }

    @Override
    public List<String> getDirectoryFilenames(ConfigLocation configLocation) {
        List<String> result;
        try {
            RemoteRepoLocation repo = configLocation.getRepoLocation();

            String path = String.format(GET_CONTENTS_TEMPLATE,
                    repo.getNamespace(), repo.getRepoName(), configLocation.getPath());

            URI uri = new URIBuilder(repo.getApiBaseUrl())
                    .setPath(path)
                    .setParameter(REF_SPECIFIER, repo.getRef())
                    .build();

            HttpResponse response = Request.Get(uri)
                    .execute()
                    .returnResponse();

            InputStream responseStream = response.getEntity().getContent();
            ArrayNode contents = (ArrayNode) objectMapper.readTree(responseStream);
            log.debug("Response: {}", contents);

            result = StreamSupport.stream(contents.spliterator(), false)
                    .filter(node -> node.get("type").asText().equals("file"))
                    .map(node -> node.get("name").asText())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("Error downloading directory contents.", e);
            result = Collections.emptyList();
        }
        return result;
    }
}
