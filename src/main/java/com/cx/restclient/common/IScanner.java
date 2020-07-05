package com.cx.restclient.common;

import com.cx.restclient.dto.IResults;
import com.cx.restclient.dto.ScanResults;

import java.io.IOException;

/**
 * Dependency Scanner is an umbrella term for OSA and SCA.
 */
public interface IScanner {
    public void init() ;

    public ScanResults createScan() ;

    public ScanResults waitForScanResults() ;

    public ScanResults getLatestScanResults()  ;
    
    public void login() throws IOException;

    public void close();
}
