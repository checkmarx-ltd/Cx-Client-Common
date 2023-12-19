package com.cx.restclient.ast.dto.sca;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.cx.restclient.ast.dto.common.ASTConfig;

import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class AstScaConfig extends ASTConfig implements Serializable {

    private String accessControlUrl;
    private String username;
    private String password;
    private String tenant;
    private String teamPath;
    private String teamId;

    /**
     * true: upload all sources for scan
     * <br>
     * false: only upload manifest and fingerprints for scan. Useful for customers that don't want their proprietary
     * code to be uploaded into the cloud.
     */
    private boolean includeSources;
    private boolean enableScaResolver;
    
    private String fingerprintsIncludePattern;
    private String manifestsIncludePattern;
    private String fingerprintFilePath;
    private String sastProjectId;
    private String sastProjectName;
    private String sastServerUrl;
    private String sastUsername;
    private String sastPassword;
    private Boolean isScaProxy;

	private String pathToScaResolver;
    private String scaProjectCustomTags;
    private String scaScanCustomTags;
    private String scaResolverAddParameters;
	
    public String getPathToScaResolver() {
		return pathToScaResolver;
	}
	public void setPathToScaResolver(String pathToScaResolver) {
		this.pathToScaResolver = pathToScaResolver;
	}
	public String getScaResolverAddParameters() {
		return scaResolverAddParameters;
	}
	public void setScaResolverAddParameters(String scaResolverAddParameters) {
		this.scaResolverAddParameters = scaResolverAddParameters;
	}

    public String getScaProjectCustomTags() {
        return scaProjectCustomTags;
    }

    public void setScaProjectCustomTags(String scaProjectCustomTags) {
        this.scaProjectCustomTags = scaProjectCustomTags;
    }

    public String getScaScanCustomTags() {
        return scaScanCustomTags;
    }

    public void setScaScanCustomTags(String scaScanCustomTags) {
        this.scaScanCustomTags = scaScanCustomTags;
    }
	private Map<String,String> envVariables;
    private List<String> configFilePaths;
    
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
    public Boolean getScaProxy() {return isScaProxy;}

    public void isScaProxy(Boolean scaProxy) {isScaProxy = scaProxy;}
}
