package com.cx.configprovider.services;

import com.cx.configprovider.dto.ConfigLocation;

import java.util.Collections;
import java.util.List;

class GitHubClient implements SourceControlClient {
    @Override
    public String downloadFileContent(ConfigLocation configLocation, String filename) {
        return "content stub";
    }

    @Override
    public List<String> getDirectoryFilenames(ConfigLocation configLocation) {
        return Collections.singletonList("filename-stub");
    }
}
