package com.cx.restclient.ast.dto.sca;

import java.util.ArrayList;
import java.util.List;

public class CreateProjectRequest {
    private String name;
    private List<String> assignedTeams = new ArrayList<>();

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
