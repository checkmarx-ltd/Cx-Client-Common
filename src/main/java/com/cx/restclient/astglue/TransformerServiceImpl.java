package com.cx.restclient.astglue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import com.checkmarx.one.CxOneClient;
import com.checkmarx.one.dto.CxOneConfig;
import com.checkmarx.one.dto.configuration.ProjectConfiguration;
import com.checkmarx.one.dto.configuration.ProjectConfigurationResponse;
import com.checkmarx.one.dto.configuration.ProjectConfigurationResults;
import com.checkmarx.one.dto.project.ProjectCreateResponse;
import com.checkmarx.one.dto.scan.ScanConfig;
import com.checkmarx.one.dto.scan.ScanQueueResponse;
import com.checkmarx.one.sast.CxOneProjectTransformer;
import com.checkmarx.one.sast.EngineConfigurationTransformer;
import com.checkmarx.one.sast.PresetTransformer;
import com.checkmarx.one.sast.ProjectNameTransformer;
import com.checkmarx.one.sast.ProxyTransformer;
import com.checkmarx.one.sast.ScanConfigTransformer;
import com.checkmarx.one.sast.TeamsTransformer;
import com.checkmarx.one.util.zip.PathFilter;
import com.checkmarx.one.util.zip.ZipUtil;
import com.cx.restclient.common.CxOneConstants;
import com.cx.restclient.configuration.CxScanConfig;
import com.cx.restclient.dto.ProxyConfig;
import com.cx.restclient.exception.CxClientException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TransformerServiceImpl implements TransformerService {

	private CxScanConfig cxConfig;
	private Logger log;
	public TransformerServiceImpl(CxScanConfig scanConfig, Logger log) {
		this.cxConfig = scanConfig;
		this.log = log;
	}
	
	/**
	 * This method is used to get cxOneConfig  
	 * @return cxOneConfig
	 */
	

	@Override
	public CxOneConfig getCxOneConfig() {

		CxOneConfig cxOneConfig = new CxOneConfig();
		cxOneConfig.setAccessControlBaseUrl(cxConfig.getAccessControlBaseUrl());
		cxOneConfig.setApiBaseUrl(cxConfig.getApiBaseUrl());
		cxOneConfig.setClientId(cxConfig.getClientId());
		cxOneConfig.setClientSecret(cxConfig.getClientSecret());
		cxOneConfig.setTenant(cxConfig.getTenant());
		/**
		 * This initialization is partial. The actual initialization is done in
		 * CxOneWrapperClient.java
		 */
		CxOneClient cxOneClient = new CxOneClient(cxOneConfig);
		cxOneClient.init();
		// Teams
		List<String> groups = getGroupListFromTransformer(cxOneClient, cxConfig.getTeamPath());

		// Proxy
		cxOneConfig.setProxyConfig(getProxyConfigFromTransformer(cxOneClient, cxConfig.getProxyConfig()));

		// Scan Level custom fields
		Map<String, String> tags = new HashMap<>();
		if (!StringUtils.isEmpty(cxConfig.getCustomFields()))
			tags = setCustomFieldTags(tags, cxConfig.getCustomFields());
		//Project Name
		String projectName = getProjectNameFromTransformer(cxOneClient, cxConfig.getProjectName());
		String projectId = getProjectIdFromProjectName(cxOneClient, projectName);

		if (!cxConfig.getDenyProject() && StringUtils.isEmpty(projectId)) {
			ProjectCreateResponse project = createProjectFromTransformer(cxOneClient, groups, tags);
			if (project != null) {
				projectId = project.getId();
				projectName = project.getName();
				log.info("Created a project with ID {}", projectId);
				cxOneConfig.setIsNewProject(true);
			}
		} else if (cxConfig.getDenyProject() && StringUtils.isEmpty(projectId)) {
			throw new CxClientException(CxOneConstants.DENY_NEW_PROJECT_ERROR.replace("{projectName}", cxConfig.getProjectName()));
		} else if (cxConfig.getAvoidDuplicateProjectScans()) {
			ScanQueueResponse scanQueueResponse = cxOneClient.getQueueScans(projectId, CxOneConstants.RUNNING_QUEUED);
			if (scanQueueResponse != null && scanQueueResponse.getTotalCount() > 0) {
				throw new CxClientException(CxOneConstants.MSG_AVOID_DUPLICATE_PROJECT_SCANS);
			}
		}

		ProjectConfigurationResponse projectConfigurationResponse = cxOneClient.getProjectConfiguration(projectId);
		List<ProjectConfiguration> projectConfigurationList = getProjectConfigurationList(projectConfigurationResponse);
		List<ProjectConfiguration> updatedProjectConfigurationList = new ArrayList<>();
		if (projectConfigurationList != null) {
			ProjectConfiguration languageModeConfiguration = getLanguageModeConfiguration(projectConfigurationList);
			if (languageModeConfiguration != null && languageModeConfiguration.getAllowOverride()) {
				ProjectConfiguration updatedLanguageModeConfiguration = languageModeConfiguration;
				String languageMode = getLanguageModeFromTransformer(cxOneClient);
				updatedLanguageModeConfiguration.setValue(languageMode);
				updatedProjectConfigurationList.add(updatedLanguageModeConfiguration);
				cxConfig.setEngineConfigurationName(updatedLanguageModeConfiguration.getValue());
			}

			ProjectConfiguration presetConfiguration = getPresetConfiguration(projectConfigurationList);
			if (presetConfiguration != null && presetConfiguration.getAllowOverride()) {
				ProjectConfiguration updatedPresetConfiguration = presetConfiguration;
				String presetName = getPresetNameFromTransformer(cxOneClient);
				updatedPresetConfiguration.setValue(presetName);
				updatedProjectConfigurationList.add(updatedPresetConfiguration);
				cxConfig.setPresetName(updatedPresetConfiguration.getValue());
			}

			ProjectConfiguration incrementalConfiguration = getIncrementalConfiguration(projectConfigurationList);
			if (incrementalConfiguration != null && incrementalConfiguration.getAllowOverride()) {
				ProjectConfiguration updatedIncrementalConfiguration = incrementalConfiguration;
				updatedIncrementalConfiguration.setValue(String.valueOf(cxConfig.getIncremental()));
				updatedProjectConfigurationList.add(updatedIncrementalConfiguration);
			}

			ProjectConfiguration filterConfiguration = getFilterConfiguration(projectConfigurationList);
			if (filterConfiguration != null || filterConfiguration.getAllowOverride()) {
				ProjectConfiguration updatedFilterConfiguration = filterConfiguration;
				updatedFilterConfiguration.setValue(getFilterConfigurationValue(cxConfig.getSastFolderExclusions(),
						cxConfig.getSastFilterPattern()));
				updatedProjectConfigurationList.add(updatedFilterConfiguration);
			}

			if (updatedProjectConfigurationList != null && !updatedProjectConfigurationList.isEmpty()) {
				try {
					cxOneClient.patchProjectConfiguration(updatedProjectConfigurationList, projectId);
				} catch (Exception e) {
					log.error("Exception occurred while patching Project Configuration list ", e);
				}
			}
		}
		cxOneConfig.setCxOneSastScanTimeoutSec(cxConfig.getSastScanTimeoutInMinutes());
		try {
			cxOneConfig.setScanConfig(getScanConfigFromTransformer(cxOneClient, projectId, projectName, groups, tags));
		} catch (CxClientException e) {
			throw new CxClientException(e.getMessage());
		}
		return cxOneConfig;
	}

	// Helper Methods
	
	/**
	 * This method is used to call TeamsTransformer to get groupName 
	 * @param cxOneClient
	 * @param teamPath
	 * @return groups
	 */
	
	private List<String> getGroupListFromTransformer(CxOneClient cxOneClient, String teamPath) {
		TeamsTransformer teamTransformer = new TeamsTransformer(cxOneClient);
		String groupName = teamTransformer.getGroupNameFromTeam(cxConfig.getTeamPath());
		List<String> groups = new ArrayList<String>();
		groups.add(groupName);
		return groups;
	}

	/**
	 * This method is used to call ProxyTransformer to get proxy details 
	 * @param cxOneClient
	 * @param proxyConfig
	 * @return ProxyConfig
	 */
	
	private com.checkmarx.one.dto.ProxyConfig getProxyConfigFromTransformer(CxOneClient cxOneClient,
			ProxyConfig proxyConfig) {
		ProxyTransformer proxyTransformer = new ProxyTransformer(cxOneClient);
		if (proxyConfig != null)
			return proxyTransformer.getProxyConfiguration(cxConfig.isProxy(), proxyConfig.getHost(),
					proxyConfig.isUseHttps(), proxyConfig.getUsername(), proxyConfig.getPassword(),
					proxyConfig.getNoproxyHosts(), proxyConfig.getPort());
		return null;
	}

	/**
	 * This method is used to set scanCustomFields to scan tags 
	 * @param tags
	 * @param customFields
	 * @return tags
	 */
	 
	private Map<String, String> setCustomFieldTags(Map<String, String> tags, String customFields) {
		String customFieldValue = customFields.substring(1, customFields.length() - 1);
		String[] keyValuePairs = customFieldValue.split(",");
		for (String pair : keyValuePairs) {
			String[] entry = pair.split(":");
			String key = entry[0].trim();
			key = key.substring(1, key.length() - 1).trim();
			String value = entry[1].trim();
			value = value.substring(1, value.length() - 1).trim();
			tags.put(key, value);
		}
		return tags;
	}

	/**
	 * This method is used to get the cxOneProjectName, if exists
	 * @param cxOneClient
	 * @param sastProjectName
	 * @return projectName
	 */
	private String getProjectNameFromTransformer(CxOneClient cxOneClient, String sastProjectName) {
		ProjectNameTransformer projectNameTransformer = new ProjectNameTransformer(cxOneClient);
		return projectNameTransformer.getProjectName(sastProjectName);
	}

	/**
	 * This method is used to get the projectId using cxOne Project Name, if exists 
	 * @param cxOneClient
	 * @param projectName
	 * @return projectId
	 */
	
	private String getProjectIdFromProjectName(CxOneClient cxOneClient, String projectName) {
		ProjectNameTransformer projectNameTransformer = new ProjectNameTransformer(cxOneClient);
		return projectNameTransformer.getProjectIdForProjectName(projectName);
	}

	/**
	 * This method is used to create project using CxOneProjectTransformer
	 * @param cxOneClient
	 * @param groups
	 * @param tags
	 * @return projectObject
	 */
	 
	private ProjectCreateResponse createProjectFromTransformer(CxOneClient cxOneClient, List<String> groups,
			Map<String, String> tags) {
		/*
		 * if(projectName == null) { use look up which will return project Id if look up
		 * also does not give you the project, assume that a new project to be created.
		 * }
		 */

		CxOneProjectTransformer projectCreateTransformer = new CxOneProjectTransformer(cxOneClient,
				cxConfig.getProjectName());
		return projectCreateTransformer.getProjectObject(groups, cxConfig.getSourceDir(), 1, cxConfig.getBranchName(),
				cxConfig.getCxOrigin(), tags);
	}

	/**
	 * This method is used to get languageMode from EngineConfigurationTransformer
	 * @param cxOneClient
	 * @return languageMode
	 */
	
	private String getLanguageModeFromTransformer(CxOneClient cxOneClient) {
		EngineConfigurationTransformer engineConfigurationTransformer = new EngineConfigurationTransformer(cxOneClient);
		return engineConfigurationTransformer.getEngineConfigurationTransformer(cxConfig.getEngineConfigurationId());
	}

	/**
	 * This method is used to get presetName from PresetTransformer
	 * @param cxOneClient
	 * @return presetName
	 */
	
	private String getPresetNameFromTransformer(CxOneClient cxOneClient) {
		PresetTransformer presetTransformer = new PresetTransformer(cxOneClient);
		return presetTransformer.getPresetTransformer(cxConfig.getPresetId());
	}

	/**
	 * This method is used to get scan configurations from ScanConfigTransformer
	 * @param cxOneClient
	 * @param projectId
	 * @param projectName
	 * @param groups
	 * @param tags
	 * @return scanConfig
	 */
	
	private ScanConfig getScanConfigFromTransformer(CxOneClient cxOneClient, String projectId, String projectName,
			List<String> groups, Map<String, String> tags) {
		PathFilter astFilter = new PathFilter(cxConfig.getSastFolderExclusions(), cxConfig.getSastFilterPattern());
		ScanConfigTransformer scanConfigTransformer = new ScanConfigTransformer(cxOneClient);
		ScanConfig scanConfig = null;
		try {
			scanConfig = scanConfigTransformer.constructScanConfig(projectId, projectName, groups, astFilter, tags,
					cxConfig.getSourceDir(), cxConfig.getIncremental(), cxConfig.getPresetName(), null);
		} catch (Exception e) {
			throw new CxClientException(e.getMessage());
		}
		return scanConfig;

	}

	/**
	 * This method is used to get the list of all projectConfigurations
	 * @param projectConfigurationResponse
	 */
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

	/**
	 * This method is used to get languageModeConfiguration from ProjectConfigurations
	 * @param projectConfigurationList
	 * @return projectConfigurationList
	 */
	
	private ProjectConfiguration getLanguageModeConfiguration(List<ProjectConfiguration> projectConfigurationList) {

		ProjectConfiguration projectConfiguration = null;
		for (ProjectConfiguration configuration : projectConfigurationList) {
			if (configuration.getName().equalsIgnoreCase(CxOneConstants.LANGUAGE_MODE)
					&& configuration.getCategory().equalsIgnoreCase(CxOneConstants.SAST))
				return configuration;
		}
		return projectConfiguration;
	}
	/**
	 * This method is used to get presetConfiguration from ProjectConfigurations 
	 * @param projectConfigurationList
	 * @return projectConfigurationList
	 */
	
	private ProjectConfiguration getPresetConfiguration(List<ProjectConfiguration> projectConfigurationList) {
		ProjectConfiguration projectConfiguration = null;
		for (ProjectConfiguration configuration : projectConfigurationList) {
			if (configuration.getName().equalsIgnoreCase(CxOneConstants.PRESET_NAME)
					&& configuration.getCategory().equalsIgnoreCase(CxOneConstants.SAST))
				return configuration;
		}
		return projectConfiguration;
	}
	
	/**
	 * This method is used to get incrementalConfiguration from ProjectConfigurations 
	 * @param projectConfigurationList
	 * @return projectConfigurationList
	 */
	
	private ProjectConfiguration getIncrementalConfiguration(List<ProjectConfiguration> projectConfigurationList) {
		ProjectConfiguration projectConfiguration = null;
		for (ProjectConfiguration configuration : projectConfigurationList) {
			if (configuration.getName().equalsIgnoreCase(CxOneConstants.INCREMENTAL)
					&& configuration.getCategory().equalsIgnoreCase(CxOneConstants.SAST))
				return configuration;
		}
		return projectConfiguration;
	}
	
	/**
	 * This method is used to get filterConfiguration from ProjectConfigurations 
	 * @param projectConfigurationList
	 * @return projectConfigurationList
	 */
	
	private ProjectConfiguration getFilterConfiguration(List<ProjectConfiguration> projectConfigurationList) {
		ProjectConfiguration projectConfiguration = null;
		for (ProjectConfiguration configuration : projectConfigurationList) {
			if (configuration.getName().equalsIgnoreCase(CxOneConstants.FILTER)
					&& configuration.getCategory().equalsIgnoreCase(CxOneConstants.SAST))
				return configuration;
		}
		return projectConfiguration;
	}
	
	/**
	 * This method is used to get a single filter value using sastFolderExclusions and sastFilterPattern
	 * @param sastFolderExclusions
	 * @param sastFilterPattern
	 * @return filter
	 */
	
	private String getFilterConfigurationValue(String sastFolderExclusions, String sastFilterPattern) {
		String filter = "";
		String excludeFoldersPattern = "";
		if (!StringUtils.isEmpty(sastFolderExclusions)) {
			excludeFoldersPattern = Arrays.stream(sastFolderExclusions.split(",")).map(String::trim)
					.collect(Collectors.joining(","));
			excludeFoldersPattern = ZipUtil.processExcludeFolders(excludeFoldersPattern);
		}

		String excludeFilesPattern = "";
		if (!StringUtils.isEmpty(sastFilterPattern))
			excludeFilesPattern = Arrays.stream(sastFilterPattern.split(",")).map(String::trim)
					.collect(Collectors.joining(","));

		if (excludeFoldersPattern != null && !excludeFoldersPattern.isEmpty())
			filter = excludeFoldersPattern;

		if (excludeFilesPattern != null && !excludeFilesPattern.isEmpty())
			if (filter.isEmpty())
				filter = excludeFilesPattern;
			else
				filter += "," + excludeFilesPattern;

		return filter;
	}
}
