package com.cx.restclient;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;

import com.checkmarx.one.CxOneClient;
import com.checkmarx.one.dto.CxOneConfig;
import com.checkmarx.one.dto.scan.ResultsResponse;
import com.checkmarx.one.sast.TeamsTransformer;
import com.cx.restclient.ast.dto.sast.AstSastResults;
import com.cx.restclient.astglue.CxConfigParamsTransformerServiceFactory;
import com.cx.restclient.astglue.TransformerService;
import com.cx.restclient.common.Scanner;
import com.cx.restclient.configuration.CxScanConfig;
import com.cx.restclient.dto.Results;
import com.cx.restclient.dto.ScannerType;
import com.cx.restclient.exception.CxClientException;
import com.cx.restclient.sast.dto.SASTResults;
import com.cx.restclient.sast.utils.State;

public class CxOneWrapperClient implements Scanner{
    private CxScanConfig config;
    private CxOneConfig oneConfig ;
    private CxOneClient cxOneClient;
    private Logger log;
    private List<String> groups;
    private State state = State.SUCCESS;
    private long scanId;
    private AstSastResults astSastResults = new AstSastResults();
    
	CxOneWrapperClient(CxScanConfig config, Logger log) throws MalformedURLException {
		this.config = config;
        this.log = log;
		CxConfigParamsTransformerServiceFactory factory = new CxConfigParamsTransformerServiceFactory();
		TransformerService service = factory.create(ScannerType.SAST, ScannerType.CXONE_SAST, config);
		oneConfig = service.getCxOneConfig();
        oneConfig.setAccessControlBaseUrl(config.getAccessControlBaseUrl());
        oneConfig.setApiBaseUrl(config.getApiBaseUrl());
        oneConfig.setClientId(config.getClientId());
        oneConfig.setClientSecret(config.getClientSecret());
        oneConfig.setTenant(config.getTenant());
        cxOneClient = new CxOneClient(oneConfig);
	}

	@Override
	public Results init() {
		AstSastResults initAstSastResults = new AstSastResults();
        try {
            initiate();
        } catch (CxClientException e) {
            setState(State.FAILED);
            initAstSastResults.setException(e);
        	log.error("error while initializing AST scanner");
        	log.error(e.getMessage());
        }
        return initAstSastResults;
	}

	private void initiate() {
		try {
            if (config.isSubmitToAST()) {
            	// This will invoke init() of CxHttpClient and then generate access token within this call.Login happens in this method
    			cxOneClient.init();
    			String accessToken = cxOneClient.getAccessToken();
    			// TODO : Remove this AT
    			log.info("Login successful to AST" + accessToken);
//                resolveTeam();
            }
        } catch (Exception e) {
            throw new CxClientException(e);
        }		
	}
	@Override
	public Results initiateScan() {
		astSastResults = new AstSastResults();
		ResultsResponse results = cxOneClient.syncScan(oneConfig.getScanConfig());
		
        return results;
	}

	@Override
	public Results waitForScanResults() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Results getLatestScanResults() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}
	 public State getState() {
	        return state;
	    }

	    public void setState(State state) {
	        this.state = state;
	    }

}
