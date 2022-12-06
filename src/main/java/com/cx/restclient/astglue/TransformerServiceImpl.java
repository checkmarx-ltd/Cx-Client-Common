package com.cx.restclient.astglue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;

import com.checkmarx.one.CxOneClient;
import com.checkmarx.one.dto.CxOneConfig;
import com.checkmarx.one.dto.configuration.ProjectConfiguration;
import com.checkmarx.one.dto.configuration.ProjectConfigurationResponse;
import com.checkmarx.one.dto.configuration.ProjectConfigurationResults;
import com.checkmarx.one.dto.project.ProjectCreateResponse;
import com.checkmarx.one.dto.scan.ScanConfig;
import com.checkmarx.one.sast.CxOneProjectTransformer;
import com.checkmarx.one.sast.EngineConfigurationMap;
import com.checkmarx.one.sast.EngineConfigurationTransformer;
import com.checkmarx.one.sast.FilterTransformer;
import com.checkmarx.one.sast.PresetMap;
import com.checkmarx.one.sast.PresetTransformer;
import com.checkmarx.one.sast.ProjectNameTransformer;
import com.checkmarx.one.sast.ProxyTransformer;
import com.checkmarx.one.sast.ScanConfigTransformer;
import com.checkmarx.one.sast.TeamsTransformer;
import com.checkmarx.one.util.zip.PathFilter;
import com.cx.restclient.configuration.CxScanConfig;
import com.cx.restclient.dto.ProxyConfig;
import com.fasterxml.jackson.databind.ObjectMapper;

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
		
		TransformerServiceImpl transformerServiceImpl = new TransformerServiceImpl(cxConfig, log);
		//TransformerServiceImpl transformerServiceImpl = new TransformerServiceImpl(cxConfig, LoggerFactory.getLogger(TransformerServiceImpl.class));
		ProjectConfigurationResponse projectConfigurationResponse = cxOneClient.getProjectConfiguration(projectId);
		List<ProjectConfiguration> projectConfigurationList = transformerServiceImpl
				.getProjectConfigurationList(projectConfigurationResponse);
		List<ProjectConfiguration> updatedProjectConfigurationList = new ArrayList<>();
		if (projectConfigurationList != null) {
			ProjectConfiguration languageModeConfiguration = transformerServiceImpl
					.getLanguageModeConfiguration(projectConfigurationList);
			if (languageModeConfiguration != null && languageModeConfiguration.getAllowOverride()) {
				Map<Integer, String> engineConfigurationsMap = EngineConfigurationMap.getEngineConfigurationMap();
				EngineConfigurationTransformer engineConfigurationTransformer = new EngineConfigurationTransformer(
						cxOneClient);
				ProjectConfiguration updatedLanguageModeConfiguration = engineConfigurationTransformer
						.getEngineConfigurationTransformer(languageModeConfiguration, engineConfigurationsMap,
								cxConfig.getEngineConfigurationId());
				updatedProjectConfigurationList.add(updatedLanguageModeConfiguration);
				cxConfig.setEngineConfigurationName(updatedLanguageModeConfiguration.getValue());

			}
			ProjectConfiguration presetConfiguration = transformerServiceImpl
					.getPresetConfiguration(projectConfigurationList);
			if (presetConfiguration != null && presetConfiguration.getAllowOverride()) {
				Map<Integer, String> sastPresetMap = PresetMap.getSastPresetMap();
				PresetTransformer presetTransformer = new PresetTransformer(cxOneClient);
				ProjectConfiguration updatedPresetConfiguration = presetTransformer
						.getPresetTransformer(presetConfiguration, sastPresetMap, cxConfig.getPresetId());
				updatedProjectConfigurationList.add(updatedPresetConfiguration);
				cxConfig.setPresetName(updatedPresetConfiguration.getValue());
			}

			ProjectConfiguration incrementalConfiguration = transformerServiceImpl.getIncrementalConfiguration(projectConfigurationList);
			if(incrementalConfiguration != null && incrementalConfiguration.getAllowOverride()) {
				ProjectConfiguration updatedIncrementalConfiguration = incrementalConfiguration;
				updatedIncrementalConfiguration.setValue(String.valueOf(cxConfig.getIncremental()));
				updatedProjectConfigurationList.add(updatedIncrementalConfiguration);
			}
			
			ProjectConfiguration filterConfiguration = transformerServiceImpl
					.getFilterConfiguration(projectConfigurationList);
			if (filterConfiguration != null || filterConfiguration.getAllowOverride()) {
				ProjectConfiguration updatedFilterConfiguration = filterConfiguration;
				updatedFilterConfiguration.setValue(transformerServiceImpl.getFilterConfigurationValue(
						cxConfig.getSastFolderExclusions(), cxConfig.getSastFilterPattern()));
				updatedProjectConfigurationList.add(updatedFilterConfiguration);
			}

			if (updatedProjectConfigurationList != null && !updatedProjectConfigurationList.isEmpty()) {
				try {
					cxOneClient.patchProjectConfiguration(updatedProjectConfigurationList, projectId);
				} catch (Exception e) {
					log.error("Exception occured while patching Project Configuration list ", e);
				}
			}
		}
		ScanConfigTransformer scanConfigTransformer = new ScanConfigTransformer(cxOneClient);
		ScanConfig scanConfig = scanConfigTransformer.constructScanConfig(projectId, projectName, groups,
				pathfilter, tags, cxConfig.getSourceDir(), cxConfig.getIncremental(), cxConfig.getPresetName());
		cxOneConfig.setScanConfig(scanConfig);
//		String projectId = projectNameTransformer.getProjectIdForProjectName(projectName);
		cxOneConfig.getScanConfig().getProject().setId(projectId);
		return cxOneConfig;
		}

		private List<ProjectConfiguration> getProjectConfigurationList(
				ProjectConfigurationResponse projectConfigurationResponse) {
			try {
				if (projectConfigurationResponse != null) {
					ObjectMapper mapper = new ObjectMapper();
					ProjectConfigurationResults results = mapper.readValue(projectConfigurationResponse.toString(),
							ProjectConfigurationResults.class);
					return results.getResults().get(0);
				}
			} catch (Exception e) {
				log.error("Error occurred while parsing Project Configuration List ", e);
			}
			return null;
		}

		private ProjectConfiguration getLanguageModeConfiguration(List<ProjectConfiguration> projectConfigurationList) {

			ProjectConfiguration projectConfiguration = null;
			for (ProjectConfiguration configuration : projectConfigurationList) {
				if (configuration.getName().equalsIgnoreCase("languageMode") && configuration.getCategory().equalsIgnoreCase("sast"))
					return configuration;
			}
			return projectConfiguration;
		}

		private ProjectConfiguration getPresetConfiguration(List<ProjectConfiguration> projectConfigurationList) {
			ProjectConfiguration projectConfiguration = null;
			for (ProjectConfiguration configuration : projectConfigurationList) {
				if (configuration.getName().equalsIgnoreCase("presetName") && configuration.getCategory().equalsIgnoreCase("sast"))
					return configuration;
			}
			return projectConfiguration;
		}
		private ProjectConfiguration getIncrementalConfiguration(List<ProjectConfiguration> projectConfigurationList) {
			ProjectConfiguration projectConfiguration = null;
			for (ProjectConfiguration configuration : projectConfigurationList) {
				if (configuration.getName().equalsIgnoreCase("incremental") && configuration.getCategory().equalsIgnoreCase("sast"))
					return configuration;
			}
			return projectConfiguration;
		}
		private ProjectConfiguration getFilterConfiguration(List<ProjectConfiguration> projectConfigurationList) {
			ProjectConfiguration projectConfiguration = null;
			for (ProjectConfiguration configuration : projectConfigurationList) {
				if (configuration.getName().equalsIgnoreCase("filter") && configuration.getCategory().equalsIgnoreCase("sast"))
					return configuration;
			}
			return projectConfiguration;
		}

		private String getFilterConfigurationValue(String sastFolderExclusions, String sastFilterPattern) {
			String filter = "";
			String excludeFoldersPattern = Arrays.stream(sastFolderExclusions.split(",")).map(String::trim)
					.collect(Collectors.joining(","));
			String excludeFilesPattern = Arrays.stream(sastFilterPattern.split(",")).map(String::trim)
					.map(file -> file.replace("!**/", "")).collect(Collectors.joining(","));
			if (excludeFoldersPattern != null && !excludeFoldersPattern.isEmpty())
				filter = excludeFoldersPattern;
			if (filter != null && !filter.isEmpty())
				filter += "," + excludeFilesPattern;
			else if (excludeFilesPattern != null && !excludeFilesPattern.isEmpty())
				filter = excludeFilesPattern;
			return filter;
		}
	}
