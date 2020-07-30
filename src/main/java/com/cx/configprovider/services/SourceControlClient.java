package com.cx.configprovider.services;

import com.cx.configprovider.dto.ConfigLocation;

import java.util.List;

interface SourceControlClient {
    String downloadFileContent(ConfigLocation configLocation, String filename);

    List<String> getDirectoryFilenames(ConfigLocation configLocation);
}
