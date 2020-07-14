package com.cx.restclient.general;

import com.cx.restclient.CxClientDelegator;
import com.cx.restclient.ast.dto.sca.AstScaConfig;
import com.cx.restclient.configuration.CxScanConfig;
import com.cx.restclient.dto.ProxyConfig;
import com.cx.restclient.dto.ScanResults;
import com.cx.restclient.dto.ScannerType;
import com.cx.restclient.exception.CxClientException;
import com.cx.utility.TestingUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.BeforeClass;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Properties;

@Slf4j
public abstract class CommonClientTest {
    private static final String MAIN_PROPERTIES_FILE = "config.properties";
    public static final String OVERRIDE_FILE = "config-secrets.properties";

    static Properties props;

    @BeforeClass
    public static void initTest() throws IOException {
        props = TestingUtils.getProps(MAIN_PROPERTIES_FILE, CommonClientTest.class);
        loadOverrides(props);
    }

    protected static String prop(String key) {
        return props.getProperty(key);
    }

    private static void loadOverrides(Properties targetProps) {
        try {
            Properties overridingProps = TestingUtils.getProps(OVERRIDE_FILE, CommonClientTest.class);
            targetProps.putAll(overridingProps);
        } catch (IOException e) {
            log.warn("Failed to load property overrides.");
        }
    }

    protected static void setProxy(CxScanConfig config) {
        ProxyConfig proxyConfig = new ProxyConfig();
        proxyConfig.setHost(props.getProperty("proxy.host"));
        proxyConfig.setPort(Integer.parseInt(props.getProperty("proxy.port")));
        config.setProxyConfig(proxyConfig);
    }

    void failOnException(Exception e) {
        log.error("Unexpected exception during test.", e);
        Assert.fail(e.getMessage());
    }

    protected CxScanConfig initSastConfig(){
        return initSastConfig(new  CxScanConfig(), "sastOnlyScan");
    }

    protected CxScanConfig initSastConfig(String projectName){
        return initSastConfig(new  CxScanConfig(), projectName);
    }
    
    protected CxScanConfig initSastConfig( CxScanConfig config, String projectName) {

        config.setReportsDir(new File("C:\\report"));
        config.setSourceDir(props.getProperty("sastSource"));
        config.setUsername(props.getProperty("username"));
        config.setPassword(props.getProperty("password"));
        config.setUrl(props.getProperty("serverUrl"));
        config.setCxOrigin("common");
        config.setProjectName(projectName);
        config.setPresetName("Default");
        config.setTeamPath("\\CxServer");
        config.setSynchronous(true);
        config.setGeneratePDFReport(true);
        config.addScannerType(ScannerType.SAST);
        config.setPresetName("Default");
//        config.setPresetId(7);

        return config;
    }

    protected static CxScanConfig initScaConfig(boolean useOnPremAuthentication){
        return initScaConfig(useOnPremAuthentication, new CxScanConfig());
    }
    
    protected static CxScanConfig initScaConfig(boolean useOnPremAuthentication,  CxScanConfig config) {
        config.addScannerType(ScannerType.AST_SCA);
        config.setSastEnabled(false);
        config.setProjectName(props.getProperty("astSca.projectName"));

        AstScaConfig sca = TestingUtils.getScaConfig(props, useOnPremAuthentication);
        config.setAstScaConfig(sca);

        return config;
    }

    protected ScanResults runScan(CxScanConfig config) throws MalformedURLException, CxClientException {
        CxClientDelegator client = new CxClientDelegator(config, log);
        try {
            client.init();
            System.out.println("Initiate scan for the following scanners: " + config.getScannerTypes());
            client.initiateScan();
            System.out.println("Waiting for results of " + config.getScannerTypes());
            ScanResults results =  client.waitForScanResults();
            Assert.assertNotNull(results);
            System.out.println("Results retrieved" );
            return results;
        } catch (Exception e) {
            failOnException(e);
            throw new CxClientException(e);
        }
    }

    protected CxScanConfig initOsaConfig(){
        return initOsaConfig(new CxScanConfig(), "osaOnlyScan");
    }
    
    protected CxScanConfig initOsaConfig(CxScanConfig config, String projectName) {

        System.out.println("Scan ProjectName " + projectName);
        config.addScannerType(ScannerType.OSA);
        config.setSourceDir(props.getProperty("dependencyScanSourceDir"));
        config.setReportsDir(new File("C:\\report"));
        config.setUrl(props.getProperty("serverUrl"));
        config.setUsername(props.getProperty("username"));
        config.setPassword(props.getProperty("password"));

        config.setCxOrigin("common");
        config.setProjectName(projectName);
        config.setPresetName("Default");
        config.setTeamPath("\\CxServer");
        config.setSynchronous(true);
        config.setGeneratePDFReport(true);

        config.setOsaRunInstall(true);
        config.setOsaThresholdsEnabled(true);
        config.setPublic(true);

        return config;
    }


}
