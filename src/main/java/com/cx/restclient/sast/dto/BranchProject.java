package com.cx.restclient.sast.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BranchProject {
    private String name;
    
	public BranchProject(String name) {
	this.name = name;
}
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}