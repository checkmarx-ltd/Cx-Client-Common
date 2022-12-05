package com.cx.restclient.ast.dto.sast;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collections;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.checkmarx.one.dto.scan.ResultsResponse;
import com.cx.restclient.exception.CxClientException;
import com.sun.xml.bind.v2.ContextFactory;

public class AstSastUtils {
	
	 public static ResultsResponse convertToXMLResult(byte[] cxReport) throws CxClientException {
		 ResultsResponse reportObj = null;
	        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(cxReport)) {

	              JAXBContext jaxbContext = ContextFactory.createContext(ResultsResponse.class.getPackage().getName(),                    
	            		  ResultsResponse.class.getClassLoader(), Collections.<String, Object>emptyMap());
	            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

	            reportObj = (ResultsResponse) unmarshaller.unmarshal(byteArrayInputStream);

	        } catch (JAXBException | IOException e) {
	            throw new CxClientException("Failed to parse xml report: " + e.getMessage(), e);
	        }
	        return reportObj;
	    }

}
