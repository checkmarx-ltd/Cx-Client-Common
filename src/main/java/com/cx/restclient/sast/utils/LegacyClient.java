package com.cx.restclient.sast.utils;


import com.cx.restclient.common.UrlUtils;
import com.cx.restclient.configuration.CxScanConfig;
import com.cx.restclient.cxArm.dto.CxArmConfig;
import com.cx.restclient.dto.*;
import com.cx.restclient.exception.CxClientException;
import com.cx.restclient.exception.CxHTTPClientException;
import com.cx.restclient.httpClient.CxHttpClient;
import com.cx.restclient.osa.dto.ClientType;

import com.cx.restclient.sast.dto.CreateProjectRequest;
import com.cx.restclient.sast.dto.CxNameObj;
import com.cx.restclient.sast.dto.Preset;
import com.cx.restclient.sast.dto.Project;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.HttpResponseException;
import org.apache.http.entity.StringEntity;
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

    private static final String DEFAULT_AUTH_API_PATH = "CxRestApi/auth/";
    protected CxHttpClient httpClient;
    protected final CxScanConfig config;
    protected Logger log;
    private String teamPath;
    protected long projectId;

    
    public LegacyClient(CxScanConfig config, Logger log) throws MalformedURLException {
        this.config = config;
        this.log = log;
        initHttpClient(config, log);
        validateConfig(config);
    }

    public void close(){
        if(httpClient != null) {
            httpClient.close();
        }
    }

    public long resolveProjectId() throws IOException {
        List<Project> projects = getProjectByName(config.getProjectName(), config.getTeamId(), teamPath);
        
        if (projects == null || projects.isEmpty()) { // Project is new
            if (config.getDenyProject()) {
                throw new CxClientException(DENY_NEW_PROJECT_ERROR.replace("{projectName}", config.getProjectName()));
            }
            //Create newProject
            CreateProjectRequest request = new CreateProjectRequest(config.getProjectName(), config.getTeamId(), config.getPublic());
            projectId = createNewProject(request, teamPath).getId();

        } else {
            projectId = projects.get(0).getId();
        }
        
        return projectId;
    }


    public String configureTeamPath() throws IOException, CxClientException {

        List<Team> teamList = populateTeamList();
        //If there is no chosen teamPath, just add first one from the teams list as default
        if (StringUtils.isEmpty(teamPath) && teamList != null && !teamList.isEmpty()) {
            teamPath =  teamList.get(0).getFullName();
        }
        httpClient.setTeamPathHeader(teamPath);
        log.debug(" setTeamPathHeader " + teamPath);
        return teamPath;
    }
    
    public  List<Team> getTeamList() throws IOException, CxClientException {

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
                    config.isDisableCertificateValidation(),
                    config.isUseSSOLogin(),
                    config.getRefreshToken(),
                    config.getProxyConfig(),
                    log);
        }
    }


    public void init() throws CxClientException {
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
        }catch(IOException e){
            throw new CxClientException (e.getMessage());
        }
    }
    
    
    
    public String getCxVersion() throws IOException, CxClientException {
        String version = "";
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

        if(config.getToken() != null){
            httpClient.setToken(config.getToken());
            return;
        }
        LoginSettings settings = getDefaultLoginSettings();
        settings.setRefreshToken(config.getRefreshToken());
        settings.setVersion(version);
        httpClient.login(settings);
    }

    public LoginSettings getDefaultLoginSettings() throws MalformedURLException {
        LoginSettings result = new LoginSettings();

        String baseUrl = UrlUtils.parseURLToString(config.getUrl(), DEFAULT_AUTH_API_PATH);
        result.setAccessControlBaseUrl(baseUrl);

        result.setUsername(config.getUsername());
        result.setPassword(config.getPassword());
        result.getSessionCookies().addAll(config.getSessionCookie());
        result.setClientTypeForPasswordAuth(ClientType.RESOURCE_OWNER);
        result.setClientTypeForRefreshToken(ClientType.CLI);

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
                    log.info("Engine configuration: \"" + config.getEngineConfigurationName() + "\" was validated in server");
                }
            }
            if (config.getEngineConfigurationId() == null) {
                throw new CxClientException("Engine configuration: \"" + config.getEngineConfigurationName() + "\" was not found in server");
            }
        }
    }

    public List<EngineConfiguration> getEngineConfiguration() throws IOException {
        configureTeamPath();
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
        
        configureTeamPath();
        
        if (config.getTeamId() == null) {
            config.setTeamId(getTeamIdByName(config.getTeamPath()));
        }
        
        printTeamPath();
        
        //httpClient.setTeamPathHeader(this.teamPath);
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

    private void resolvePreset() throws CxClientException, IOException {
        if (config.getPresetId() == null) {
            config.setPresetId(getPresetIdByName(config.getPresetName()));
        }
        printPresetName();
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

    
    private void printPresetName() {
        try {
            String presetName = config.getPresetName();
            if (presetName == null) {
                presetName = getPresetById(config.getPresetId()).getName();
            }
            log.info("preset name: " + presetName);
        } catch (Exception e) {
        }
    }

    public Preset getPresetById(int presetId) throws IOException, CxClientException {
        httpClient.setTeamPathHeader(this.teamPath);
        return httpClient.getRequest(CXPRESETS + "/" + presetId, CONTENT_TYPE_APPLICATION_JSON_V1, Preset.class, 200, "preset by id", false);
    }
    
    private void printTeamPath() {
        try {
            this.teamPath = config.getTeamPath();
            if (this.teamPath == null) {
                this.teamPath = getTeamNameById(config.getTeamId());
            }
            log.info("full team path: " + this.teamPath);
        } catch (Exception e) {
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

    public Project getProjectById(String projectId,String contentType) throws IOException, CxClientException {
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

}
