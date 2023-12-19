package com.cx.restclient.common;

import java.io.File;

/**
 * Created by Galn on 14/02/2018.
 */
public abstract class CxPARAM {
    public static final String AUTHENTICATION = "identity/connect/token";
    public static final String REVOCATION = "auth/identity/connect/revocation";
    public static final String SSO_AUTHENTICATION = "auth/identity/externalLogin";
    public static final String CXPRESETS = "sast/presets";
    public static final String CXTEAMS = "auth/teams";
    public static final String CREATE_PROJECT = "projects";//Create new project (default preset and configuration)
    public static final String CX_VERSION = "system/version";

    public static final String CX_ARM_URL = "/Configurations/Portal";
    public static final String CX_ARM_VIOLATION = "/cxarm/policymanager/projects/{projectId}/violations?provider={provider}";

    public static final String BROWSER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36";



    public static final String CX_REPORT_LOCATION = File.separator + "Checkmarx" + File.separator + "Reports";

    public static final String ORIGIN_HEADER = "cxOrigin";
    public static final String ORIGIN_URL_HEADER = "cxOriginUrl";
    public static final String CSRF_TOKEN_HEADER = "CXCSRFToken";
    public static final String PROJECT_POLICY_VIOLATED_STATUS_SAST = "Project policy status for SAST and OSA : violated";

    public static final String PROJECT_POLICY_VIOLATED_STATUS ="Project policy status : violated";
    public static final String PROJECT_POLICY_VIOLATED_STATUS_SCA = "Project policy status for SCA : violated";
    public static final String PROJECT_POLICY_COMPLIANT_STATUS_SAST = "Project policy status for SAST and OSA : compliant";
    public static final String PROJECT_POLICY_COMPLIANT_STATUS_SCA = "Project policy status for SCA : compliant";
    public static final String DENY_NEW_PROJECT_ERROR = "Creation of the new project [{projectName}] is not authorized. " +
            "Please use an existing project. \nYou can enable the creation of new projects by disabling" + "" +
            " the Deny new Checkmarx projects creation checkbox in the Checkmarx plugin global settings.\n";
    public static final String TEAM_PATH = "cxTeamPath";

}
