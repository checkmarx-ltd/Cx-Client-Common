package com.cx.restclient.sast.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Galn on 4/11/2018.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectLevelCustomFields {
	private long id;
	   private String value;
	   private String name;
	   
	   public ProjectLevelCustomFields() {
	   }
	   
	   public ProjectLevelCustomFields(long id, String value, String name) {
			this.id = id;
			       this.value = value;
			       this.name = name;
			   }
	   
			   public long getId() {
			       return id;
			   }
			   
			   public String getValue() {
			       return value;
			   }
			   
			   public String getName() {
			       return name;
			   }
	   
}
