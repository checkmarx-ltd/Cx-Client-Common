package com.cx.restclient.sast.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Galn on 13/02/2018.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Project {
    private long id;
    private String owner;
    private String name;
    private String teamId;
    private boolean isPublic;
    private List<ProjectLevelCustomFields> customFields;

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public ArrayList<ProjectLevelCustomFields> getCustomFields() {
        if (customFields instanceof ArrayList) {
            return (ArrayList<ProjectLevelCustomFields>) customFields;
        } else if (customFields != null) {
            return new ArrayList<>(customFields);
        } else {
            return new ArrayList<>();
        }
    }
    public void setCustomFields(ArrayList<ProjectLevelCustomFields> customFields) {
        this.customFields = customFields;
    }
    
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
    
}
