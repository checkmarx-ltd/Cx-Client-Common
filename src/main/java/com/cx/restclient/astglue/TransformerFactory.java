package com.cx.restclient.astglue;

import com.cx.restclient.configuration.CxScanConfig;
import com.cx.restclient.dto.ScannerType;

public interface TransformerFactory {

	public TransformerService create(ScannerType from, ScannerType to, CxScanConfig cxConfig);
}
