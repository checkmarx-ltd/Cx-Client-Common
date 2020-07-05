package com.cx.restclient.common;

import com.cx.restclient.dto.ScanResults;

/**
 * Common functionality for vulnerability scanners.
 */
public interface IScanner {
    void init();

    ScanResults createScan();

    ScanResults waitForScanResults();

    ScanResults getLatestScanResults();

    void close();
}
