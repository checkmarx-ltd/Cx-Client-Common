package com.cx.restclient.sast.utils;


import com.cx.restclient.common.UrlUtils;
import com.cx.restclient.configuration.CxScanConfig;
import com.cx.restclient.cxArm.dto.CxArmConfig;
import com.cx.restclient.dto.*;
import com.cx.restclient.exception.CxClientException;
import com.cx.restclient.exception.CxHTTPClientException;
import com.cx.restclient.httpClient.CxHttpClient;
import com.cx.restclient.osa.dto.ClientType;
import com.cx.restclient.sast.dto.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpResponseException;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.cx.restclient.common.CxPARAM.*;
import static com.cx.restclient.httpClient.utils.ContentType.CONTENT_TYPE_APPLICATION_JSON_V1;
import static com.cx.restclient.httpClient.utils.HttpClientHelper.convertToJson;
import static com.cx.restclient.sast.utils.SASTParam.*;

/**
 * Common parent for SAST and OSA clients.
 * Extracted from {@link com.cx.restclient.CxClientDelegator} for better maintainability.
 */
public abstract class LegacyClient {

    private static final String DEFAULT_AUTH_API_PATH = "CxRestApi/auth/" + AUTHENTICATION;
    public static final String PRESETNAME_PROJET_SETTING_DEFAULT = "Project Default";
    public static final String PRESETID_PROJET_SETTING_DEFAULT = "0";
    private static final Integer UNKNOWN_INT = -1;

    private static final String ID_PATH_PARAM = "{id}";

    protected CxHttpClient httpClient;
    protected CxScanConfig config;
    protected Logger log;
    private String teamPath;
    protected long projectId;
    private State state = State.SUCCESS;
    private boolean isNewProject = false;

    public LegacyClient(CxScanConfig config, Logger log) throws MalformedURLException {
        this.config = config;
        this.log = log;
        initHttpClient(config, log);
        validateConfig(config);
    }

    public void setConfig(CxScanConfig config) {
        this.config = config;
    }

    public void close() {
        if (httpClient != null) {
            httpClient.close();
        }
    }

    public  boolean isIsNewProject() {
        return isNewProject;
    }

    public void setIsNewProject(boolean isNewProject) {
        this.isNewProject = isNewProject;
    }

    public long resolveProjectId() throws IOException {
        List<Project> projects = getProjectByName(config.getProjectName(), config.getTeamId(), teamPath);

        if (projects == null || projects.isEmpty()) { // Project is new
            if (config.getDenyProject()) {
                throw new CxClientException(DENY_NEW_PROJECT_ERROR.replace("{projectName}", config.getProjectName()));
            }
            //Create newProject and checking if EnableSASTBranching is enabled then creating branch project 
            if(config.isEnableSASTBranching()) {
				if (StringUtils.isEmpty(config.getMasterBranchProjName())) {
					throw new CxClientException("Master branch project name is must to create branched project.");
				} else {
					Long masterProjectId;
					List<Project> masterProject = getProjectByName(config.getMasterBranchProjName(), config.getTeamId(),
							teamPath);
					if (masterProject != null && !masterProject.isEmpty()) {
						masterProjectId = masterProject.get(0).getId();
					} else {
						throw new CxClientException(
								"Master branch project does not exist:" + config.getMasterBranchProjName());
					}
					log.info("Project not found, creating a new one.: '{}' with Team '{}'", config.getProjectName(),
							teamPath);
					projectId = createChildProject(masterProjectId, config.getProjectName());
					if (projectId == UNKNOWN_INT) {
						throw new CxClientException(
								"Branched project could not be created: " + config.getProjectName());
					}else {
                        log.info("Created a project with ID {}", projectId);
                        if(config.isEnableDataRetention()){
                            setRetentionRate(projectId);
                        }
                        setIsNewProject(true);
					}
				}
            }
			else {
				CreateProjectRequest request = new CreateProjectRequest(config.getProjectName(), config.getTeamId(),
						config.getPublic());
				log.info("Project not found, creating a new one.: '{}' with Team '{}'", config.getProjectName(),
						teamPath);
				projectId = createNewProject(request, teamPath).getId();
                log.info("Created a project with ID {}", projectId);
                if(config.isEnableDataRetention()) {
                    setRetentionRate(projectId);
                }
                setIsNewProject(true);
			}
        } else {
            projectId = projects.get(0).getId();
            if(config.isEnableDataRetention()){
                setRetentionRate(projectId);
            }
            setIsNewProject(false);
            log.info("Project already exists with ID {}", projectId);
        }

        return projectId;
    }


    public String configureTeamPath() throws IOException, CxClientException {
		if (StringUtils.isEmpty(config.getTeamPath())) {
			List<Team> teamList = populateTeamList();
			// If there is no chosen teamPath, just add first one from the teams
			// list as default
			if (StringUtils.isEmpty(teamPath) && teamList != null && !teamList.isEmpty()) {
				teamPath = teamList.get(0).getFullName();
			}
		} else {
			teamPath = config.getTeamPath();
		}
        httpClient.setTeamPathHeader(teamPath);
        log.debug(String.format("setTeamPathHeader %s", teamPath));
        return teamPath;
    }

    public List<Team> getTeamList() throws IOException, CxClientException {

        return populateTeamList();
    }

    private List<Team> populateTeamList() throws IOException {
        return (List<Team>) httpClient.getRequest(CXTEAMS, CONTENT_TYPE_APPLICATION_JSON_V1, Team.class, 200, "team list", true);
    }


    public String getToken() throws IOException, CxClientException {
        LoginSettings settings = getDefaultLoginSettings();
        settings.setClientTypeForPasswordAuth(ClientType.CLI);
        final TokenLoginResponse tokenLoginResponse = getHttpClient().generateToken(settings);
        return tokenLoginResponse.getRefresh_token();
    }

    public void revokeToken(String token) throws IOException, CxClientException {
        getHttpClient().revokeToken(token);
    }


    private Project createNewProject(CreateProjectRequest request, String teamPath) throws IOException {
        String json = convertToJson(request);
        httpClient.setTeamPathHeader(teamPath);
        StringEntity entity = new StringEntity(json, StandardCharsets.UTF_8);
        return httpClient.postRequest(CREATE_PROJECT, CONTENT_TYPE_APPLICATION_JSON_V1, entity, Project.class, 201, "create new project: " + request.getName());
    }

    private List<Project> getProjectByName(String projectName, String teamId, String teamPath) throws IOException, CxClientException {
        projectName = URLEncoder.encode(projectName, "UTF-8");
        String projectNamePath = SAST_GET_PROJECT.replace("{name}", projectName).replace("{teamId}", teamId);
        List<Project> projects = null;
        try {
            httpClient.setTeamPathHeader(teamPath);
            projects = (List<Project>) httpClient.getRequest(projectNamePath, CONTENT_TYPE_APPLICATION_JSON_V1, Project.class, 200, "project by name: " + projectName, true);
        } catch (CxHTTPClientException ex) {
            if (ex.getStatusCode() != 404) {
                throw ex;
            }
        }
        return projects;
    }

    private void initHttpClient(CxScanConfig config, Logger log) throws MalformedURLException {

        if (!org.apache.commons.lang3.StringUtils.isEmpty(config.getUrl())) {
            httpClient = new CxHttpClient(
                    UrlUtils.parseURLToString(config.getUrl(), "CxRestAPI/"),
                    config.getCxOrigin(),
                    config.getCxOriginUrl(),
                    config.isDisableCertificateValidation(),
                    config.isUseSSOLogin(),
                    config.getRefreshToken(),
                    config.isProxy(),
                    config.getProxyConfig(),
                    log,
                    config.getNTLM());
        }
    }

    public void initiate() throws CxClientException {
        try {
            if (config.isSastOrOSAEnabled()) {
                String version = getCxVersion();
                login(version);
                resolveTeam();
                //httpClient.setTeamPathHeader(this.teamPath);
                if (config.isSastEnabled()) {
                    resolvePreset();
                }
                if (config.getEnablePolicyViolations()) {
                    resolveCxARMUrl();
                }
                resolveEngineConfiguration();
                resolveProjectId();
            }
        } catch (Exception e) {
            throw new CxClientException(e);
        }
    }

    public String getCxVersion() throws IOException, CxClientException {
        String version;
        try {
            config.setCxVersion(httpClient.getRequest(CX_VERSION, CONTENT_TYPE_APPLICATION_JSON_V1, CxVersion.class, 200, "cx Version", false));
            String hotfix = "";
            try {
                if (config.getCxVersion().getHotFix() != null && Integer.parseInt(config.getCxVersion().getHotFix()) > 0) {
                    hotfix = " Hotfix [" + config.getCxVersion().getHotFix() + "].";
                }
            } catch (Exception ex) {
            }

            version = config.getCxVersion().getVersion();
            log.info("Checkmarx server version [" + config.getCxVersion().getVersion() + "]." + hotfix);

        } catch (Exception ex) {
            version = "lower than 9.0";
            log.debug("Checkmarx server version [lower than 9.0]");
        }
        return version;
    }

    public void login() throws IOException {
        String version = getCxVersion();
        login(version);
    }

    public void login(String version) throws IOException, CxClientException {
        // perform login to server
        log.info("Logging into the Checkmarx service.");

        if (config.getToken() != null) {
            httpClient.setToken(config.getToken());
            return;
        }
        LoginSettings settings = getDefaultLoginSettings();
        settings.setRefreshToken(config.getRefreshToken());
        settings.setVersion(version);
        httpClient.login(settings);
    }

    public LoginSettings getDefaultLoginSettings() throws MalformedURLException {
        String baseUrl = UrlUtils.parseURLToString(config.getUrl(), DEFAULT_AUTH_API_PATH);
        LoginSettings result = LoginSettings.builder()
                .accessControlBaseUrl(baseUrl)
                .username(config.getUsername())
                .password(config.getPassword())
                .clientTypeForPasswordAuth(ClientType.RESOURCE_OWNER)
                .clientTypeForRefreshToken(ClientType.CLI)
                .build();

        result.getSessionCookies().addAll(config.getSessionCookie());

        return result;
    }


    public CxHttpClient getHttpClient() {
        return httpClient;
    }

    private void resolveEngineConfiguration() throws IOException {
        if (config.getEngineConfigurationId() == null && config.getEngineConfigurationName() == null) {
            config.setEngineConfigurationId(1);
        } else if (config.getEngineConfigurationName() != null) {
            final List<EngineConfiguration> engineConfigurations = getEngineConfiguration();
            for (EngineConfiguration engineConfiguration : engineConfigurations) {
                if (engineConfiguration.getName().equalsIgnoreCase(config.getEngineConfigurationName())) {
                    config.setEngineConfigurationId(engineConfiguration.getId());
                    log.info(String.format("Engine configuration: \"%s\" was validated in server", config.getEngineConfigurationName()));
                }else{
                    if ("Improved Scan Flow".equalsIgnoreCase(config.getEngineConfigurationName())){
                        config.setEngineConfigurationId(1);
                    }
                }
            }
            if (config.getEngineConfigurationId() == null) {
                throw new CxClientException("Engine configuration: \"" + config.getEngineConfigurationName() + "\" was not found in server");
            }
        }
    }

    public List<EngineConfiguration> getEngineConfiguration() throws IOException {
        this.teamPath = configureTeamPath();
        httpClient.setTeamPathHeader(this.teamPath);
        return (List<EngineConfiguration>) httpClient.getRequest(SAST_ENGINE_CONFIG, CONTENT_TYPE_APPLICATION_JSON_V1, EngineConfiguration.class, 200, "engine configurations", true);
    }


    public void validateConfig(CxScanConfig config) throws CxClientException {
        String message = null;
        if (config == null) {
            message = "Non-null config must be provided.";
        } else if (org.apache.commons.lang3.StringUtils.isEmpty(config.getUrl()) && config.isSastOrOSAEnabled()) {
            message = "Server URL is required when SAST or OSA is enabled.";
        }
        if (message != null) {
            throw new CxClientException(message);
        }
    }

    private void resolveTeam() throws CxClientException, IOException {

        config.setTeamPath(configureTeamPath());

        if (config.getTeamId() == null) {
            config.setTeamId(getTeamIdByName(config.getTeamPath()));
        }

        printTeamPath();
    }

    public String getTeamIdByName(String teamName) throws CxClientException, IOException {
        teamName = replaceDelimiters(teamName);
        List<Team> allTeams = getTeamList();
        for (Team team : allTeams) {
            String fullName = replaceDelimiters(team.getFullName());
            if (fullName.equalsIgnoreCase(teamName)) { //TODO caseSenesitive
                return team.getId();
            }
        }
        throw new CxClientException("Could not resolve team ID from team name: " + teamName);
    }

    private String replaceDelimiters(String teamName) {
        while (teamName.contains("\\") || teamName.contains("//")) {
            teamName = teamName.replace("\\", "/");
            teamName = teamName.replace("//", "/");
        }
        return teamName;
    }

    private CxArmConfig getCxARMConfig() throws IOException, CxClientException {
        httpClient.setTeamPathHeader(this.teamPath);
        return httpClient.getRequest(CX_ARM_URL, CONTENT_TYPE_APPLICATION_JSON_V1, CxArmConfig.class, 200, "CxARM URL", false);
    }

    private void resolveCxARMUrl() throws CxClientException {
        try {
            this.config.setCxARMUrl(getCxARMConfig().getCxARMPolicyURL());
        } catch (Exception ex) {
            throw new CxClientException("CxARM is not available. Policy violations cannot be calculated: " + ex.getMessage());
        }
    }

    //Some plugins share preset name while others share presetId in CxScanConfig
    //If is given preference.
    //presetId=0 is a special case, which does not exist in SAST but SAST has a special meaning for it,
    // which is to use whatever preset available on the project settings.
    private void resolvePreset() throws CxClientException, IOException {
        if (config.getPresetId() == null && !StringUtils.isEmpty(config.getPresetName())) {
            if(PRESETNAME_PROJET_SETTING_DEFAULT.equalsIgnoreCase(config.getPresetName()))
            		config.setPresetId(Integer.parseInt(PRESETID_PROJET_SETTING_DEFAULT));
            else
            	config.setPresetId(getPresetIdByName(config.getPresetName()));
            
        }else if(config.getPresetId() == Integer.parseInt(PRESETID_PROJET_SETTING_DEFAULT)) {
        	config.setPresetName(PRESETNAME_PROJET_SETTING_DEFAULT);
        }else {
        	config.setPresetName(getPresetById(config.getPresetId()).getName());
        }
        log.info(String.format("preset name: %s preset id: %s", config.getPresetName(), config.getPresetId()));
    }

    public int getPresetIdByName(String presetName) throws CxClientException, IOException {
        List<Preset> allPresets = getPresetList();
        for (Preset preset : allPresets) {
            if (preset.getName().equalsIgnoreCase(presetName)) { //TODO caseSenesitive- checkkk
                return preset.getId();
            }
        }

        throw new CxClientException("Could not resolve preset ID from preset name: " + presetName);
    }

    public List<Preset> getPresetList() throws IOException, CxClientException {
        configureTeamPath();
        return (List<Preset>) httpClient.getRequest(CXPRESETS, CONTENT_TYPE_APPLICATION_JSON_V1, Preset.class, 200, "preset list", true);
    }

    public Preset getPresetById(int presetId) throws IOException, CxClientException {
        httpClient.setTeamPathHeader(this.teamPath);
        return httpClient.getRequest(CXPRESETS + "/" + presetId, CONTENT_TYPE_APPLICATION_JSON_V1, Preset.class, 200, "preset by id", false);
    }

    public List<PostAction> getPostScanActionList() throws IOException, CxClientException {
        configureTeamPath();
        return (List<PostAction>) httpClient.getRequest(SAST_CUSTOM_TASKS, CONTENT_TYPE_APPLICATION_JSON_V1, PostAction.class, 200, "post scan action list", true);
    }
    
    private void printTeamPath() {
        try {
            this.teamPath = config.getTeamPath();
            if (this.teamPath == null) {
                this.teamPath = getTeamNameById(config.getTeamId());
            }
            log.info(String.format("full team path: %s", this.teamPath));
        } catch (Exception e) {
            log.warn("Error getting team path.");
        }
    }

    private void setRetentionRate(long projectId) throws IOException {
        DataRetentionSettingsDto retentionRequest = new DataRetentionSettingsDto(config.getProjectRetentionRate());
        log.info("Sending request to set scan retention rate for project with id {}",projectId);
        String json = convertToJson(retentionRequest);
        httpClient.setTeamPathHeader(teamPath);
        HttpEntity entity = new StringEntity(json);
        try{
             httpClient.postRequest(SAST_RETENTION_RATE.replace(ID_PATH_PARAM, Long.toString(projectId)), CONTENT_TYPE_APPLICATION_JSON_V1, entity, CxID.class, 204, "Set scan retention rate for project");
                log.info("Set '{}' scan retention rate for project with project ID : '{}' ",config.getProjectRetentionRate(), projectId);
              }catch (CxHTTPClientException exception){
            log.info(exception.getMessage());
            log.info("Fail to set scan retention rate for project with project ID {}", projectId);
            }
    }
    public String getTeamNameById(String teamId) throws CxClientException, IOException {
        List<Team> allTeams = getTeamList();
        for (Team team : allTeams) {
            if (teamId.equals(team.getId())) {
                return team.getFullName();
            }
        }
        throw new CxClientException("Could not resolve team name from id: " + teamId);
    }


    public List<Project> getAllProjects() throws IOException, CxClientException {
        List<Project> projects = null;
        configureTeamPath();

        try {
            projects = (List<Project>) httpClient.getRequest(SAST_GET_ALL_PROJECTS, CONTENT_TYPE_APPLICATION_JSON_V1, Project.class, 200, "all projects", true);
        } catch (HttpResponseException ex) {
            if (ex.getStatusCode() != 404) {
                throw ex;
            }
        }
        return projects;
    }

    public Project getProjectById(String projectId, String contentType) throws IOException, CxClientException {
        String projectNamePath = SAST_GET_PROJECT_BY_ID.replace("{projectId}", projectId);
        Project projects = null;
        try {
            httpClient.setTeamPathHeader(this.teamPath);
            projects = httpClient.getRequest(projectNamePath, contentType, Project.class, 200, "project by id: " + projectId, false);
        } catch (CxHTTPClientException ex) {
            if (ex.getStatusCode() != 404) {
                throw ex;
            }
        }
        return projects;
    }


    public List<CxNameObj> getConfigurationSetList() throws IOException, CxClientException {
        configureTeamPath();
        return (List<CxNameObj>) httpClient.getRequest(SAST_ENGINE_CONFIG, CONTENT_TYPE_APPLICATION_JSON_V1, CxNameObj.class, 200, "engine configurations", true);
    }

    public String getTeamPath() {
        return teamPath;
    }

    public void setTeamPath(String teamPath) {
        this.teamPath = teamPath;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }
     ///function to create child project from master branch   
    public long createChildProject(long projectId, String childProjectName)throws IOException, CxClientException {
    	long childProjectId = UNKNOWN_INT;
    	CreateProjectRequest request = new CreateProjectRequest(childProjectName);
    	String json = convertToJson(request);
        httpClient.setTeamPathHeader(teamPath);    	
        StringEntity entity = new StringEntity(json, StandardCharsets.UTF_8);           	
        log.info("Creating branched project with name '{}' from existing project with ID {}", childProjectName, projectId);
        try {
        	Project obj = httpClient.postRequest(PROJECT_BRANCH.replace("{id}", Long.toString(projectId)), CONTENT_TYPE_APPLICATION_JSON_V1, entity, Project.class, 201, "branch project");
            if (obj != null) {
            	childProjectId = obj.getId();
            	return childProjectId;
                
            } else {
                log.error("CX Response for branch project request with name '{}' from existing project with ID {} was null", childProjectName, projectId);
            }            
        }        
        catch (CxHTTPClientException e) {
	        	log.error(e.getMessage());
	            log.error("Error occured while creating branched project with name '{}' from existing project with ID {}",  childProjectName, projectId);       
	    }
        catch (JSONException e) {
        log.error("Error processing JSON Response while creating branched project with name '{}' from existing project with ID {}", childProjectName, projectId);
        log.error(ExceptionUtils.getStackTrace(e));        
        }         
        return childProjectId;
    }
}
