package com.cx.restclient.ast;

public class UrlPaths {
    private UrlPaths() {
    }

    public static final String RISK_MANAGEMENT_API = "/risk-management/";
    public static final String PROJECTS = RISK_MANAGEMENT_API + "projects";
    public static final String SUMMARY_REPORT = RISK_MANAGEMENT_API + "riskReports/%s/summary";
    public static final String FINDINGS = RISK_MANAGEMENT_API + "riskReports/%s/vulnerabilities";
    public static final String PACKAGES = RISK_MANAGEMENT_API + "riskReports/%s/packages";
    private static final String SETTINGS_API = "/settings/";
    public static final String RESOLVING_CONFIGURATION_API = SETTINGS_API + "projects/%s/resolving-configuration";

    public static final String REPORT_ID = RISK_MANAGEMENT_API + "scans/%s/riskReportId";

    public static final String GET_UPLOAD_URL = "/api/uploads";
    public static final String CREATE_SCAN = "/api/scans";
    public static final String GET_SCAN = "/api/scans/%s";

    public static final String WEB_REPORT = "/#/projects/%s/reports/%s";
}
