package com.cx.restclient.common;

import com.cx.restclient.dto.IResults;
import com.cx.restclient.exception.CxClientException;
import com.cx.restclient.httpClient.CxHttpClient;

import java.io.IOException;

/**
 * Dependency Scanner is an umbrella term for OSA and SCA.
 */
public interface IScanner {
    public void init() throws CxClientException;

    public IResults createScan() throws CxClientException;

    public IResults waitForScanResults() throws CxClientException, InterruptedException;

    public IResults getLatestScanResults() throws CxClientException,  InterruptedException ;

    public CxHttpClient getHttpClient();
    
    public void login() throws IOException;
}
