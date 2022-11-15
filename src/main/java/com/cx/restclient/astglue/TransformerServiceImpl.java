package com.cx.restclient.astglue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.checkmarx.one.CxOneClient;
import com.checkmarx.one.CxOneProjectTransformer;
import com.checkmarx.one.PresetTransformer;
import com.checkmarx.one.ProjectNameTransformer;
import com.checkmarx.one.ProxyTransformer;
import com.checkmarx.one.ScanConfigTransformer;
import com.checkmarx.one.TeamsTransformer;
import com.checkmarx.one.dto.CxOneConfig;
import com.checkmarx.one.dto.project.ProjectCreateResponse;
import com.checkmarx.one.dto.scan.ScanConfig;
import com.checkmarx.one.util.zip.PathFilter;
import com.cx.restclient.configuration.CxScanConfig;
import com.cx.restclient.dto.ProxyConfig;

public class TransformerServiceImpl implements  TransformerService{

	private CxScanConfig cxConfig;
	public TransformerServiceImpl(CxScanConfig scanConfig) {
		this.cxConfig = scanConfig;
	}

	@Override
	public CxOneConfig getCxOneConfig() {
		
		CxOneConfig cxOneConfig = new CxOneConfig();
		cxOneConfig.setAccessControlBaseUrl(cxConfig.getAccessControlBaseUrl());
		cxOneConfig.setApiBaseUrl(cxConfig.getApiBaseUrl());
		cxOneConfig.setClientId(cxConfig.getClientId());
		cxOneConfig.setClientSecret(cxConfig.getClientSecret());
		cxOneConfig.setTenant(cxConfig.getTenant());
		
		CxOneClient cxOneClient = new CxOneClient(cxOneConfig);
		
		TeamsTransformer teamTransformer = new TeamsTransformer(cxOneClient);
//		cxOneConfig.getScanConfig().getProject().getGroups().add(teamTransformer.getGroupNameFromTeam(cxConfig.getTeamPath()));
		
		ProjectNameTransformer projectTransformer = new ProjectNameTransformer(cxOneClient);
		cxOneConfig.getScanConfig().getProject().setName(projectTransformer.getProjectName(cxConfig.getProjectName()));
		String transformedProjectName = cxOneConfig.getScanConfig().getProject().getName();
		
		ProxyTransformer proxyTransformer = new ProxyTransformer(cxOneClient);
		ProxyConfig proxyConfig = cxConfig.getProxyConfig();
		cxOneConfig.setProxyConfig(proxyTransformer.getProxyConfiguration(cxConfig.isProxy(), proxyConfig.getHost(), proxyConfig.isUseHttps(),
				proxyConfig.getUsername(), proxyConfig.getPassword(), proxyConfig.getNoproxyHosts(), proxyConfig.getPort()));
		
		PresetTransformer presetTransformer = new PresetTransformer(cxOneClient);
		presetTransformer.getPresetId(cxConfig.getPresetId());
		cxOneConfig = presetTransformer.getPresetName(cxConfig.getPresetName(), cxOneConfig);
		
		CxOneProjectTransformer projectCreateTransformer = new CxOneProjectTransformer(cxOneClient, transformedProjectName);
		Map<String, String> tags = new HashMap<>();
		List<String> groups = new ArrayList<String>();
		groups.add(teamTransformer.getGroupNameFromTeam(cxConfig.getTeamPath()));
		ProjectCreateResponse project = projectCreateTransformer.getProjectObject(groups, cxConfig.getSourceDir(), 
				1, "master", "Jenkins", tags);
		String projectId = project.getId();
		String projectName = project.getName();
		
		PathFilter pathFilter = new PathFilter(".git,target,.idea,.settings", "");
		//TODO : instead of the above string pass the include/exclude pattern to PathFilter
				
//		ScanConfigTransformer scanConfigTransformer = new ScanConfigTransformer(cxOneClient);
//		ScanConfig scanConfig = scanConfigTransformer.constructScanConfig(projectId, projectName, groups, pathFilter, tags);
//		cxOneConfig.setScanConfig(scanConfig);
		
		return cxOneConfig;
	}
}
