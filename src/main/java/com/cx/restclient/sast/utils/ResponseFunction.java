package com.cx.restclient.sast.utils;

public class ResponseFunction {
	private String func;
	private String regex;
	private Boolean isMigrate;
	
	public ResponseFunction() {
		super();
		// TODO Auto-generated constructor stub
	}
	public ResponseFunction(String func, String regex, Boolean isMigrate) {
		super();
		this.func = func;
		this.regex = regex;
		this.isMigrate = isMigrate;
	}
	public String getFunc() {
		return func;
	}
	public void setFunc(String func) {
		this.func = func;
	}
	public String getRegex() {
		return regex;
	}
	public void setRegex(String regex) {
		this.regex = regex;
	}
	public Boolean getIsMigrate() {
		return isMigrate;
	}
	public void setIsMigrate(Boolean isMigrate) {
		this.isMigrate = isMigrate;
	}
	
	
	
}
