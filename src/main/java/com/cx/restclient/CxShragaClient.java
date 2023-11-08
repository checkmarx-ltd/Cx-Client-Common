package com.cx.restclient;

import com.cx.restclient.common.DependencyScanner;
import com.cx.restclient.common.UrlUtils;
import com.cx.restclient.common.summary.SummaryUtils;
import com.cx.restclient.configuration.CxScanConfig;
import com.cx.restclient.cxArm.dto.CxArmConfig;
import com.cx.restclient.dto.*;
import com.cx.restclient.exception.CxClientException;
import com.cx.restclient.exception.CxHTTPClientException;
import com.cx.restclient.httpClient.CxHttpClient;
import com.cx.restclient.osa.dto.ClientType;
import com.cx.restclient.sast.dto.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.HttpResponseException;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Properties;

import static com.cx.restclient.common.CxPARAM.*;
import static com.cx.restclient.cxArm.utils.CxARMUtils.getPoliciesNames;
import static com.cx.restclient.httpClient.utils.ContentType.CONTENT_TYPE_APPLICATION_JSON_V1;
import static com.cx.restclient.httpClient.utils.HttpClientHelper.convertToJson;
import static com.cx.restclient.sast.utils.SASTParam.*;

/**
 * Created by Galn on 05/02/2018.
 */
//SHRAGA
//System Holistic Rest Api Generic Application
public class CxShragaClient {
    private static final String DEFAULT_AUTH_API_PATH = "CxRestApi/auth/";
    private static final String PRINT_LINE = "-----------------------------------------------------------------------------------------";

    private CxHttpClient httpClient;
    private Logger log;
    private CxScanConfig config;
    private long projectId;
    private String teamPath;
    private CxSASTClient sastClient;

    private long sastScanId;
    private SASTResults sastResults = new SASTResults();
    private DependencyScanResults dependencyScanResults = new DependencyScanResults();

    private DependencyScanner dependencyScanner;

    public CxShragaClient(CxScanConfig config, Logger log) throws MalformedURLException, CxClientException {
        validateConfig(config);

        this.config = config;
        this.log = log;

        if (!StringUtils.isEmpty(config.getUrl())) {
            this.httpClient = new CxHttpClient(
                    UrlUtils.parseURLToString(config.getUrl(), "CxRestAPI/"),
                    config.getCxOrigin(),
                    config.isDisableCertificateValidation(),
                    config.isUseSSOLogin(),
                    config.getRefreshToken(),
                    config.isProxy(),
                    config.getProxyConfig(),
                    log);
        }

        if (config.getSastEnabled()) {
            sastClient = new CxSASTClient(httpClient, log, config);
        }

        if (config.getDependencyScannerType() == DependencyScannerType.OSA) {
            dependencyScanner = new CxOSAClient(httpClient, log, config);
        } else if (config.getDependencyScannerType() == DependencyScannerType.SCA) {
            dependencyScanner = new SCAClient(config, log);
        }
    }

    private void validateConfig(CxScanConfig config) throws CxClientException {
        String message = null;
        if (config == null) {
            message = "Non-null config must be provided.";
        } else if (StringUtils.isEmpty(config.getUrl()) && config.isSastOrOSAEnabled()) {
            message = "Server URL is required when SAST or OSA is enabled.";
        }
        if (message != null) {
            throw new CxClientException(message);
        }
    }

    public CxShragaClient(String serverUrl, String username, String password, String origin, boolean disableCertificateValidation, Logger log) throws MalformedURLException, CxClientException {
        this(new CxScanConfig(serverUrl, username, password, origin, disableCertificateValidation), log);
    }

    //API Scans methods
    public String getClientVersion() {
        String version = "";
        try {
            Properties properties = new Properties();
            java.io.InputStream is = getClass().getClassLoader().getResourceAsStream("common.properties");
            if (is != null) {
                properties.load(is);
                version = properties.getProperty("version");
            }
        } catch (Exception e) {

        }
        return version;
    }

    public void init() throws CxClientException, IOException {
        log.info("Initializing Cx client [" + getClientVersion() + "]");
        if (config.isSastOrOSAEnabled()) {
            String version = getCxVersion();
            login(version);
            resolveTeam();
            httpClient.setTeamPathHeader(this.teamPath);
            if (config.getSastEnabled()) {
                resolvePreset();
            }
            if (config.getEnablePolicyViolations()) {
                resolveCxARMUrl();
            }
            resolveEngineConfiguration();
            resolveProject();
        }

        if (dependencyScanner != null) {
            dependencyScanner.init();
        }
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

    public long createSASTScan() throws IOException, CxClientException {
        sastScanId = getSastClient().createSASTScan(projectId);
        sastResults.setSastScanLink(config.getUrl(), sastScanId, projectId);
        return sastScanId;
    }

    public String createDependencyScan() throws CxClientException {
        String scanId = getDependencyScanner().createScan(dependencyScanResults);
        return scanId;
    }

    public void cancelSASTScan() throws IOException, CxClientException {
        getSastClient().cancelSASTScan(sastScanId);
    }

    public SASTResults waitForSASTResults() throws InterruptedException, CxClientException, IOException {
        sastResults = getSastClient().waitForSASTResults(sastScanId, projectId);
        return sastResults;
    }

    public SASTResults getLatestSASTResults() throws InterruptedException, CxClientException, IOException {
        sastResults = getSastClient().getLatestSASTResults(projectId);
        return sastResults;
    }

    public DependencyScanResults waitForDependencyScanResults() throws CxClientException {
        getDependencyScanner().waitForScanResults(dependencyScanResults);
        return dependencyScanResults;
    }

    public DependencyScanResults getLatestDependencyScanResults() throws CxClientException {
        dependencyScanResults = getDependencyScanner().getLatestScanResults();
        return dependencyScanResults;
    }

    public void printIsProjectViolated() {
        if (config.getEnablePolicyViolations()) {
            log.info(PRINT_LINE);
            log.info("Policy Management: ");
            log.info("--------------------");

            boolean hasOsaViolations = dependencyScanResults != null &&
                    dependencyScanResults.getOsaResults() != null &&
                    dependencyScanResults.getOsaResults().getOsaPolicies() != null &&
                    !dependencyScanResults.getOsaResults().getOsaPolicies().isEmpty();

            if (sastResults.getSastPolicies().isEmpty() && !hasOsaViolations) {
                log.info(PROJECT_POLICY_COMPLIANT_STATUS);
                log.info(PRINT_LINE);
            } else {
                log.info(PROJECT_POLICY_VIOLATED_STATUS);
                if (!sastResults.getSastPolicies().isEmpty()) {
                    log.info("SAST violated policies names: " + getPoliciesNames(sastResults.getSastPolicies()));
                }
                if (hasOsaViolations) {
                    log.info("OSA violated policies names: " + getPoliciesNames(dependencyScanResults.getOsaResults().getOsaPolicies()));
                }
                log.info(PRINT_LINE);
            }
        }
    }

    /**
     * @param config The following config properties are used:
     *               scaConfig
     *               proxyConfig
     *               cxOrigin
     *               disableCertificateValidation
     */
    public static void testScaConnection(CxScanConfig config, Logger log) throws CxClientException {
        SCAClient client = new SCAClient(config, log);
        try {
            client.testConnection();
        } catch (IOException e) {
            throw new CxClientException(e);
        }
    }

    private CxArmConfig getCxARMConfig() throws IOException, CxClientException {
        httpClient.setTeamPathHeader(this.teamPath);
        return httpClient.getRequest(CX_ARM_URL, CONTENT_TYPE_APPLICATION_JSON_V1, CxArmConfig.class, 200, "CxARM URL", false);
    }

    public String generateHTMLSummary() throws Exception {
        return SummaryUtils.generateSummary(sastResults, dependencyScanResults, config);
    }

    public String generateHTMLSummary(SASTResults sastResults, DependencyScanResults dependencyScanResults) throws Exception {
        return SummaryUtils.generateSummary(sastResults, dependencyScanResults, config);
    }

    public List<Project> getAllProjects() throws IOException, CxClientException {
        List<Project> projects = null;
        List<Team> teamList = getTeamList();

        try {
            httpClient.setTeamPathHeader(this.teamPath);
            projects = (List<Project>) httpClient.getRequest(SAST_GET_ALL_PROJECTS, CONTENT_TYPE_APPLICATION_JSON_V1, Project.class, 200, "all projects", true);
        } catch (HttpResponseException ex) {
            if (ex.getStatusCode() != 404) {
                throw ex;
            }
        }
        return projects;
    }

    public void close() {
        httpClient.close();
    }

    //HELP config  Methods


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

    public String getToken() throws IOException, CxClientException {
        LoginSettings settings = getDefaultLoginSettings();
        settings.setClientTypeForPasswordAuth(ClientType.CLI);
        final TokenLoginResponse tokenLoginResponse = httpClient.generateToken(settings);
        return tokenLoginResponse.getRefresh_token();
    }

    public void revokeToken(String token) throws IOException, CxClientException {
        httpClient.revokeToken(token);
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

    public String getTeamNameById(String teamId) throws CxClientException, IOException {
        List<Team> allTeams = getTeamList();
        for (Team team : allTeams) {
            if (teamId.equals(team.getId())) {
                return team.getFullName();
            }
        }
        throw new CxClientException("Could not resolve team name from id: " + teamId);
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

    private List<Team> populateTeamList() throws IOException, CxClientException {
        return (List<Team>) httpClient.getRequest(CXTEAMS, CONTENT_TYPE_APPLICATION_JSON_V1, Team.class, 200, "team list", true);
    }

    public List<Team> getTeamList() throws IOException, CxClientException {

        List<Team> teamList = populateTeamList();
        //If there is no chosen teamPath, just add first one from the teams list as default
        if (StringUtils.isEmpty(this.teamPath) && teamList != null && !teamList.isEmpty()) {
            this.teamPath = teamList.get(0).getFullName();
        }
        httpClient.setTeamPathHeader(this.teamPath);
        log.debug("getTeamList setTeamPathHeader " + this.teamPath);
        return teamList;
    }

    public Preset getPresetById(int presetId) throws IOException, CxClientException {
        httpClient.setTeamPathHeader(this.teamPath);
        return httpClient.getRequest(CXPRESETS + "/" + presetId, CONTENT_TYPE_APPLICATION_JSON_V1, Preset.class, 200, "preset by id", false);
    }

    public List<Preset> getPresetList() throws IOException, CxClientException {
        List<Team> teamList = getTeamList();
        httpClient.setTeamPathHeader(this.teamPath);
        return (List<Preset>) httpClient.getRequest(CXPRESETS, CONTENT_TYPE_APPLICATION_JSON_V1, Preset.class, 200, "preset list", true);
    }

    public List<CxNameObj> getConfigurationSetList() throws IOException, CxClientException {
        List<Team> teamList = getTeamList();
        httpClient.setTeamPathHeader(this.teamPath);
        return (List<CxNameObj>) httpClient.getRequest(SAST_ENGINE_CONFIG, CONTENT_TYPE_APPLICATION_JSON_V1, CxNameObj.class, 200, "engine configurations", true);
    }

    public List<EngineConfiguration> getEngineConfiguration() throws IOException {
        List<Team> teamList = getTeamList();
        httpClient.setTeamPathHeader(this.teamPath);
        return (List<EngineConfiguration>) httpClient.getRequest(SAST_ENGINE_CONFIG, CONTENT_TYPE_APPLICATION_JSON_V1, EngineConfiguration.class, 200, "engine configurations", true);
    }

    public void setOsaFSAProperties(Properties fsaConfig) {  //For CxMaven plugin
        config.setOsaFsaConfig(fsaConfig);
    }
    //Private methods

    private void resolveTeam() throws CxClientException, IOException {
        if (config.getTeamId() == null) {
            config.setTeamId(getTeamIdByName(config.getTeamPath()));
        }
        printTeamPath();
        httpClient.setTeamPathHeader(this.teamPath);
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

    private void resolveProject() throws IOException, CxClientException {
        List<Project> projects = getProjectByName(config.getProjectName(), config.getTeamId());
        if (projects == null || projects.isEmpty()) { // Project is new
            if (config.getDenyProject()) {
                throw new CxClientException(DENY_NEW_PROJECT_ERROR.replace("{projectName}", config.getProjectName()));
            }
            //Create newProject
            CreateProjectRequest request = new CreateProjectRequest(config.getProjectName(), config.getTeamId(), config.getPublic());
            projectId = createNewProject(request).getId();
            config.setExestingProject(false);

        } else {
            projectId = projects.get(0).getId();
            config.setExestingProject(true);
        }

        // SAST and OSA share the same project ID.
        if (dependencyScanner instanceof CxOSAClient) {
            ((CxOSAClient) dependencyScanner).setProjectId(projectId);
        }
    }

    private List<Project> getProjectByName(String projectName, String teamId) throws IOException, CxClientException {
        projectName = URLEncoder.encode(projectName, "UTF-8");
        String projectNamePath = SAST_GET_PROJECT.replace("{name}", projectName).replace("{teamId}", teamId);
        List<Project> projects = null;
        try {
            httpClient.setTeamPathHeader(this.teamPath);
            projects = (List<Project>) httpClient.getRequest(projectNamePath, CONTENT_TYPE_APPLICATION_JSON_V1, Project.class, 200, "project by name: " + projectName, true);
        } catch (CxHTTPClientException ex) {
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

    private Project createNewProject(CreateProjectRequest request) throws CxClientException, IOException {
        String json = convertToJson(request);
        httpClient.setTeamPathHeader(this.teamPath);
        StringEntity entity = new StringEntity(json, StandardCharsets.UTF_8);
        return httpClient.postRequest(CREATE_PROJECT, CONTENT_TYPE_APPLICATION_JSON_V1, entity, Project.class, 201, "create new project: " + request.getName());
    }

    private LoginSettings getDefaultLoginSettings() throws MalformedURLException {
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

    private DependencyScanner getDependencyScanner() throws CxClientException {
        if (dependencyScanner == null) {
            String message = String.format("The action can't be performed, because dependency scanner type is set to %s in scan configuration.",
                    DependencyScannerType.NONE);

            throw new CxClientException(message);
        }
        return dependencyScanner;
    }

    private CxSASTClient getSastClient() throws CxClientException {
        if (sastClient == null) {
            throw new CxClientException("The action can't be performed, because SAST is disabled in scan configuration.");
        }
        return sastClient;
    }

    public ResponseQueueScanStatus getStatus(String scanId) throws IOException {
        return sastClient.getSASTScanStatus(scanId);
    }


    public Long getProjectId() {
        return projectId;
    }

    public ScanSettingResponse getScanSetting(Long projectId) throws IOException {
        return sastClient.getScanSetting(projectId);
    }

    public List<LastScanResponse> getLastScansByProjectId(long projectId) throws IOException {
        return sastClient.getLatestSASTStatus(projectId);
    }

    public List<Cookie> ssoLegacyLogin() {
        return httpClient.ssoLegacyLogin();
    }

    public String getTeamPath() {
        return teamPath;
    }

    public void setTeamPath(String teamPath) {
        this.teamPath = teamPath;
    }
}