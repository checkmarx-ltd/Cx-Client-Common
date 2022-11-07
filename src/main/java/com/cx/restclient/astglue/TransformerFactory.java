package com.cx.restclient.astglue;

import com.cx.restclient.configuration.CxScanConfig;
import com.cx.restclient.dto.ScannerType;

public interface TransformerFactory {

	public void create(ScannerType from, ScannerType to, CxScanConfig cxConfig);
}
