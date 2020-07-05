package com.cx.restclient.common;

import com.cx.restclient.dto.Results;
import com.cx.restclient.dto.ScanResults;

/**
 * Common functionality for vulnerability scanners.
 */
public interface Scanner {
    void init();

    Results createScan();

    Results waitForScanResults();

    Results getLatestScanResults();

    void close();
}
