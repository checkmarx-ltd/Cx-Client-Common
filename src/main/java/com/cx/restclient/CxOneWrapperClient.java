package com.cx.restclient;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;

import com.checkmarx.one.CxOneClient;
import com.checkmarx.one.dto.CxOneConfig;
import com.checkmarx.one.sast.TeamsTransformer;
import com.cx.restclient.ast.dto.sast.AstSastResults;
import com.cx.restclient.astglue.CxConfigParamsTransformerServiceFactory;
import com.cx.restclient.astglue.TransformerService;
import com.cx.restclient.common.Scanner;
import com.cx.restclient.configuration.CxScanConfig;
import com.cx.restclient.dto.Results;
import com.cx.restclient.dto.ScannerType;
import com.cx.restclient.exception.CxClientException;

public class CxOneWrapperClient implements Scanner{
    private CxScanConfig config;
    private CxOneConfig oneConfig ;
    private CxOneClient cxOneClient;
    private Logger log;
    private List<String> groups;
//    TeamsTransformer teamTransformer;
    
	CxOneWrapperClient(CxScanConfig config, Logger log) throws MalformedURLException {
		this.config = config;
//        this.log = log;
		
		CxConfigParamsTransformerServiceFactory factory = new CxConfigParamsTransformerServiceFactory();
		TransformerService service = factory.create(ScannerType.SAST, ScannerType.CXONE_SAST, config);
		oneConfig = service.getCxOneConfig();
        oneConfig.setAccessControlBaseUrl(config.getAccessControlBaseUrl());
        oneConfig.setApiBaseUrl(config.getApiBaseUrl());
        oneConfig.setClientId(config.getClientId());
        oneConfig.setClientSecret(config.getClientSecret());
        oneConfig.setTenant(config.getTenant());
        cxOneClient = new CxOneClient(oneConfig);
//        teamTransformer = new TeamsTransformer(cxOneClient);
	}

	@Override
	public Results init() {
		AstSastResults initAstSastResults = new AstSastResults();
		CxOneConfig oneConfig = new CxOneConfig();
		
        try {
            initiate();
        } catch (CxClientException e) {
//            setState(State.FAILED);
//            initAstSastResults.setException(e);
        }
        return initAstSastResults;
	}

	private void initiate() {
		try {
			
            if (config.isSubmitToAST()) {
            	// This will invoke init() of CxHttpClient and then generate access token within this call.Login happens in this method
    			cxOneClient.init();
    			String accessToken = cxOneClient.getAccessToken();
    			System.out.println(" AUTHENTICATED ... " + accessToken);
                resolveTeam();
                //httpClient.setTeamPathHeader(this.teamPath);
//                if (config.isSastEnabled()) {
//                    resolvePreset();
//                }
//                if (config.getEnablePolicyViolations()) {
//                    resolveCxARMUrl();
//                }
//                resolveEngineConfiguration();
//                resolveProjectId();
            }
        } catch (Exception e) {
            throw new CxClientException(e);
        }		
	}

	 private void resolveTeam() throws CxClientException, IOException {
//		 oneConfig.getScanConfig().getProject().setGroups(configureGroup());
	        printGroup();
	    }

	 private void printGroup() {
		 try {
	            for (Iterator iterator = groups.iterator(); iterator.hasNext();) {
					String group = (String) iterator.next();
					log.info(String.format("full team path: %s", group));
				}	        } catch (Exception e) {
	            log.warn("Error getting team path.");
	        }		
	}

	/*public List<String> configureGroup() throws IOException, CxClientException {
		String groupName = "";
			if (oneConfig.getScanConfig().getProject().getGroups().size() == 0) {
				List<String> groupList = populateGroupsList();
				// If there is no chosen teamPath, just add first one from the teams
				// list as default
				if (groupList != null && !groupList.isEmpty()) {
					groupName = groupList.get(0).toString();
				}
			} else {
//				groupName = teamTransformer.getGroupNameFromTeam(config.getTeamPath());
			}
			groups.add(groupName);
	        return groups;
	    }*/
	 
	 private List<String> populateGroupsList() throws IOException {
		 TeamsTransformer teamTransformer = new TeamsTransformer(cxOneClient);
	        return teamTransformer.getGroups();
	    }
	@Override
	public Results initiateScan() {
		cxOneClient.syncScan(oneConfig.getScanConfig());
		return null;
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

}
