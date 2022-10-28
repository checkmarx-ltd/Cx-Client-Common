package com.cxone.restclient.ast.utils;

import static com.cxone.restclient.common.CxOnePARAM.CXONE_REPORT_LOCATION;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Collections;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

import com.cxone.restclient.ast.dto.ASTResults1;
import com.cxone.restclient.ast.dto.CxOneXMLResults;
import com.cxone.restclient.exception.CxOneClientException;
import com.sun.xml.bind.v2.ContextFactory;

public abstract class Astutils {
	
	public static CxOneXMLResults convertToXMLResult(byte[] cxOneReport) throws CxOneClientException {
        CxOneXMLResults reportObj = null;
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(cxOneReport)) {

              JAXBContext jaxbContext = ContextFactory.createContext(CxOneXMLResults.class.getPackage().getName(),                    
                       CxOneXMLResults.class.getClassLoader(), Collections.<String, Object>emptyMap());
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            reportObj = (CxOneXMLResults) unmarshaller.unmarshal(byteArrayInputStream);

        } catch (JAXBException | IOException e) {
            throw new CxOneClientException("Failed to parse xml report: " + e.getMessage(), e);
        }
        return reportObj;
    }

    public static void printASTResultsToConsole(ASTResults1 astResults, boolean enableViolations, Logger log) {

        String highNew = astResults.getNewHigh() > 0 ? " (" + astResults.getNewHigh() + " new)" : "";
        String mediumNew = astResults.getNewMedium() > 0 ? " (" + astResults.getNewMedium() + " new)" : "";
        String lowNew = astResults.getNewLow() > 0 ? " (" + astResults.getNewLow() + " new)" : "";
        String infoNew = astResults.getNewInfo() > 0 ? " (" + astResults.getNewInfo() + " new)" : "";

        log.info("----------------------------Checkmarx Scan Results(CxOneSAST):-------------------------------");
        log.info("High severity results: " + astResults.getHigh() + highNew);
        log.info("Medium severity results: " + astResults.getMedium() + mediumNew);
        log.info("Low severity results: " + astResults.getLow() + lowNew);
        log.info("Information severity results: " + astResults.getInformation() + infoNew);
        log.info("");
		if (astResults.getAstScanLink() != null)
			log.info("Scan results location: " + astResults.getAstScanLink());
        log.info("------------------------------------------------------------------------------------------\n");
    }

    //PDF Report
    public static String writePDFReport(byte[] scanReport, File workspace, String pdfFileName, Logger log) {
        try {
            FileUtils.writeByteArrayToFile(new File(workspace + CXONE_REPORT_LOCATION, pdfFileName), scanReport);
            log.info("PDF report location: " + workspace + CXONE_REPORT_LOCATION + File.separator + pdfFileName);
        } catch (Exception e) {
            log.error("Failed to write PDF report to workspace: ", e.getMessage());
            pdfFileName = "";
        }
        return pdfFileName;
    }

    // CLI Report/s
    public static void writeReport(byte[] scanReport, String reportName, Logger log) {
        try {
            File reportFile = new File(reportName);
            if (!reportFile.isAbsolute()) {
                reportFile = new File(System.getProperty("user.dir") + CXONE_REPORT_LOCATION + File.separator + reportFile);
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
