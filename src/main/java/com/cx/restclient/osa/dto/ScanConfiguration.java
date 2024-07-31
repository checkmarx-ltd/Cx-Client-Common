package com.cx.restclient.osa.dto;


import com.cx.restclient.sast.dto.Project;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.File;

/**
 * Created by galn on 21/12/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ScanConfiguration {

    private boolean SASTEnabled;
    private String cxOrigin;
    private String sourceDir;
    private String tempDir;
    private String reportsDir;
    private String username;
    private String password;
    private String url;
    private String projectName;
    private String presetName;
    private int presetId;
    private String fullTeamPath;
    private String teamId;
    private String folderExclusions;
    private String filterPattern;
    private Integer scanTimeoutInMinutes;
    private String scanComment;
    private boolean isIncremental = false;
    private boolean isSynchronous = false;
    private boolean thresholdsEnabled = false;
    private Integer highThreshold;
    private Integer mediumThreshold;
    private Integer lowThreshold;
    private boolean generatePDFReport = false;
    private boolean osaEnabled = false;
    private String osaFilterPattern;
    private String osaArchiveIncludePatterns;
    private boolean osaInstallBeforeScan;
    private boolean osaThresholdsEnabled = false;
    private Integer osaHighThreshold;
    private Integer osaMediumThreshold;
    private Integer osaLowThreshold;
    private boolean denyProject = false;
    private boolean isPublic = false;
    private boolean forceScan = false;
    private File zipFile;
    private Integer engineConfigurationId;
    private String osaDependenciesJson;
    private Project project;


    public boolean isSASTEnabled() {
        return SASTEnabled;
    }

    public void setSASTEnabled(boolean SASTEnabled) {
        this.SASTEnabled = SASTEnabled;
    }

    public String getCxOrigin() {
        return cxOrigin;
    }

    public void setCxOrigin(String cxOrigin) {
        this.cxOrigin = cxOrigin;
    }

    public String getSourceDir() {
        return sourceDir;
    }

    public void setSourceDir(String sourceDir) {
        this.sourceDir = sourceDir;
    }

    public String getTempDir() {
        return tempDir;
    }

    public void setTempDir(String tempDir) {
        this.tempDir = tempDir;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public void setUrl(String url) {
        this.url = url;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public int getPresetId() {
        return presetId;
    }

    public void setPresetId(int presetId) {
        this.presetId = presetId;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public String getPresetName() {
        return presetName;
    }

    public void setPresetName(String presetName) {
        this.presetName = presetName;
    }

    public String getFullTeamPath() {
        return fullTeamPath;
    }

    public void setFullTeamPath(String fullTeamPath) {
        this.fullTeamPath = fullTeamPath;
    }

    public Integer getScanTimeoutInMinutes() {
        if (scanTimeoutInMinutes == null) {
            scanTimeoutInMinutes = -1;
        }
        return scanTimeoutInMinutes;
    }

    public void setScanTimeoutInMinutes(Integer scanTimeoutInMinutes) {
        this.scanTimeoutInMinutes = scanTimeoutInMinutes;
    }

    public String getScanComment() {
        return scanComment;
    }

    public void setScanComment(String scanComment) {
        this.scanComment = scanComment;
    }

    public boolean isIncremental() {
        return isIncremental;
    }

    public void setIncremental(boolean incremental) {
        isIncremental = incremental;
    }

    public boolean isSynchronous() {
        return isSynchronous;
    }

    public void setSynchronous(boolean synchronous) {
        isSynchronous = synchronous;
    }

    public boolean isThresholdsEnabled() {
        return thresholdsEnabled;
    }

    public void setThresholdsEnabled(boolean thresholdsEnabled) {
        this.thresholdsEnabled = thresholdsEnabled;
    }

    public Integer getHighThreshold() {
        return highThreshold;
    }

    public void setHighThreshold(Integer highThreshold) {
        this.highThreshold = highThreshold;
    }

    private void setHighThreshold(String highSeveritiesThreshold) {
        this.highThreshold = getAsInteger(highSeveritiesThreshold);
    }

    public Integer getMediumThreshold() {
        return mediumThreshold;
    }

    public void setMediumThreshold(Integer mediumThreshold) {
        this.mediumThreshold = mediumThreshold;
    }

    private void setMediumThreshold(String mediumSeveritiesThreshold) {
        this.mediumThreshold = getAsInteger(mediumSeveritiesThreshold);
    }

    public Integer getLowThreshold() {
        return lowThreshold;
    }

    public void setLowThreshold(Integer lowThreshold) {
        this.lowThreshold = lowThreshold;
    }

    private void setLowThreshold(String lowSeveritiesThreshold) {
        this.lowThreshold = getAsInteger(lowSeveritiesThreshold);
    }

    public String getFolderExclusions() {
        return folderExclusions;
    }

    public void setFolderExclusions(String folderExclusions) {
        this.folderExclusions = folderExclusions;
    }

    public String getFilterPattern() {
        return filterPattern;
    }

    public void setFilterPattern(String filterPattern) {
        this.filterPattern = filterPattern;
    }

    public boolean isGeneratePDFReport() {
        return generatePDFReport;
    }

    public void setGeneratePDFReport(boolean generatePDFReport) {
        this.generatePDFReport = generatePDFReport;
    }

    public boolean isOsaEnabled() {
        return osaEnabled;
    }

    public void setOsaEnabled(boolean osaEnabled) {
        this.osaEnabled = osaEnabled;
    }

    public String getOsaFilterPattern() {
        return osaFilterPattern;
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

    public boolean isOsaInstallBeforeScan() {
        return osaInstallBeforeScan;
    }

    public void setOsaInstallBeforeScan(boolean osaInstallBeforeScan) {
        this.osaInstallBeforeScan = osaInstallBeforeScan;
    }

    public boolean isOsaThresholdsEnabled() {
        return osaThresholdsEnabled;
    }

    public void setOsaThresholdsEnabled(boolean osaThresholdsEnabled) {
        this.osaThresholdsEnabled = osaThresholdsEnabled;
    }

    public Integer getOsaHighThreshold() {
        return osaHighThreshold;
    }

    public void setOsaHighThreshold(Integer osaHighThreshold) {
        this.osaHighThreshold = osaHighThreshold;
    }

    private void setOsaHighSeveritiesThreshold(String osaHighSeveritiesThreshold) {
        this.osaHighThreshold = getAsInteger(osaHighSeveritiesThreshold);
    }

    public Integer getOsaMediumThreshold() {
        return osaMediumThreshold;
    }

    public void setOsaMediumThreshold(Integer osaMediumThreshold) {
        this.osaMediumThreshold = osaMediumThreshold;
    }

    private void setOsaMediumSeveritiesThreshold(String osaMediumSeveritiesThreshold) {
        this.osaMediumThreshold = getAsInteger(osaMediumSeveritiesThreshold);
    }

    public Integer getOsaLowThreshold() {
        return osaLowThreshold;
    }

    public void setOsaLowThreshold(Integer osaLowThreshold) {
        this.osaLowThreshold = osaLowThreshold;
    }

    private void setOsaLowSeveritiesThreshold(String osaLowSeveritiesThreshold) {
        this.osaLowThreshold = getAsInteger(osaLowSeveritiesThreshold);
    }

    private Integer getAsInteger(String number) {
        Integer inti = null;
        try {
            if (number != null && number.length() > 0) {
                inti = Integer.parseInt(number);
            }
        } catch (NumberFormatException e) {
            inti = null;
        }
        return inti;
    }

    public boolean isSASTThresholdEffectivelyEnabled() {
        return isThresholdsEnabled() && (getLowThreshold() != null || getMediumThreshold() != null || getHighThreshold() != null);
    }

    public boolean isOSAThresholdEffectivelyEnabled() {
        return isOsaEnabled() && isOsaThresholdsEnabled() && (getOsaHighThreshold() != null || getOsaMediumThreshold() != null || getOsaLowThreshold() != null);
    }

    public String getReportsDir() {
        return reportsDir;
    }

    public void setReportsDir(String reportsDir) {
        this.reportsDir = reportsDir;
    }

    public boolean isDenyProject() {
        return denyProject;
    }

    public void setDenyProject(boolean denyProject) {
        this.denyProject = denyProject;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
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

    public boolean isForceScan() {
        return forceScan;
    }

    public void setForceScan(boolean forceScan) {
        this.forceScan = forceScan;
    }

    public String getOsaDependenciesJson() {
        return osaDependenciesJson;
    }

    public void setOsaDependenciesJson(String osaDependenciesJson) {
        this.osaDependenciesJson = osaDependenciesJson;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }
}
