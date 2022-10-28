package com.cxone.restclient.ast.dto;

import com.cx.restclient.dto.BaseStatus;
import com.cx.restclient.sast.dto.CxDateAndTimeObj;
import com.cx.restclient.sast.dto.CxLinkObj;
import com.cx.restclient.sast.dto.CxNameObj;
import com.cx.restclient.sast.dto.CxScanStateObj;
import com.cx.restclient.sast.dto.CxValueObj;
import com.cx.restclient.sast.dto.Project;
import com.cxone.restclient.ast.dto.ResponseQueueScanStatus;
import com.cxone.restclient.ast.dto.ResponseAstScanStatus;

public class ResponseAstScanStatus extends BaseStatus{
	
	 private long id;
	    private Project project;
	    private CxNameObj status;
	    private CxValueObj scanType;
	    private String comment;
	    private CxDateAndTimeObj dateAndTime;
	    private CxLinkObj resultsStatistics;
	    private CxScanStateObj scanState;
	    private String owner;
	    private String origin;
	    private String initiatorName;
	    private long owningTeamId;
	    private boolean isPublic;
	    private boolean isLocked;
	    private boolean isIncremental;
	    private long scanRisk;
	    private long scanRiskSeverity;
	    private CxNameObj engineServer;
	    private CxValueObj finishedScanStatus;

	    public long getId() {
	        return id;
	    }

	    public void setId(long id) {
	        this.id = id;
	    }

	    public Project getProject() {
	        return project;
	    }

	    public void setProject(Project project) {
	        this.project = project;
	    }

	    public CxNameObj getStatus() {
	        return status;
	    }

	    public void setStatus(CxNameObj status) {
	        this.status = status;
	    }

	    public CxValueObj getScanType() {
	        return scanType;
	    }

	    public void setScanType(CxValueObj scanType) {
	        this.scanType = scanType;
	    }

	    public String getComment() {
	        return comment;
	    }

	    public void setComment(String comment) {
	        this.comment = comment;
	    }

	    public CxDateAndTimeObj getDateAndTime() {
	        return dateAndTime;
	    }

	    public void setDateAndTime(CxDateAndTimeObj dateAndTime) {
	        this.dateAndTime = dateAndTime;
	    }

	    public CxLinkObj getResultsStatistics() {
	        return resultsStatistics;
	    }

	    public void setResultsStatistics(CxLinkObj resultsStatistics) {
	        this.resultsStatistics = resultsStatistics;
	    }

	    public CxScanStateObj getScanState() {
	        return scanState;
	    }

	    public void setScanState(CxScanStateObj scanState) {
	        this.scanState = scanState;
	    }

	    public String getOwner() {
	        return owner;
	    }

	    public void setOwner(String owner) {
	        this.owner = owner;
	    }

	    public String getOrigin() {
	        return origin;
	    }

	    public void setOrigin(String origin) {
	        this.origin = origin;
	    }

	    public String getInitiatorName() {
	        return initiatorName;
	    }

	    public void setInitiatorName(String initiatorName) {
	        this.initiatorName = initiatorName;
	    }

	    public long getOwningTeamId() {
	        return owningTeamId;
	    }

	    public void setOwningTeamId(long owningTeamId) {
	        this.owningTeamId = owningTeamId;
	    }

	    public boolean isPublic() {
	        return isPublic;
	    }

	    public void setPublic(boolean aPublic) {
	        isPublic = aPublic;
	    }

	    public boolean isLocked() {
	        return isLocked;
	    }

	    public void setLocked(boolean locked) {
	        isLocked = locked;
	    }

	    public boolean isIncremental() {
	        return isIncremental;
	    }

	    public void setIncremental(boolean incremental) {
	        isIncremental = incremental;
	    }

	    public long getScanRisk() {
	        return scanRisk;
	    }

	    public void setScanRisk(long scanRisk) {
	        this.scanRisk = scanRisk;
	    }

	    public long getScanRiskSeverity() {
	        return scanRiskSeverity;
	    }

	    public void setScanRiskSeverity(long scanRiskSeverity) {
	        this.scanRiskSeverity = scanRiskSeverity;
	    }

	    public CxNameObj getEngineServer() {
	        return engineServer;
	    }

	    public void setEngineServer(CxNameObj engineServer) {
	        this.engineServer = engineServer;
	    }

	    public CxValueObj getFinishedScanStatus() {
	        return finishedScanStatus;
	    }

	    public void setFinishedScanStatus(CxValueObj finishedScanStatus) {
	        this.finishedScanStatus = finishedScanStatus;
	    }
	    public ResponseQueueScanStatus convertResponseAstScanStatusToResponseQueueScanStatus(ResponseAstScanStatus responseastScanStatus){
	        ResponseQueueScanStatus tempResponseQueueScanStatus =  new ResponseQueueScanStatus();

	        tempResponseQueueScanStatus.setId(this.id);
	        CxOneValueObj stage = new CxOneValueObj();
	        stage.setId(responseastScanStatus.status.getId());
	        stage.setValue(responseastScanStatus.status.getName());
	        tempResponseQueueScanStatus.setStage(stage);
	        tempResponseQueueScanStatus.setStageDetails("");
	        tempResponseQueueScanStatus.setProject(responseastScanStatus.project);
	        tempResponseQueueScanStatus.setTotalPercent(100);
	        tempResponseQueueScanStatus.setStagePercent(100);
	        tempResponseQueueScanStatus.setBaseStatus(responseastScanStatus.getBaseStatus());

	        return tempResponseQueueScanStatus;
	    }

}
