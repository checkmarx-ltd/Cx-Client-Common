package com.cx.restclient.sast.utils;

import static com.cx.restclient.common.CxPARAM.CX_REPORT_LOCATION;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Collections;

import org.apache.commons.io.FileUtils;
import org.glassfish.jaxb.runtime.v2.ContextFactory;
import org.slf4j.Logger;

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

              JAXBContext jaxbContext = ContextFactory.createContext(CxXMLResults.class.getPackage().getName(),                    
                       CxXMLResults.class.getClassLoader(), Collections.<String, Object>emptyMap());
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            reportObj = (CxXMLResults) unmarshaller.unmarshal(byteArrayInputStream);

        } catch (JAXBException | IOException e) {
            throw new CxClientException("Failed to parse xml report: " + e.getMessage(), e);
        }
        return reportObj;
    }

    public static void printSASTResultsToConsole(SASTResults sastResults, boolean enableViolations, Logger log) {

        String highNew = sastResults.getNewHigh() > 0 ? " (" + sastResults.getNewHigh() + " new)" : "";
        String mediumNew = sastResults.getNewMedium() > 0 ? " (" + sastResults.getNewMedium() + " new)" : "";
        String lowNew = sastResults.getNewLow() > 0 ? " (" + sastResults.getNewLow() + " new)" : "";
        String infoNew = sastResults.getNewInfo() > 0 ? " (" + sastResults.getNewInfo() + " new)" : "";

        log.info("----------------------------Checkmarx Scan Results(CxSAST):-------------------------------");
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
    public static String writePDFReport(byte[] scanReport, File workspace, String pdfFileName, Logger log) {
        try {
            FileUtils.writeByteArrayToFile(new File(workspace + CX_REPORT_LOCATION, pdfFileName), scanReport);
            log.info("PDF report location: " + workspace + CX_REPORT_LOCATION + File.separator + pdfFileName);
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
