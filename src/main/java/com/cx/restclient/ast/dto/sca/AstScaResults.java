package com.cx.restclient.ast.dto.sca;

import com.cx.restclient.ast.dto.sca.report.AstScaSummaryResults;
import com.cx.restclient.ast.dto.sca.report.Finding;
import com.cx.restclient.ast.dto.sca.report.Package;
import com.cx.restclient.ast.dto.sca.report.PolicyEvaluation;
import com.cx.restclient.dto.Results;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AstScaResults extends Results implements Serializable {
    private String scanId;
    private String reportId;
    private byte[] rawXMLReport;
    private String scaPDFLink;
    public String getScaPDFLink() {
		return scaPDFLink;
	}

	public void setScaPDFLink(String scaPDFLink) {
		this.scaPDFLink = scaPDFLink;
	}

	public byte[] getRawXMLReport() {
		return rawXMLReport;
	}

	public void setRawXMLReport(byte[] rawXMLReport) {
		this.rawXMLReport = rawXMLReport;
	}

	public byte[] getPDFReport() {
		return PDFReport;
	}

	public void setPDFReport(byte[] pDFReport) {
		PDFReport = pDFReport;
	}

	public String getPdfFileName() {
		return pdfFileName;
	}

	public void setPdfFileName(String pdfFileName) {
		this.pdfFileName = pdfFileName;
	}

	private byte[] PDFReport;
    private String pdfFileName;

    public String getScanId() {
		return scanId;
	}

	public void setScanId(String scanId) {
		this.scanId = scanId;
	}

	public String getReportId() {
		return reportId;
	}

	public void setReportId(String reportId) {
		this.reportId = reportId;
	}

	public AstScaSummaryResults getSummary() {
		return summary;
	}

	public void setSummary(AstScaSummaryResults summary) {
		this.summary = summary;
	}

	public String getWebReportLink() {
		return webReportLink;
	}

	public void setWebReportLink(String webReportLink) {
		this.webReportLink = webReportLink;
	}

	public List<Finding> getFindings() {
		return findings;
	}

	public void setFindings(List<Finding> findings) {
		this.findings = findings;
	}

	public List<Package> getPackages() {
		return packages;
	}

	public void setPackages(List<Package> packages) {
		this.packages = packages;
	}

	public boolean isScaResultReady() {
		return scaResultReady;
	}

	public void setScaResultReady(boolean scaResultReady) {
		this.scaResultReady = scaResultReady;
	}

	public int getNonVulnerableLibraries() {
		return nonVulnerableLibraries;
	}

	public void setNonVulnerableLibraries(int nonVulnerableLibraries) {
		this.nonVulnerableLibraries = nonVulnerableLibraries;
	}

	public int getVulnerableAndOutdated() {
		return vulnerableAndOutdated;
	}

	public void setVulnerableAndOutdated(int vulnerableAndOutdated) {
		this.vulnerableAndOutdated = vulnerableAndOutdated;
	}

	public List<PolicyEvaluation> getPolicyEvaluations() {
		return policyEvaluations;
	}

	public void setPolicyEvaluations(List<PolicyEvaluation> policyEvaluations) {
		this.policyEvaluations = policyEvaluations;
	}

	public boolean isPolicyViolated() {
		return policyViolated;
	}

	public void setPolicyViolated(boolean policyViolated) {
		this.policyViolated = policyViolated;
	}

	public boolean isBreakTheBuild() {
		return breakTheBuild;
	}

	public void setBreakTheBuild(boolean breakTheBuild) {
		this.breakTheBuild = breakTheBuild;
	}

	private AstScaSummaryResults summary;
    private String webReportLink;
    private List<Finding> findings;
    private List<Package> packages;
    private boolean scaResultReady;
    private int nonVulnerableLibraries;
    private int vulnerableAndOutdated;
    private List<PolicyEvaluation> policyEvaluations;
    private boolean policyViolated;
    private boolean breakTheBuild;

    public void calculateVulnerableAndOutdatedPackages() {
        int sum;
        if (this.packages != null) {
            for (Package pckg : this.packages) {
                sum = pckg.getCriticalVulnerabilityCount() + pckg.getHighVulnerabilityCount() + pckg.getMediumVulnerabilityCount() + pckg.getLowVulnerabilityCount();                if (sum == 0) {
                    this.nonVulnerableLibraries++;
                } else if (sum > 0 && pckg.isOutdated()) {
                    this.vulnerableAndOutdated++;
                }
            }
        }
    }	
    
}
