package com.cx.restclient.astglue;

import com.cx.restclient.configuration.CxScanConfig;
import com.cx.restclient.dto.ScannerType;

public class EngineParamsTransformerServiceFactory implements TransformerFactory{

	@Override
	public void create(ScannerType from, ScannerType to, CxScanConfig cxConfig) {
		if(from.getDisplayName().equals(ScannerType.CXONE_SAST.getDisplayName()) {
			return new TransformerServiceImpl();
		}
	}

}
