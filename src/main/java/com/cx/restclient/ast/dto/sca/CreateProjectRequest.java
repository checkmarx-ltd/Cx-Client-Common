package com.cx.restclient.ast.dto.sca;

import com.cx.restclient.sca.dto.Tags;

import java.util.ArrayList;
import java.util.List;

public class CreateProjectRequest {
    private String name;
    private List<String> assignedTeams = new ArrayList<>();
    private Object Tags;

    public Object getTags() {
        return Tags;
    }

    public void setTags(Object tags) {
        this.Tags = tags;
    }

	public List<String> getAssignedTeams() {
		return assignedTeams;
	}

	public void addAssignedTeams(String assignedTeam) {
		this.assignedTeams.add(assignedTeam);
	}

	public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


}
