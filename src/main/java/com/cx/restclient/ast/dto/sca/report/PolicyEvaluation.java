package com.cx.restclient.ast.dto.sca.report;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

//cannot use lombok because the way boolean members in the APi payload are named
//Jackson ObjectMapper looks for setter/getter with literally same.

public class PolicyEvaluation implements Serializable {
	private String id;
	private String description;
	private String name;
	private boolean isViolated;
	private PolicyAction actions;
	private List<PolicyRule> rules = new ArrayList<>();
	
	public final String getId() {
		return id;
	}
	public final void setId(String id) {
		this.id = id;
	}
	public final String getDescription() {
		return description;
	}
	public final void setDescription(String description) {
		this.description = description;
	}
	public final String getName() {
		return name;
	}
	public final void setName(String name) {
		this.name = name;
	}
	public final boolean getIsViolated() {
		return isViolated;
	}	
	
	public final void setIsViolated(boolean isViolated) {
		this.isViolated = isViolated;
	}
	
	public final PolicyAction getActions() {
		return actions;
	}
	public final void setActions(PolicyAction actions) {
		this.actions = actions;
	}
	public final List<PolicyRule> getRules() {
		return rules;
	}
	public final void setRules(List<PolicyRule> rules) {
		this.rules = rules;
	}


}
