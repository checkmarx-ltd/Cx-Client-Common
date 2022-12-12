package com.cx.restclient.sast.utils;

public class MigrationYaml {
	private String regex;
	private String isMigrate;

	public MigrationYaml() {
		super();
		// TODO Auto-generated constructor stub
	}

	public MigrationYaml(String regex, String isMigrate) {
		super();
		this.regex = regex;
		this.isMigrate = isMigrate;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}

	public String getIsMigrate() {
		return isMigrate;
	}

	public void setIsMigrate(String isMigrate) {
		this.isMigrate = isMigrate;
	}

}
