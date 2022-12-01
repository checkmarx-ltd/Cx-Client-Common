package com.cx.restclient.astglue;

import org.slf4j.Logger;

import com.cx.restclient.configuration.CxScanConfig;
import com.cx.restclient.dto.ScannerType;

public interface TransformerFactory {

	public TransformerService create(ScannerType from, ScannerType to, CxScanConfig cxConfig, Logger log);
}
