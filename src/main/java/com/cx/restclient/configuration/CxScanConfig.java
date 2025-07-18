package com.cx.restclient.configuration;

import com.cx.restclient.ast.dto.sast.AstSastConfig;
import com.cx.restclient.ast.dto.sca.AstScaConfig;
import com.cx.restclient.dto.*;
import com.cx.restclient.sast.dto.ReportType;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.cookie.Cookie;

import java.io.File;
import java.io.Serializable;
import java.util.*;

/**
 * Created by galn on 21/12/2016.
 */
public class CxScanConfig implements Serializable {

    private String cxOrigin;
    private String cxOriginUrl;
    private CxVersion cxVersion;
    private boolean showCriticalLabel = false;
    

    public boolean isShowCriticalLabel() {
		return showCriticalLabel;
	}

	public void setShowCriticalLabel(boolean showCriticalLabel) {
		this.showCriticalLabel = showCriticalLabel;
	}

	private Integer projectRetentionRate;
    private boolean enableDataRetention;
    private boolean disableCertificateValidation = false;
    private boolean useSSOLogin = false;

    private String sourceDir;
    private String osaLocationPath;
    private File reportsDir;
    // Map<reportType, reportPath> / (e.g. PDF to its file path)
    private Map<ReportType, String> reports = new HashMap<>();    
    private String username;
    private String password;
    private String refreshToken;
    private String url;
    private String projectName;
    private String teamPath;
    private String mvnPath;
    private String teamId;
    private Boolean denyProject = false;
    private Boolean hideResults = false;
    private Boolean continueBuild = false;
    private Boolean isPublic = true;
    private Boolean forceScan = false;
    private String presetName;
    private Integer presetId;
    private String postScanName;
    private String sastFolderExclusions;
    private String sastFilterPattern;
    private Integer sastScanTimeoutInMinutes;
    private Integer scaScanTimeoutInMinutes;
    private Integer osaScanTimeoutInMinutes;
    private String scanComment;
    private Boolean isIncremental = false;
    private Boolean isSynchronous = false;
    private Boolean sastThresholdsEnabled = false;
    private Boolean sastEnableCriticalSeverity = false;
	private Integer sastCriticalThreshold;
    private Integer sastHighThreshold;
    private Integer sastMediumThreshold;
    private Integer sastLowThreshold;
    private Boolean sastNewResultsThresholdEnabled = false;
    private String sastNewResultsThresholdSeverity;
    private TokenLoginResponse token;
    private Boolean generatePDFReport = false;
    private File zipFile;
    private Integer engineConfigurationId;
    private String engineConfigurationName;
    private String projectCustomFields;
    private boolean ignoreBenignErrors = false;
    private String pluginVersion;

    public String getPluginVersion() {
		return pluginVersion;
	}

	public void setPluginVersion(String pluginVersion) {
		this.pluginVersion = pluginVersion;
	}

	private String osaFolderExclusions;    
    public String getEngineConfigurationName() {
        return engineConfigurationName;
    }

    public void setEngineConfigurationName(String engineConfigurationName) {
        this.engineConfigurationName = engineConfigurationName;
    }
    
    public String getprojectCustomFields() {
        return projectCustomFields;
    }

    public void setprojectCustomFields(String projectCustomFields) {
        this.projectCustomFields = projectCustomFields;
    }

    private String osaFilterPattern;
    private String osaArchiveIncludePatterns;
    private Boolean osaGenerateJsonReport = true;
    private Boolean osaRunInstall = false;
    private Boolean osaThresholdsEnabled = false;
    private Boolean osaFailOnError = false;
    private Integer osaCriticalThreshold;
	private Integer osaHighThreshold;
    private Integer osaMediumThreshold;
    private Integer osaLowThreshold;
    private Properties osaFsaConfig; //for MAVEN
    private String osaDependenciesJson;
    private Boolean avoidDuplicateProjectScans = false;
    private boolean enablePolicyViolations = false;
    private boolean enablePolicyViolationsSCA = false;
    private Boolean generateXmlReport = true;

    private String cxARMUrl;
    private String[] paths;
    //remote source control
    private RemoteSourceTypes remoteType = null;
    private String remoteSrcUser;
    private String remoteSrcPass;
    private String remoteSrcUrl;
    private int remoteSrcPort;
    private byte[] remoteSrcKeyFile;
    private String remoteSrcBranch;
    private String perforceMode;

    // CLI config properties
    private Integer progressInterval;
    private Integer osaProgressInterval;
    private Integer connectionRetries;
    private String osaScanDepth;
    private Integer maxZipSize;
    private String defaultProjectName;

    private String scaJsonReport;

    private AstScaConfig astScaConfig;
    private AstSastConfig astSastConfig;

    private final Set<ScannerType> scannerTypes = new HashSet<>();
    private final List<Cookie> sessionCookies = new ArrayList<>();
    private Boolean isProxy = true;
    private Boolean isScaProxy = false;
    private ProxyConfig proxyConfig;
    private ProxyConfig proxyScaConfig;
    private Boolean useNTLM = false;
    private boolean generateScaReport = false;
    private boolean hasScaReportFormat = false;
	private String scaReportFormat;

    private Integer postScanActionId;

    private String customFields;
    
    private String projectLevelCustomFields;
    
    private boolean isOverrideProjectSetting = false;

    public boolean isOverrideRetentionRate() {
        return isOverrideRetentionRate;
    }

    public void setOverrideRetentionRate(boolean overrideRetentionRate) {
        isOverrideRetentionRate = overrideRetentionRate;
    }

    private boolean isOverrideRetentionRate = false;

    private Boolean enableSastBranching = false;
    
    private String masterBranchProjName;
    
    private Integer copyBranchTimeOutInSeconds;

    public CxScanConfig() {
    }

    public CxScanConfig(String url, String username, String password, String cxOrigin, boolean disableCertificateValidation) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.cxOrigin = cxOrigin;
        this.disableCertificateValidation = disableCertificateValidation;
    }

    public CxScanConfig(String url, String username, String password, String cxOrigin, String cxOriginUrl, boolean disableCertificateValidation) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.cxOrigin = cxOrigin;
        this.cxOriginUrl = cxOriginUrl;
        this.disableCertificateValidation = disableCertificateValidation;
    }


    public CxScanConfig(String url, String refreshToken, String cxOrigin, boolean disableCertificateValidation) {
        this.url = url;
        this.refreshToken = refreshToken;
        this.cxOrigin = cxOrigin;
        this.disableCertificateValidation = disableCertificateValidation;
    }

    public boolean isSastEnabled() {
        return scannerTypes.contains(ScannerType.SAST);
    }

    public boolean isOsaEnabled() {
        return scannerTypes.contains(ScannerType.OSA);
    }

    public boolean isAstScaEnabled() {
        return scannerTypes.contains(ScannerType.AST_SCA);
    }

    public boolean isAstSastEnabled() {
        return scannerTypes.contains(ScannerType.AST_SAST);
    }

    public void setSastEnabled(boolean sastEnabled) {
        if (sastEnabled) {
            scannerTypes.add(ScannerType.SAST);
        } else {
            scannerTypes.remove(ScannerType.SAST);
        }
    }

    public String getCxOrigin() {
        return cxOrigin;
    }

    public String getCxOriginUrl() {
        return cxOriginUrl;
    }

    public void setCxOrigin(String cxOrigin) {
        this.cxOrigin = cxOrigin;
    }

    public void setCxOriginUrl(String cxOriginUrl) {
        this.cxOriginUrl = cxOriginUrl;
    }

    public boolean isDisableCertificateValidation() {
        return disableCertificateValidation;
    }

    public void setDisableCertificateValidation(boolean disableCertificateValidation) {
        this.disableCertificateValidation = disableCertificateValidation;
    }

    public boolean isUseSSOLogin() {
        return useSSOLogin;
    }

    public void setUseSSOLogin(boolean useSSOLogin) {
        this.useSSOLogin = useSSOLogin;
    }

    public Boolean getAvoidDuplicateProjectScans() {
        return avoidDuplicateProjectScans;
    }
    public boolean isEnableDataRetention() {
        return enableDataRetention;
    }

    public void setEnableDataRetention(boolean enableDataRetention) {
        this.enableDataRetention = enableDataRetention;
    }

    public String getSourceDir() {
        return sourceDir;
    }

    public void setSourceDir(String sourceDir) {
        this.sourceDir = sourceDir;
    }

    public String getCustomFields() {
		return customFields;
	}

	public void setCustomFields(String customFields) {
		this.customFields = customFields;
	}
	
	public String getProjectLevelCustomFields() {
        return projectLevelCustomFields;
    }
	
    public void setProjectLevelCustomFields(String projectLevelCustomFields) {
        this.projectLevelCustomFields = projectLevelCustomFields;
    }

	public String getOsaLocationPath() {
        return osaLocationPath;
    }

    public void setOsaLocationPath(String osaLocationPath) {
        this.osaLocationPath = osaLocationPath;
    }

    public String getEffectiveSourceDirForDependencyScan() {
        return osaLocationPath != null ? osaLocationPath : sourceDir;
    }

    public File getReportsDir() {
        return reportsDir;
    }

    public void setReportsDir(File reportsDir) {
        this.reportsDir = reportsDir;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setRefreshToken(String token) {
        this.refreshToken = token;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public String getScaJsonReport() {
        return scaJsonReport;
    }

    public void setScaJsonReport(String scaJsonReport) {
        this.scaJsonReport = scaJsonReport;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getTeamPath() {
        return teamPath;
    }

    public void setTeamPath(String teamPath) {
        //Make teampath always in the form /CxServer/Team1. User might have used '\' in the path.
        if (!StringUtils.isEmpty(teamPath) && !teamPath.startsWith("\\") && !teamPath.startsWith(("/"))) {
            teamPath = "/" + teamPath;
        }
        if (!StringUtils.isEmpty(teamPath) && teamPath != null) {
            teamPath = teamPath.replace("\\", "/");
        }
        this.teamPath = teamPath;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public Boolean getDenyProject() {
        return denyProject;
    }

    public void setDenyProject(Boolean denyProject) {
        this.denyProject = denyProject;
    }

    public Boolean getPublic() {
        return isPublic;
    }

    public void setPublic(Boolean aPublic) {
        isPublic = aPublic;
    }

    public Boolean getForceScan() {
        return forceScan;
    }

    public void setForceScan(Boolean forceScan) {
        this.forceScan = forceScan;
    }

    public String getPresetName() {
        return presetName;
    }

    public void setPresetName(String presetName) {
        this.presetName = presetName;
    }

    public Integer getPresetId() {
        return presetId;
    }

    public void setPresetId(Integer presetId) {
        this.presetId = presetId;
    }

    public String getSastFolderExclusions() {
        return sastFolderExclusions;
    }
    
    public String getPostScanName() {
		return postScanName;
	}
    public void setPostScanName(String postScanName) {
		this.postScanName = postScanName;
	}

    public void setSastFolderExclusions(String sastFolderExclusions) {
        this.sastFolderExclusions = sastFolderExclusions;
    }
    public Integer getProjectRetentionRate() {
        return projectRetentionRate;
    }

    public void setProjectRetentionRate(Integer projectRetentionRate) {
        this.projectRetentionRate = projectRetentionRate;
    }

    public String getSastFilterPattern() {
        return sastFilterPattern;
    }

    public void setSastFilterPattern(String sastFilterPattern) {
        this.sastFilterPattern = sastFilterPattern;
    }

    public Integer getSastScanTimeoutInMinutes() {
        return sastScanTimeoutInMinutes == null ? -1 : sastScanTimeoutInMinutes;
    }
    
    public Integer getSCAScanTimeoutInMinutes() {
        return scaScanTimeoutInMinutes == null ? -1 : scaScanTimeoutInMinutes;
    }
    
    public void setSCAScanTimeoutInMinutes(Integer scaScanTimeoutInMinutes) {
        this.scaScanTimeoutInMinutes = scaScanTimeoutInMinutes;
    }


    public void setSastScanTimeoutInMinutes(Integer sastScanTimeoutInMinutes) {
        this.sastScanTimeoutInMinutes = sastScanTimeoutInMinutes;
    }

    public Integer getOsaScanTimeoutInMinutes() {
        return osaScanTimeoutInMinutes == null ? -1 : osaScanTimeoutInMinutes;
    }

    public void setOsaScanTimeoutInMinutes(Integer sastOsaScanTimeoutInMinutes) {
        this.osaScanTimeoutInMinutes = sastOsaScanTimeoutInMinutes;
    }

    public String getScanComment() {
        return scanComment;
    }

    public void setScanComment(String scanComment) {
        this.scanComment = scanComment;
    }

    public Boolean getIncremental() {
        return isIncremental;
    }

    public void setIncremental(Boolean incremental) {
        this.isIncremental = incremental;
    }

    public Boolean getSynchronous() {
        return isSynchronous;
    }

    public void setSynchronous(Boolean synchronous) {
        this.isSynchronous = synchronous;
    }
    
    public Boolean getSastEnableCriticalSeverity() {
		return sastEnableCriticalSeverity;
	}

	public void setSastEnableCriticalSeverity(Boolean sastEnableCriticalSeverity) {
		this.sastEnableCriticalSeverity = sastEnableCriticalSeverity;
	}


    public Boolean getSastThresholdsEnabled() {
        return sastThresholdsEnabled;
    }

    public void setSastThresholdsEnabled(Boolean sastThresholdsEnabled) {
        this.sastThresholdsEnabled = sastThresholdsEnabled;
    }
    
    public Integer getSastCriticalThreshold() {
        return sastCriticalThreshold;
    }

    public void setSastCriticalThreshold(Integer sastCriticalThreshold) {
        this.sastCriticalThreshold = sastCriticalThreshold;
    }

    public Integer getSastHighThreshold() {
        return sastHighThreshold;
    }

    public void setSastHighThreshold(Integer sastHighThreshold) {
        this.sastHighThreshold = sastHighThreshold;
    }

    public Integer getSastMediumThreshold() {
        return sastMediumThreshold;
    }

    public void setSastMediumThreshold(Integer sastMediumThreshold) {
        this.sastMediumThreshold = sastMediumThreshold;
    }

    public Integer getSastLowThreshold() {
        return sastLowThreshold;
    }

    public void setSastLowThreshold(Integer sastLowThreshold) {
        this.sastLowThreshold = sastLowThreshold;
    }

    public String getSastNewResultsThresholdSeverity() {
        return sastNewResultsThresholdSeverity;
    }

    public void setSastNewResultsThresholdSeverity(String sastNewResultsThresholdSeverity) {
        this.sastNewResultsThresholdSeverity = sastNewResultsThresholdSeverity;
    }

    public Boolean getSastNewResultsThresholdEnabled() {
        return sastNewResultsThresholdEnabled;
    }

    public void setSastNewResultsThresholdEnabled(Boolean sastNewResultsThresholdEnabled) {
        this.sastNewResultsThresholdEnabled = sastNewResultsThresholdEnabled;
    }

    public Boolean getGeneratePDFReport() {
        return generatePDFReport;
    }

    public void setGeneratePDFReport(Boolean generatePDFReport) {
        this.generatePDFReport = generatePDFReport;
    }

    public File getZipFile() {
        return zipFile;
    }

    public void setZipFile(File zipFile) {
        this.zipFile = zipFile;
    }

    public Integer getEngineConfigurationId() {
        return engineConfigurationId;
    }

    public void setEngineConfigurationId(Integer engineConfigurationId) {
        this.engineConfigurationId = engineConfigurationId;
    }

    public String getOsaFilterPattern() {
        return osaFilterPattern;
    }

    public String getOsaFolderExclusions() {
        return osaFolderExclusions;
    }

    public void setOsaFolderExclusions(String osaFolderExclusions) {
        this.osaFolderExclusions = osaFolderExclusions;
    }

    public void setOsaFilterPattern(String osaFilterPattern) {
        this.osaFilterPattern = osaFilterPattern;
    }

    public String getOsaArchiveIncludePatterns() {
        return osaArchiveIncludePatterns;
    }

    public void setOsaArchiveIncludePatterns(String osaArchiveIncludePatterns) {
        this.osaArchiveIncludePatterns = osaArchiveIncludePatterns;
    }

    public Boolean getOsaRunInstall() {
        return osaRunInstall;
    }

    public void setOsaRunInstall(Boolean osaRunInstall) {
        this.osaRunInstall = osaRunInstall;
    }

    public Boolean getOsaThresholdsEnabled() {
        return osaThresholdsEnabled;
    }

    public void setOsaThresholdsEnabled(Boolean osaThresholdsEnabled) {
        this.osaThresholdsEnabled = osaThresholdsEnabled;
    }

    public Boolean isOsaFailOnError() {
        return osaFailOnError;
    }

    public void setOsaFailOnError(Boolean osaFailOnError) {
        this.osaFailOnError = osaFailOnError;
    }
    
    public Integer getOsaCriticalThreshold() {
		return osaCriticalThreshold;
	}

	public void setOsaCriticalThreshold(Integer osaCriticalThreshold) {
		this.osaCriticalThreshold = osaCriticalThreshold;
	}

    public Integer getOsaHighThreshold() {
        return osaHighThreshold;
    }

    public void setOsaHighThreshold(Integer osaHighThreshold) {
        this.osaHighThreshold = osaHighThreshold;
    }

    public Integer getOsaMediumThreshold() {
        return osaMediumThreshold;
    }

    public void setOsaMediumThreshold(Integer osaMediumThreshold) {
        this.osaMediumThreshold = osaMediumThreshold;
    }

    public Integer getOsaLowThreshold() {
        return osaLowThreshold;
    }

    public void setOsaLowThreshold(Integer osaLowThreshold) {
        this.osaLowThreshold = osaLowThreshold;
    }

    public Properties getOsaFsaConfig() {
        return osaFsaConfig;
    }

    public void setOsaFsaConfig(Properties osaFsaConfig) {
        this.osaFsaConfig = osaFsaConfig;
    }

    public String getOsaDependenciesJson() {
        return osaDependenciesJson;
    }

    public boolean isSASTThresholdEffectivelyEnabled() {

        return isSastEnabled() && getSastThresholdsEnabled() && (getSastCriticalThreshold() != null || getSastHighThreshold() != null || getSastMediumThreshold() != null || getSastLowThreshold() != null );

    }

    public boolean isOSAThresholdEffectivelyEnabled() {
        return (isOsaEnabled() || isAstScaEnabled()) &&
                getOsaThresholdsEnabled() &&
                (getOsaCriticalThreshold() != null || getOsaHighThreshold() != null || getOsaMediumThreshold() != null || getOsaLowThreshold() != null);
    }

    public void setOsaDependenciesJson(String osaDependenciesJson) {
        this.osaDependenciesJson = osaDependenciesJson;
    }

    public Boolean getOsaGenerateJsonReport() {
        return osaGenerateJsonReport;
    }

    public void setOsaGenerateJsonReport(Boolean osaGenerateJsonReport) {
        this.osaGenerateJsonReport = osaGenerateJsonReport;
    }

    public boolean getEnablePolicyViolations() {
        return enablePolicyViolations;
    }

    public boolean getEnablePolicyViolationsSCA() {
        return enablePolicyViolationsSCA;
    }

    public void setEnablePolicyViolations(boolean enablePolicyViolations) {
        this.enablePolicyViolations = enablePolicyViolations;
    }

    public void setEnablePolicyViolationsSCA(boolean enablePolicyViolationsSCA) {
        this.enablePolicyViolationsSCA = enablePolicyViolationsSCA;
    }

    public boolean isEnablePolicyViolations() {
        return enablePolicyViolations;
    }

    public boolean isEnablePolicyViolationsSCA() {
        return enablePolicyViolationsSCA;
    }

    public Boolean isSASTversionCompitable(){

        if(Float.parseFloat(cxVersion.getVersion())>=9.6){
            return false;
        }
        return true;
    }

    public String getCxARMUrl() {
        return cxARMUrl;
    }

    public void setCxARMUrl(String cxARMUrl) {
        this.cxARMUrl = cxARMUrl;
    }

    public Boolean getHideResults() {
        return hideResults;
    }

    public void setHideResults(Boolean hideResults) {
        this.hideResults = hideResults;
    }

    public Boolean getContinueBuild() {
        return continueBuild;
    }

    public void setContinueBuild(Boolean continueBuild) {
        this.continueBuild = continueBuild;
    }
    

    public Boolean isAvoidDuplicateProjectScans() {
        return avoidDuplicateProjectScans;
    }

    public void setAvoidDuplicateProjectScans(Boolean avoidDuplicateProjectScans) {
        this.avoidDuplicateProjectScans = avoidDuplicateProjectScans;
    }

    public String getRemoteSrcUser() {
        return remoteSrcUser;
    }

    public void setRemoteSrcUser(String remoteSrcUser) {
        this.remoteSrcUser = remoteSrcUser;
    }

    public String getRemoteSrcPass() {
        return remoteSrcPass;
    }

    public void setRemoteSrcPass(String remoteSrcPass) {
        this.remoteSrcPass = remoteSrcPass;
    }

    public String getRemoteSrcUrl() {
        return remoteSrcUrl;
    }

    public void setRemoteSrcUrl(String remoteSrcUrl) {
        this.remoteSrcUrl = remoteSrcUrl;
    }

    public int getRemoteSrcPort() {
        return remoteSrcPort;
    }

    public void setRemoteSrcPort(int remoteSrcPort) {
        this.remoteSrcPort = remoteSrcPort;
    }

    public byte[] getRemoteSrcKeyFile() {
        return remoteSrcKeyFile;
    }

    public void setRemoteSrcKeyFile(byte[] remoteSrcKeyFile) {
        this.remoteSrcKeyFile = remoteSrcKeyFile;
    }

    public RemoteSourceTypes getRemoteType() {
        return remoteType;
    }

    public void setRemoteType(RemoteSourceTypes remoteType) {
        this.remoteType = remoteType;
    }

    public String[] getPaths() {
        return paths;
    }

    public void setPaths(String[] paths) {
        this.paths = paths;
    }

    public String getRemoteSrcBranch() {
        return remoteSrcBranch;
    }

    public void setRemoteSrcBranch(String remoteSrcBranch) {
        this.remoteSrcBranch = remoteSrcBranch;
    }

    public String getPerforceMode() {
        return perforceMode;
    }

    public void setPerforceMode(String perforceMode) {
        this.perforceMode = perforceMode;
    }

    public Boolean getGenerateXmlReport() {
        return generateXmlReport;
    }

    public void setGenerateXmlReport(Boolean generateXmlReport) {
        this.generateXmlReport = generateXmlReport;
    }

    public CxVersion getCxVersion() {
        return cxVersion;
    }

    public void setCxVersion(CxVersion cxVersion) {
        this.cxVersion = cxVersion;
    }

    public Integer getProgressInterval() {
        return progressInterval;
    }

    public void setProgressInterval(Integer progressInterval) {
        this.progressInterval = progressInterval;
    }

    public Integer getOsaProgressInterval() {
        return osaProgressInterval;
    }

    public void setOsaProgressInterval(Integer osaProgressInterval) {
        this.osaProgressInterval = osaProgressInterval;
    }

    public Integer getConnectionRetries() {
        return connectionRetries;
    }

    public void setConnectionRetries(Integer connectionRetries) {
        this.connectionRetries = connectionRetries;
    }

    public String getMvnPath() {
        return mvnPath;
    }

    public void setMvnPath(String mvnPath) {
        this.mvnPath = mvnPath;
    }

    public String getOsaScanDepth() {
        return osaScanDepth;
    }

    public void setOsaScanDepth(String osaScanDepth) {
        this.osaScanDepth = osaScanDepth;
    }

    public Integer getMaxZipSize() {
        return maxZipSize;
    }

    public void setMaxZipSize(Integer maxZipSize) {
        this.maxZipSize = maxZipSize;
    }

    public String getDefaultProjectName() {
        return defaultProjectName;
    }

    public void setDefaultProjectName(String defaultProjectName) {
        this.defaultProjectName = defaultProjectName;
    }
   
    public Map<ReportType, String> getReports() {
        return reports;
    }

    public void addPDFReport(String pdfReportPath) {
        reports.put(ReportType.PDF, pdfReportPath);
    }

    public void addXMLReport(String xmlReportPath) {
        reports.put(ReportType.XML, xmlReportPath);
    }

    public void addCSVReport(String csvReportPath) {
        reports.put(ReportType.CSV, csvReportPath);
    }

    public void addRTFReport(String rtfReportPath) {
        reports.put(ReportType.RTF, rtfReportPath);
    }

    public AstScaConfig getAstScaConfig() {
        return astScaConfig;
    }

    public void setAstScaConfig(AstScaConfig astScaConfig) {
        this.astScaConfig = astScaConfig;
    }

    public AstSastConfig getAstSastConfig() {
        return astSastConfig;
    }

    public void setAstSastConfig(AstSastConfig astConfig) {
        this.astSastConfig = astConfig;
    }

    public Set<ScannerType> getScannerTypes() {
        return scannerTypes;
    }

    public void addScannerType(ScannerType scannerType) {
        this.scannerTypes.add(scannerType);
    }

    /**
     * SAST and OSA are currently deployed on-premises, whereas AST-SCA is deployed in a cloud.
     * If SAST or OSA are enabled, some of the config properties are mandatory (url, username, password etc).
     * Otherwise, these properties are optional.
     */
    public boolean isSastOrOSAEnabled() {
        return isSastEnabled() || isOsaEnabled();
    }
    
    public Boolean isProxy() {
        return isProxy;
    }

    public void setProxy(Boolean proxy) {
        isProxy = proxy;
    }

    public Boolean isScaProxy() {return isScaProxy;}

    public void setScaProxy(Boolean scaProxy) {isScaProxy = scaProxy;}

    public ProxyConfig getProxyConfig() {
        return proxyConfig;
    }

    public void setProxyConfig(ProxyConfig proxyConfig) {
        this.proxyConfig = proxyConfig;
    }

    public ProxyConfig getScaProxyConfig() {
        return proxyScaConfig;
    }

    public void setScaProxyConfig(ProxyConfig proxyScaConfig) {
        this.proxyScaConfig = proxyScaConfig;
    }

    public void addCookie(Cookie cookie) {
        this.sessionCookies.add(cookie);
    }

    public List<Cookie> getSessionCookie() {
        return this.sessionCookies;
    }

    public TokenLoginResponse getToken() {
        return token;
    }

    public void setToken(TokenLoginResponse token) {
        this.token = token;
    }

    public Boolean getNTLM() {
        return useNTLM;
    }

    public void setNTLM(Boolean ntlm) {
        useNTLM = ntlm;
    }

	public boolean getIsOverrideProjectSetting() {
		return isOverrideProjectSetting;
	}

	public void setIsOverrideProjectSetting(boolean isOverrideProjectSetting) {
		this.isOverrideProjectSetting = isOverrideProjectSetting;
	}

	public Integer getPostScanActionId() {
		return postScanActionId;
	}

	public void setPostScanActionId(Integer postScanActionId) {
		this.postScanActionId = postScanActionId;
	}

	public final boolean isIgnoreBenignErrors() {
		return ignoreBenignErrors;
	}

	public final void setIgnoreBenignErrors(boolean ignoreBenignErrors) {
		this.ignoreBenignErrors = ignoreBenignErrors;
	}
	
	public Boolean isEnableSASTBranching() {
		return enableSastBranching;
	}

	public void setEnableSASTBranching(Boolean enableSASTBranching) {
		this.enableSastBranching = enableSASTBranching;
	}

	public String getMasterBranchProjName() {
		return masterBranchProjName;
	}

	public void setMasterBranchProjName(String masterBranchProjName) {
		this.masterBranchProjName = masterBranchProjName;
	}
	
	public boolean isGenerateScaReport() {
		return generateScaReport;
	}

	public void setGenerateScaReport(boolean generateScaReport) {
		this.generateScaReport = generateScaReport;
	}

	public String getScaReportFormat() {
		return scaReportFormat;
	}

	public void setScaReportFormat(String scaReportFormat) {
		this.scaReportFormat = scaReportFormat;
	}

    public Integer getcopyBranchTimeOutInSeconds() {
        return copyBranchTimeOutInSeconds;
    }

    public void setcopyBranchTimeOutInSeconds(Integer copyBranchTimeOutInSeconds) {
        this.copyBranchTimeOutInSeconds = copyBranchTimeOutInSeconds;
    }
	
}
