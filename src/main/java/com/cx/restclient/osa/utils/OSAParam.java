package com.cx.restclient.osa.utils;

/**
 * Created by Galn on 06/02/2018.
 */
public class OSAParam {
    //REST PATH
    public static final String OSA_SCAN_PROJECT = "osa/inventory";
    public static final String OSA_SCAN_STATUS = "osa/scans/{scanId}";
    public static final String OSA_SCANS = "osa/scans";
    public static final String OSA_SCAN_SUMMARY = "osa/reports";
    public static final String OSA_SCAN_LIBRARIES = "/osa/libraries";
    public static final String OSA_SCAN_VULNERABILITIES = "/osa/vulnerabilities";
    public static final String SCAN_ID_QUERY_PARAM = "?scanId=";
    public static final String PROJECT_ID_QUERY_PARAM = "?projectId=";
    public static final String ITEM_PER_PAGE_QUERY_PARAM = "&itemsPerPage=";
    public static final long MAX_ITEMS = 1000000;

    //REPORT PARAMS
    public static final String OSA_LIBRARIES_NAME = "CxOSALibraries";
    public static final String OSA_VULNERABILITIES_NAME = "CxOSAVulnerabilities";
    public static final String OSA_SUMMARY_NAME = "CxOSASummary";
    public static final String LINK_FORMAT = "/CxWebClient/portal#/projectState/";
    public static final String LINK_FORMAL_SUMMARY = "/Summary";
}
