package com.cx.restclient.dto;

import java.io.Serializable;

import com.cx.restclient.exception.CxClientException;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Results implements Serializable{
    private CxClientException exception;
    
    public String encodeXSS(String injection) {
		 String lt="<";
		 String gt=">";
		 String ap="\'";
		 String ic="\"";
		 System.out.println( "  TEST FOR NPE "+ injection);
		 injection=injection.replace(lt, "&lt;").replace(gt, "&gt;").replace(ap, "&#39;").replace(ic,"&#34;");
		 return injection;
	 }
}
