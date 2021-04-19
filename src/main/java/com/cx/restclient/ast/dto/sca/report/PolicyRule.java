package com.cx.restclient.ast.dto.sca.report;

import java.io.Serializable;

public class PolicyRule implements Serializable {
	private String id;
	private boolean isViolated;
	private String name;
	
	public final String getId() {
		return id;
	}
	public final void setId(String id) {
		this.id = id;
	}
	public final boolean getIsViolated() {
		return isViolated;
	}
	public final void setIsViolated(boolean isViolated) {
		this.isViolated = isViolated;
	}
	public final String getName() {
		return name;
	}
	public final void setName(String name) {
		this.name = name;
	}
	
	
}
