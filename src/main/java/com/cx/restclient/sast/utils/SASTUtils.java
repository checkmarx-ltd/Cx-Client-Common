package com.cx.restclient.sast.utils;

import static com.cx.restclient.common.CxPARAM.CX_REPORT_LOCATION;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.glassfish.jaxb.runtime.v2.JAXBContextFactory;
import org.slf4j.Logger;

import com.cx.restclient.configuration.CxScanConfig;
import com.cx.restclient.dto.CxVersion;
import com.cx.restclient.exception.CxClientException;
import com.cx.restclient.sast.dto.CxXMLResults;
import com.cx.restclient.sast.dto.SASTResults;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

/**
 * Created by Galn on 07/02/2018.
 */
public abstract class SASTUtils {

    public static CxXMLResults convertToXMLResult(byte[] cxReport) throws CxClientException {
        CxXMLResults reportObj = null;
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(cxReport)) {
        	 JAXBContextFactory jaxbContextFactory = new JAXBContextFactory();
              JAXBContext jaxbContext =  jaxbContextFactory.createContext(new Class[]{CxXMLResults.class}, null);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            reportObj = (CxXMLResults) unmarshaller.unmarshal(byteArrayInputStream);

        } catch (JAXBException | IOException e) {
            throw new CxClientException("Failed to parse xml report: " + e.getMessage(), e);
        }
        return reportObj;
    }

    public static void printSASTResultsToConsole(CxScanConfig config, SASTResults sastResults, boolean enableViolations, Logger log) {

        String highNew = sastResults.getNewHigh() > 0 ? " (" + sastResults.getNewHigh() + " new)" : "";
        String mediumNew = sastResults.getNewMedium() > 0 ? " (" + sastResults.getNewMedium() + " new)" : "";
        String lowNew = sastResults.getNewLow() > 0 ? " (" + sastResults.getNewLow() + " new)" : "";
        String criticalNew = sastResults.getNewCritical() > 0 ? " (" + sastResults.getNewCritical() + " new)" : "";
        String infoNew = sastResults.getNewInfo() > 0 ? " (" + sastResults.getNewInfo() + " new)" : "";
        
        CxVersion cxVersion = config.getCxVersion();
        String sastVersion = cxVersion != null ? cxVersion.getVersion() : null;
        
		if (sastVersion != null && !sastVersion.isEmpty()) {
			
			String[] versionComponents = sastVersion.split("\\.");
			
			if (versionComponents.length >= 2) {
				
				String currentVersion = versionComponents[0] + "." + versionComponents[1];
				float currentVersionFloat = Float.parseFloat(currentVersion);
				
		        String cxOrigin = config.getCxOrigin();				
        
				if(currentVersionFloat < Float.parseFloat("9.7")){
					if(config.getSastCriticalThreshold() != null && config.getSastCriticalThreshold() != 0) {
						log.warn("SAST Critical Threshold only works with SAST version >= 9.7");        	
					}
				}
        
				log.info("----------------------------Checkmarx Scan Results(CxSAST):-------------------------------");
        
        
				if (currentVersionFloat >= Float.parseFloat("9.7")) {
					log.info("Critical severity results: " + sastResults.getCritical() + criticalNew);
				}
			}
		}
        log.info("High severity results: " + sastResults.getHigh() + highNew);
        log.info("Medium severity results: " + sastResults.getMedium() + mediumNew);
        log.info("Low severity results: " + sastResults.getLow() + lowNew);
        log.info("Information severity results: " + sastResults.getInformation() + infoNew);
        log.info("");
		if (sastResults.getSastScanLink() != null)
			log.info("Scan results location: " + sastResults.getSastScanLink());
        log.info("------------------------------------------------------------------------------------------\n");
    }

    //PDF Report
  //This method is used for generate report for other file formats(CSV , XML, JSON etc) as well not only PDF file format.
    public static String writePDFReport(byte[] scanReport, File workspace, String pdfFileName, Logger log, String reportFormat) {
        try {
        	FileUtils.writeByteArrayToFile(new File(workspace + CX_REPORT_LOCATION, pdfFileName), scanReport);
            log.info("" +reportFormat + " Report Location: " + workspace + CX_REPORT_LOCATION+ File.separator+ pdfFileName);
        } catch (Exception e) {
        	log.error("Failed to write "+reportFormat+" report to workspace: ", e.getMessage());
            pdfFileName = "";
        }
        return pdfFileName;
    }

    // CLI Report/s
    public static void writeReport(byte[] scanReport, String reportName, Logger log) {
        try {
            File reportFile = new File(reportName);
            if (!reportFile.isAbsolute()) {
                reportFile = new File(System.getProperty("user.dir") + CX_REPORT_LOCATION + File.separator + reportFile);
            }

            if (!reportFile.getParentFile().exists()) {
                reportFile.getParentFile().mkdirs();
            }

            FileUtils.writeByteArrayToFile(reportFile, scanReport);
            log.info("report location: " + reportFile.getAbsolutePath());
        } catch (Exception e) {
            log.error("Failed to write report: ", e.getMessage());
        }
    }
}
