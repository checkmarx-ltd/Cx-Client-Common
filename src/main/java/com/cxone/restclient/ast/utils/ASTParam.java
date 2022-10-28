package com.cxone.restclient.ast.utils;

public class ASTParam {
	 //REST PATH
    public static final String AST_ENGINE_CONFIG = "ast/engineConfigurations";
    public static final String AST_UPDATE_SCAN_SETTINGS = "ast/pluginsScanSettings"; //Update preset and configuration
    public static final String AST_GET_SCAN_SETTINGS = "/ast/scanSettings/{projectId}"; //Update preset and configuration
    public static final String AST_CREATE_SCAN = "ast/scans"; //Run a new Scan
    public static final String AST_SCAN_STATUS = "ast/scans/{scanId}"; //Get Scan status (by scan ID)
    public static final String AST_QUEUE_SCAN_STATUS = "ast/scansQueue/{scanId}";
    public static final String AST_GET_PROJECT_BY_ID = "projects/{projectId}";
    public static final String AST_GET_PROJECT = "projects?projectname={name}&teamid={teamId}";// Get  project)
    public static final String AST_GET_ALL_PROJECTS = "projects";// Get  project)
    public static final String AST_ZIP_ATTACHMENTS = "projects/{projectId}/sourceCode/attachments";//Attach ZIP file
    public static final String AST_GET_PROJECT_SCANS = "ast/scans?projectId={projectId}";
    public static final String AST_GET_QUEUED_SCANS = "ast/scansQueue?projectId={projectId}";
    public static final String AST_CUSTOM_TASKS = "customTasks";

    public static final String AST_CREATE_REMOTE_SOURCE_SCAN = "projects/%s/sourceCode/remoteSettings/%s/%s";
    public static final String AST_EXCLUDE_FOLDERS_FILES_PATTERNS = "projects/%s/sourceCode/excludeSettings";



    //Once it has results
    public static final String AST_SCAN_RESULTS_STATISTICS = "ast/scans/{scanId}/resultsStatistics";
    public static final String AST_CREATE_REPORT = "reports/astScan/"; //Create new report (get ID)
    public static final String AST_GET_REPORT_STATUS = "reports/astScan/{reportId}/status"; //Get report status
    public static final String AST_GET_REPORT = "reports/astScan/{reportId}"; //Get report status
    public static final String AST_GET_CXARM_STATUS = "ast/projects/{projectId}/publisher/policyFindings/status"; //Get report status


    //ZIP PARAMS
    public static final long MAX_ZIP_SIZE_BYTES = 2147483648L;
    public static final String TEMP_FILE_NAME_TO_ZIP = "zippedSource";
    public static final String TEMP_FILE_NAME_TO_SCA_RESOLVER_RESULTS_ZIP = "ScaResolverResults";
    public static final String SCA_RESOLVER_RESULT_FILE_NAME = ".cxonesca-results.json";

    //Links formats need to be change
    public static final String LINK_FORMAT = "/CxWebClient/portal#/projectState/%d/Summary";
    public static final String SCAN_LINK_FORMAT = "/CxWebClient/ViewerMain.aspx?scanId=%s&ProjectID=%s";
    public static final String PROJECT_LINK_FORMAT = "/CxWebClient/portal#/projectState/%d/Summary";

    //REPORT PARAMS
    public static final String PDF_REPORT_NAME = "CxOneASTReport";

}
