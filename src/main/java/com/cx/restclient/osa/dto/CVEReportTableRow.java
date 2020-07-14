package com.cx.restclient.osa.dto;

import com.cx.restclient.ast.dto.sca.report.Finding;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

import static com.cx.restclient.common.ShragaUtils.formatDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CVEReportTableRow implements Serializable {

    private String name;
    private String severity;
    private String publishDate;
    private String libraryName;
    private String state;

    public CVEReportTableRow(String name, String severity, String publishDate, String libraryName, String state) {
        this.name = name;
        this.severity = severity;
        this.publishDate = publishDate;
        this.libraryName = libraryName;
        this.state = state;
    }

    public CVEReportTableRow(CVE cve) {
        this.state = cve.getState().getName();
        this.name = cve.getCveName();
        this.publishDate = cve.getPublishDate();
        this.libraryName = cve.getLibraryId();

    }

    public CVEReportTableRow(Finding finding){
        this.state = finding.isIgnored()?"NOT_EXPLOITABLE":"EXPLOITABLE";
        this.name = finding.getId();
        this.publishDate = formatDate(finding.getPublishDate(), "yyyy-MM-dd'T'HH:mm:ss", "dd/MM/yy");
        this.libraryName = finding.getPackageId();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public String getLibraryName() {
        return libraryName;
    }

    public void setLibraryName(String libraryName) {
        this.libraryName = libraryName;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
