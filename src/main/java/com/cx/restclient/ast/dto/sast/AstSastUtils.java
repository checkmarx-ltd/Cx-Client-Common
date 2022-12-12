package com.cx.restclient.ast.dto.sast;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collections;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;

import com.checkmarx.one.dto.scan.sast.SastResultsResponse;
import com.cx.restclient.exception.CxClientException;
import com.sun.xml.bind.v2.ContextFactory;

public class AstSastUtils {
	
	 public static SastResultsResponse convertToXMLResult(byte[] cxReport) throws CxClientException {
		 SastResultsResponse reportObj = null;
	        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(cxReport)) {

	              JAXBContext jaxbContext = ContextFactory.createContext(SastResultsResponse.class.getPackage().getName(),                    
	            		  SastResultsResponse.class.getClassLoader(), Collections.<String, Object>emptyMap());
	            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

	            reportObj = (SastResultsResponse) unmarshaller.unmarshal(byteArrayInputStream);

	        } catch (JAXBException | IOException e) {
	            throw new CxClientException("Failed to parse xml report: " + e.getMessage(), e);
	        }
	        return reportObj;
	    }
	 public static void printAstSASTResultsToConsole(AstSastResults astSastResults, boolean enableViolations, Logger log) {

	        String highNew = astSastResults.getNewHigh() > 0 ? " (" + astSastResults.getNewHigh() + " new)" : "";
	        String mediumNew = astSastResults.getNewMedium() > 0 ? " (" + astSastResults.getNewMedium() + " new)" : "";
	        String lowNew = astSastResults.getNewLow() > 0 ? " (" + astSastResults.getNewLow() + " new)" : "";
	        String infoNew = astSastResults.getNewInfo() > 0 ? " (" + astSastResults.getNewInfo() + " new)" : "";

	        log.info("----------------------------Checkmarx Scan Results(CxOne SAST):-------------------------------");
	        log.info("High severity results: " + astSastResults.getHigh() + highNew);
	        log.info("Medium severity results: " + astSastResults.getMedium() + mediumNew);
	        log.info("Low severity results: " + astSastResults.getLow() + lowNew);
	        log.info("Information severity results: " + astSastResults.getInformation() + infoNew);
	        log.info("");
			if (astSastResults.getAstSastScanLink() != null)
				log.info("Scan results location: " + astSastResults.getAstSastScanLink());
	        log.info("------------------------------------------------------------------------------------------\n");
	    }
}
