package com.cx.restclient.sast.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Galn on 13/02/2018.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateProjectRequest {
    private String name;
    private String owningTeam;
    private boolean isPublic;

    public CreateProjectRequest(String name, String owningTeam, boolean isPublic) {
        this.name = name;
        this.owningTeam = owningTeam;
        this.isPublic = isPublic;
    }
    public CreateProjectRequest(String name) {
    	this.name = name;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwningTeam() {
        return owningTeam;
    }

    public void setOwningTeam(String owningTeam) {
        this.owningTeam = owningTeam;
    }

    public boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }
}
