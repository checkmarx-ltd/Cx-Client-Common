package com.cx.restclient.common;

import com.cx.restclient.CxClientWrapper;
import com.cx.restclient.dto.DependencyScanResults;
import com.cx.restclient.dto.IResults;
import com.cx.restclient.dto.ScanResults;
import com.cx.restclient.exception.CxClientException;
import com.cx.restclient.httpClient.CxHttpClient;

/**
 * Dependency Scanner is an umbrella term for OSA and SCA.
 */
public interface IScanner {
    void init() throws CxClientException;

    IResults createScan() throws CxClientException;

    IResults waitForScanResults() throws CxClientException, InterruptedException;

    IResults getLatestScanResults() throws CxClientException,  InterruptedException ;

    CxHttpClient getHttpClient();
    
}
