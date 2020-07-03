package com.cx.restclient.common;

import com.cx.restclient.dto.IResults;
import com.cx.restclient.exception.CxClientException;
import com.cx.restclient.httpClient.CxHttpClient;

import java.io.IOException;

/**
 * Dependency Scanner is an umbrella term for OSA and SCA.
 */
public interface IScanner {
    public void init() ;

    public IResults createScan() ;

    public IResults waitForScanResults() throws  InterruptedException;

    public IResults getLatestScanResults() throws  InterruptedException ;
    
    public void login() throws IOException;

    void close();
}
