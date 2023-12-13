package com.cx.restclient.ast.dto.sca;

import java.util.ArrayList;
import java.util.List;

public class UpdateProjectRequest {
    private String name;

    public void setAssignedTeams(List<String> assignedTeams) {
        this.assignedTeams = assignedTeams;
    }

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
