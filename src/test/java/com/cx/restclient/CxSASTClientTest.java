package com.cx.restclient;

import java.net.MalformedURLException;
import com.cx.restclient.configuration.CxScanConfig;
import com.cx.restclient.dto.CxVersion;
import junit.framework.TestCase;

public class CxSASTClientTest extends TestCase {
	public void testGetContentTypeAndApiVersionWithValidSastVersionAndSastRetentionRate() throws MalformedURLException {
		CxScanConfig config = new CxScanConfig();
		CxVersion cxVersion = new CxVersion();
		cxVersion.setVersion("9.6");
		config.setCxVersion(cxVersion);
		String apiName = "projects/{id}/dataRetentionSettings";
		config.setEnableDataRetention(true);

		CxSASTClient cxSASTClient = new CxSASTClient(config, null);
		String apiVersion = cxSASTClient.getContentTypeAndApiVersion(config, apiName);
		assertEquals("Expected API version for valid SAST version and SAST_RETENTION_RATE", "application/json;v=1.1",
				apiVersion);
	}

	public void testGetContentTypeAndApiVersionWithValidSastVersionAndScanWithSettings() throws MalformedURLException {
		CxScanConfig config = new CxScanConfig();
		CxVersion cxVersion = new CxVersion();
		cxVersion.setVersion("9.6");
		config.setCxVersion(cxVersion);
		String apiName = "sast/scanWithSettings";
		String customFields = "{\"custom1\":\"value1\"}";
		config.setCustomFields(customFields);
		config.setPostScanActionId(1);
		CxSASTClient cxSASTClient = new CxSASTClient(config, null);
		String apiVersion = cxSASTClient.getContentTypeAndApiVersion(config, apiName);
		assertEquals(
				"Expected API version for valid SAST version and scanWithSettings with custom fields and PostScanActionId",
				"application/json;v=1.2", apiVersion);
	}

	public void testGetContentTypeAndApiVersionWithValidSastVersionAndScanWithSettingsCustomFieldsOnly()
			throws MalformedURLException {
		CxScanConfig config = new CxScanConfig();
		CxVersion cxVersion = new CxVersion();
		cxVersion.setVersion("9.6");
		config.setCxVersion(cxVersion);
		String apiName = "sast/scanWithSettings";
		String customFields = "{\"custom1\":\"value1\"}";
		config.setCustomFields(customFields);
		config.setPostScanActionId(null);
		CxSASTClient cxSASTClient = new CxSASTClient(config, null);
		String apiVersion = cxSASTClient.getContentTypeAndApiVersion(config, apiName);
		assertEquals("Expected API version for valid SAST version and scanWithSettings with custom fields only",
				"application/json;v=1.2", apiVersion);
	}

	public void testGetContentTypeAndApiVersionWithValidSastVersionAndScanWithSettingsPostScanActionIdOnly()
			throws MalformedURLException {
		CxScanConfig config = new CxScanConfig();
		CxVersion cxVersion = new CxVersion();
		cxVersion.setVersion("9.6");
		config.setCxVersion(cxVersion);
		String apiName = "sast/scanWithSettings";
		config.setCustomFields(null);
		config.setPostScanActionId(1);
		CxSASTClient cxSASTClient = new CxSASTClient(config, null);
		String apiVersion = cxSASTClient.getContentTypeAndApiVersion(config, apiName);
		assertEquals("Expected API version for valid SAST version and scanWithSettings with PostScanActionId only",
				"application/json;v=1.2", apiVersion);
	}

	public void testGetContentTypeAndApiVersionWithSastVersionIn92To93RangeAndSastRetentionRate()
			throws MalformedURLException {
		CxScanConfig config = new CxScanConfig();
		CxVersion cxVersion = new CxVersion();
		cxVersion.setVersion("9.3.5");
		config.setCxVersion(cxVersion);
		String apiName = "projects/{id}/dataRetentionSettings";
		CxSASTClient cxSASTClient = new CxSASTClient(config, null);
		String apiVersion = cxSASTClient.getContentTypeAndApiVersion(config, apiName);
		assertEquals("Expected API version for SAST version in the 9.2 to 9.3 range and SAST_RETENTION_RATE",
				"application/json;v=1.0", apiVersion);
	}

	public void testGetContentTypeAndApiVersionWithNullSastVersion() throws MalformedURLException {
		CxScanConfig config = new CxScanConfig();
		CxVersion cxVersion = new CxVersion();
		cxVersion.setVersion(null);
		config.setCxVersion(cxVersion);
		String apiName = "projects/{id}/dataRetentionSettings";
		CxSASTClient cxSASTClient = new CxSASTClient(config, null);
		String apiVersion = cxSASTClient.getContentTypeAndApiVersion(config, apiName);
		assertEquals("Expected API version when SAST version is null", "application/json;v=1.0", apiVersion);
	}

	public void testGetContentTypeAndApiVersionWithInvalidSastVersion() throws MalformedURLException {
		CxScanConfig config = new CxScanConfig();
		CxVersion cxVersion = new CxVersion();
		cxVersion.setVersion("9.1.5");
		config.setCxVersion(cxVersion);
		String apiName = "projects/{id}/dataRetentionSettings";
		CxSASTClient cxSASTClient = new CxSASTClient(config, null);
		String apiVersion = cxSASTClient.getContentTypeAndApiVersion(config, apiName);
		assertEquals("Expected default API version for an invalid SAST version", "application/json;v=1.0", apiVersion);
	}

	public void testGetContentTypeAndApiVersionWithUnknownApiName() throws MalformedURLException {
		CxScanConfig config = new CxScanConfig();
		CxVersion cxVersion = new CxVersion();
		cxVersion.setVersion("9.4.1");
		config.setCxVersion(cxVersion);
		String apiName = "createScanReport";
		CxSASTClient cxSASTClient = new CxSASTClient(config, null);
		String apiVersion = cxSASTClient.getContentTypeAndApiVersion(config, apiName);
		assertEquals("Expected default API version for an unknown API name", "application/json;v=1.0", apiVersion);
	}

	public void testGetContentTypeAndApiVersionWithSastVersion9_4() throws MalformedURLException {
		CxScanConfig config = new CxScanConfig();
		CxVersion cxVersion = new CxVersion();
		cxVersion.setVersion("9.4");
		config.setCxVersion(cxVersion);
		String apiName = "projects/{id}/dataRetentionSettings";
		CxSASTClient cxSASTClient = new CxSASTClient(config, null);
		String apiVersion = cxSASTClient.getContentTypeAndApiVersion(config, apiName);
		assertEquals("Expected API version for SAST version 9.4", "application/json;v=1.0", apiVersion);
	}

	public void testGetContentTypeAndApiVersionWithSastVersion9_5() throws MalformedURLException {
		CxScanConfig config = new CxScanConfig();
		CxVersion cxVersion = new CxVersion();
		cxVersion.setVersion("9.5");
		config.setCxVersion(cxVersion);
		String apiName = "projects/{id}/dataRetentionSettings";
		CxSASTClient cxSASTClient = new CxSASTClient(config, null);
		String apiVersion = cxSASTClient.getContentTypeAndApiVersion(config, apiName);
		assertEquals("Expected API version for SAST version 9.5", "application/json;v=1.0", apiVersion);
	}

	public void testGetContentTypeAndApiVersionWithSastVersion94() throws MalformedURLException {
		CxScanConfig config = new CxScanConfig();
		CxVersion cxVersion = new CxVersion();
		cxVersion.setVersion("9.4");
		config.setCxVersion(cxVersion);
		String apiName = "sast/scanWithSettings";
		CxSASTClient cxSASTClient = new CxSASTClient(config, null);
		String apiVersion = cxSASTClient.getContentTypeAndApiVersion(config, apiName);
		assertEquals("Expected API version for SAST version 9.4", "application/json;v=1.0", apiVersion);
	}

	public void testGetContentTypeAndApiVersionWithSastVersion95() throws MalformedURLException {
		CxScanConfig config = new CxScanConfig();
		CxVersion cxVersion = new CxVersion();
		cxVersion.setVersion("9.5");
		config.setCxVersion(cxVersion);
		String apiName = "sast/scanWithSettings";
		CxSASTClient cxSASTClient = new CxSASTClient(config, null);
		String apiVersion = cxSASTClient.getContentTypeAndApiVersion(config, apiName);
		assertEquals("Expected API version for SAST version 9.5", "application/json;v=1.0", apiVersion);
	}

	public void testGetContentTypeAndApiVersionWithSastVersion9_4AndDataRetentionEnabled()
			throws MalformedURLException {
		CxScanConfig config = new CxScanConfig();
		CxVersion cxVersion = new CxVersion();
		cxVersion.setVersion("9.4");
		config.setCxVersion(cxVersion);
		String apiName = "projects/{id}/dataRetentionSettings";
		config.setEnableDataRetention(true);
		CxSASTClient cxSASTClient = new CxSASTClient(config, null);
		String apiVersion = cxSASTClient.getContentTypeAndApiVersion(config, apiName);
		assertEquals("Expected API version for SAST version 9.4 with data retention enabled", "application/json;v=1.1",
				apiVersion);
	}

	public void testGetContentTypeAndApiVersionWithSastVersion9_4AndScanWithSettingsAndCustomFields()
			throws MalformedURLException {
		CxScanConfig config = new CxScanConfig();
		CxVersion cxVersion = new CxVersion();
		cxVersion.setVersion("9.4");
		config.setCxVersion(cxVersion);
		String apiName = "sast/scanWithSettings";
		String customFields = "{\"custom1\":\"value1\"}";
		config.setCustomFields(customFields);
		CxSASTClient cxSASTClient = new CxSASTClient(config, null);
		String apiVersion = cxSASTClient.getContentTypeAndApiVersion(config, apiName);
		assertEquals("Expected API version for SAST version 9.4 with scanWithSettings and custom fields",
				"application/json;v=1.2", apiVersion);
	}

	public void testGetContentTypeAndApiVersionWithSastVersion9_4AndScanWithSettingsAndPostScanActionId()
			throws MalformedURLException {
		CxScanConfig config = new CxScanConfig();
		CxVersion cxVersion = new CxVersion();
		cxVersion.setVersion("9.4");
		config.setCxVersion(cxVersion);
		String apiName = "sast/scanWithSettings";
		config.setPostScanActionId(1);
		CxSASTClient cxSASTClient = new CxSASTClient(config, null);
		String apiVersion = cxSASTClient.getContentTypeAndApiVersion(config, apiName);
		assertEquals("Expected API version for SAST version 9.4 with scanWithSettings and PostScanActionId",
				"application/json;v=1.2", apiVersion);
	}
}