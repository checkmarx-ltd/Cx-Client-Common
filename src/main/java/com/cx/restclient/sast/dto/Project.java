package com.cx.restclient.sast.dto;

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
    private List<CustomField> customFields;

    public long getId() {
        return id;
    }

    public List<CustomField> getCustomFields(){
    	return customFields;
    }
    
    public void setCustomFields(List<CustomField> customFields) {
        this.customFields = customFields;
    }
    
    public static class CustomField {
        @Override
		public String toString() {
			return "CustomField [id=" + id + ", value=" + value + ", name=" + name + "]";
		}
		private long id;
        private String value;
        private String name;
        public long getId() {
            return id;
        }
        public void setId(long id) {
 this.id = id;
        }
        public String getValue() {
            return value;
        }
        public void setValue(String value) {
            this.value = value;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
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
