package com.cx.restclient.astglue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;

import com.checkmarx.one.CxOneClient;
import com.checkmarx.one.dto.CxOneConfig;
import com.checkmarx.one.dto.project.ProjectCreateResponse;
import com.checkmarx.one.dto.scan.ScanConfig;
import com.checkmarx.one.dto.scan.sast.SastConfig;
import com.checkmarx.one.sast.CxOneProjectTransformer;
import com.checkmarx.one.sast.FilterTransformer;
import com.checkmarx.one.sast.PresetTransformer;
import com.checkmarx.one.sast.ProjectNameTransformer;
import com.checkmarx.one.sast.ProxyTransformer;
import com.checkmarx.one.sast.ScanConfigTransformer;
import com.checkmarx.one.sast.TeamsTransformer;
import com.checkmarx.one.util.zip.PathFilter;
import com.cx.restclient.configuration.CxScanConfig;
import com.cx.restclient.dto.ProxyConfig;

public class TransformerServiceImpl implements  TransformerService{

	private CxScanConfig cxConfig;
	private Logger log;
	public TransformerServiceImpl(CxScanConfig scanConfig, Logger log) {
		this.cxConfig = scanConfig;
		this.log = log;
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
		cxOneClient.init();
		TeamsTransformer teamTransformer = new TeamsTransformer(cxOneClient);
		String groupName = teamTransformer.getGroupNameFromTeam(cxConfig.getTeamPath());
//		cxOneConfig.getScanConfig().getProject().getGroups().add(teamTransformer.getGroupNameFromTeam(cxConfig.getTeamPath()));
		
		ProjectNameTransformer projectNameTransformer = new ProjectNameTransformer(cxOneClient);
		String transformedProjectName = projectNameTransformer.getProjectName(cxConfig.getProjectName(), cxConfig.getBranchName());
		
		ProxyTransformer proxyTransformer = new ProxyTransformer(cxOneClient);
		ProxyConfig proxyConfig = cxConfig.getProxyConfig();
		if(proxyConfig != null) {
		cxOneConfig.setProxyConfig(proxyTransformer.getProxyConfiguration(cxConfig.isProxy(), proxyConfig.getHost(), proxyConfig.isUseHttps(),
				proxyConfig.getUsername(), proxyConfig.getPassword(), proxyConfig.getNoproxyHosts(), proxyConfig.getPort()));
		}
		
		CxOneProjectTransformer projectCreateTransformer = new CxOneProjectTransformer(cxOneClient, transformedProjectName);
		Map<String, String> tags = new HashMap<>();
		List<String> groups = new ArrayList<String>();
		groups.add(groupName);
		//TODO : Need to check criticality to be set
		//TODO: Creating the project with assumption that the "transformedProjectName" does not exist. Later we need to have the logic to check if project
		//with this name exist don't create a new project rather get the existing project's projectId. This will have similar design as in SAST. But in SAST we had 
		//API to get project by name and teamId (Refer getProjectByName() in LegacyClient.java). But with present implementation , when jenkins pipeline has different
		//project name which is not present in AST, creating a new project and projectId is giving us the correct value.
		String projectId = projectNameTransformer.getProjectIdForProjectName(transformedProjectName) ;
		String projectName = transformedProjectName;
		if(projectId == null || projectId == "") {
		ProjectCreateResponse project = projectCreateTransformer.getProjectObject(groups, cxConfig.getSourceDir(), 
				1, cxConfig.getBranchName(), cxConfig.getCxOrigin(), tags);
		projectId = project.getId();
		projectName = project.getName();
		log.info("Created a project with ID {}", projectId);
		cxOneConfig.setIsNewProject(true);
		} 
		
		FilterTransformer filterTransformer = new FilterTransformer(cxOneClient);
		PathFilter pathfilter = filterTransformer.getFilterFromSastExclusion(cxConfig.getSastFolderExclusions(), cxConfig.getSastFilterPattern());
		
		ScanConfigTransformer scanConfigTransformer = new ScanConfigTransformer(cxOneClient);
		ScanConfig scanConfig = scanConfigTransformer.constructScanConfig(projectId, projectName, groups,
				/*new PathFilter("source", "*.java")*/pathfilter, tags, cxConfig.getSourceDir());
		cxOneConfig.setScanConfig(scanConfig);
//		String projectId = projectNameTransformer.getProjectIdForProjectName(projectName);
		cxOneConfig.getScanConfig().getProject().setId(projectId);
		PresetTransformer presetTransformer = new PresetTransformer(cxOneClient);
		String astPreset = presetTransformer.getPresetNameById(cxConfig.getPresetId());
		((SastConfig)(cxOneConfig.getScanConfig().getScanners().get(0))).setPresetName(astPreset);
//		cxOneConfig = presetTransformer.getPresetName(cxConfig.getPresetName(), cxOneConfig);
		
		return cxOneConfig;
	}
}
