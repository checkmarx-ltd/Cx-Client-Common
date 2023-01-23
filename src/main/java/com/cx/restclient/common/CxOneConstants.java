package com.cx.restclient.common;

public abstract class CxOneConstants {
	
	public static final String DENY_NEW_PROJECT_ERROR = "Creation of the new project [{projectName}] is not authorized. "
			+ "Please use an existing project. \nYou can enable the creation of new projects by disabling" + ""
			+ " the Deny new Checkmarx projects creation checkbox in the Checkmarx plugin global settings.\n";
	public static final String MSG_AVOID_DUPLICATE_PROJECT_SCANS = "\nScan on this project are already active.\n";
	public static final String RUNNING_QUEUED = "running,queued";
	public static final String SAST = "sast";
	public static final String LANGUAGE_MODE = "languageMode";
	public static final String PRESET_NAME = "presetName";
	public static final String INCREMENTAL = "incremental";
	public static final String FILTER = "filter";
	
}
