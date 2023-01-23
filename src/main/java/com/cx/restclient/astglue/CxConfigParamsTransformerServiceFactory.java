package com.cx.restclient.astglue;

import org.slf4j.Logger;

import com.cx.restclient.configuration.CxScanConfig;
import com.cx.restclient.dto.ScannerType;

public class CxConfigParamsTransformerServiceFactory implements TransformerFactory{

	@Override
	public TransformerService create(ScannerType from, ScannerType to, CxScanConfig cxConfig, Logger log) {
		if (to.getDisplayName().equals(ScannerType.CXONE_SAST.getDisplayName())
				&& from.getDisplayName().equals(ScannerType.SAST.getDisplayName())) {
			return new CxOneSastTransformerServiceImpl(cxConfig, log);
		} 
		return null;
	}

}
