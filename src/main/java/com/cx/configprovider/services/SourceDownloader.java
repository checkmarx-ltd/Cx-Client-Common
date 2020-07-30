package com.cx.configprovider.services;

import com.cx.configprovider.dto.ConfigLocation;

interface SourceDownloader {
    String downloadFileContent(ConfigLocation configLocation);
}
