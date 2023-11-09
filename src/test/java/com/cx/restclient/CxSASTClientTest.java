package com.cx.restclient;

import java.net.MalformedURLException;
import com.cx.restclient.configuration.CxScanConfig;
import com.cx.restclient.dto.CxVersion;
import junit.framework.TestCase;
import com.cx.restclient.httpClient.CxHttpClient;

public class CxSASTClientTest extends TestCase {

	public void testGetContentTypeAndApiVersion_ScanWithSettings_Sast96() throws MalformedURLException {
		CxScanConfig config = new CxScanConfig();
		CxVersion cxVersion = new CxVersion();
		cxVersion.setVersion("9.6");
		config.setCxVersion(cxVersion);
		String apiName = "sast/scanWithSettings";
		String customFields = "{\"custom1\":\"value1\"}";
		config.setCustomFields(customFields);
		config.setPostScanActionId(1);
		CxSASTClient cxSASTClient = new CxSASTClient(null, null, config);
		String apiVersion = cxSASTClient.getContentTypeAndApiVersion(config, apiName);
		assertEquals("Expected API version for valid SAST version and scanWithSettings with custom fields and PostScanActionId",
				"application/json;v=1.2", apiVersion);
	}

	public void testGetContentTypeAndApiVersion_ScanWithSettings_CustomFields_Sast96() throws MalformedURLException {
		CxScanConfig config = new CxScanConfig();
		CxVersion cxVersion = new CxVersion();
		cxVersion.setVersion("9.6");
		config.setCxVersion(cxVersion);
		String apiName = "sast/scanWithSettings";
		String customFields = "{\"custom1\":\"value1\"}";
		config.setCustomFields(customFields);
		config.setPostScanActionId(null);
		CxSASTClient cxSASTClient = new CxSASTClient(null, null, config);
		String apiVersion = cxSASTClient.getContentTypeAndApiVersion(config, apiName);
		assertEquals("Expected API version for valid SAST version and scanWithSettings with custom fields only",
				"application/json;v=1.2", apiVersion);
	}

	public void testGetContentTypeAndApiVersion_ScanWithSettings_PostScanActionId_Sast96()
			throws MalformedURLException {
		CxScanConfig config = new CxScanConfig();
		CxVersion cxVersion = new CxVersion();
		cxVersion.setVersion("9.6");
		config.setCxVersion(cxVersion);
		String apiName = "sast/scanWithSettings";
		config.setCustomFields(null);
		config.setPostScanActionId(1);
		CxSASTClient cxSASTClient = new CxSASTClient(null, null, config);
		String apiVersion = cxSASTClient.getContentTypeAndApiVersion(config, apiName);
		assertEquals("Expected API version for valid SAST version and scanWithSettings with PostScanActionId only",
				"application/json;v=1.2", apiVersion);
	}

	public void testGetContentTypeAndApiVersion_UnknownApiName() throws MalformedURLException {
		CxScanConfig config = new CxScanConfig();
		CxVersion cxVersion = new CxVersion();
		cxVersion.setVersion("9.4.1");
		config.setCxVersion(cxVersion);
		String apiName = "createScanReport";
		CxSASTClient cxSASTClient = new CxSASTClient(null, null, config);
		String apiVersion = cxSASTClient.getContentTypeAndApiVersion(config, apiName);
		assertEquals("Expected default API version for an unknown API name", "application/json;v=1.0", apiVersion);
	}

	public void testGetContentTypeAndApiVersion_ScanWithSettings_Sast94() throws MalformedURLException {
		CxScanConfig config = new CxScanConfig();
		CxVersion cxVersion = new CxVersion();
		cxVersion.setVersion("9.4");
		config.setCxVersion(cxVersion);
		String apiName = "sast/scanWithSettings";
		CxSASTClient cxSASTClient = new CxSASTClient(null, null, config);
		String apiVersion = cxSASTClient.getContentTypeAndApiVersion(config, apiName);
		assertEquals("Expected API version for SAST version 9.4", "application/json;v=1.0", apiVersion);
	}

	public void testGetContentTypeAndApiVersion_ScanWithSettings_Sast95() throws MalformedURLException {
		CxScanConfig config = new CxScanConfig();
		CxVersion cxVersion = new CxVersion();
		cxVersion.setVersion("9.5");
		config.setCxVersion(cxVersion);
		String apiName = "sast/scanWithSettings";
		CxSASTClient cxSASTClient = new CxSASTClient(null, null, config);
		String apiVersion = cxSASTClient.getContentTypeAndApiVersion(config, apiName);
		assertEquals("Expected API version for SAST version 9.5", "application/json;v=1.0", apiVersion);
	}

	public void testGetContentTypeAndApiVersion_ScanWithSettings_CustomFields_Sast94() throws MalformedURLException {
		CxScanConfig config = new CxScanConfig();
		CxVersion cxVersion = new CxVersion();
		cxVersion.setVersion("9.4");
		config.setCxVersion(cxVersion);
		String apiName = "sast/scanWithSettings";
		String customFields = "{\"custom1\":\"value1\"}";
		config.setCustomFields(customFields);
		CxSASTClient cxSASTClient = new CxSASTClient(null, null, config);
		String apiVersion = cxSASTClient.getContentTypeAndApiVersion(config, apiName);
		assertEquals("Expected API version for SAST version 9.4 with scanWithSettings and custom fields",
				"application/json;v=1.2", apiVersion);
	}

	public void testGetContentTypeAndApiVersion_ScanWithSettings_PostScanActionId_Sast94()
			throws MalformedURLException {
		CxScanConfig config = new CxScanConfig();
		CxVersion cxVersion = new CxVersion();
		cxVersion.setVersion("9.4");
		config.setCxVersion(cxVersion);
		String apiName = "sast/scanWithSettings";
		config.setPostScanActionId(1);
		CxSASTClient cxSASTClient = new CxSASTClient(null, null, config);
		String apiVersion = cxSASTClient.getContentTypeAndApiVersion(config, apiName);
		assertEquals("Expected API version for SAST version 9.4 with scanWithSettings and PostScanActionId",
				"application/json;v=1.2", apiVersion);
	}
}