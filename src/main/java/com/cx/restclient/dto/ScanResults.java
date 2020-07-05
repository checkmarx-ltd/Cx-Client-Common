package com.cx.restclient.dto;


import com.cx.restclient.SCAClient;
import com.cx.restclient.osa.dto.OSAResults;
import com.cx.restclient.sast.dto.SASTResults;
import com.cx.restclient.sca.dto.ASTResults;
import com.cx.restclient.sca.dto.SCAResults;

import java.io.Serializable;

public class ScanResults implements Serializable, IResults {
    private SASTResults sastResults ;
    private OSAResults osaResults ;
    private SCAResults scaResults ;
    private ASTResults astResults ;
    
    private Exception sastCreateException = null;
    private Exception sastWaitException = null;
    private Exception osaCreateException = null;
    private Exception osaWaitException = null;
    private Exception generalException = null;

    public ScanResults(ScannerType type) {
        build(type);
    }
    public ScanResults() {
 
    }

    public ScanResults(ScanResults scanResults) {
        build(scanResults);
    }

    public void build(ScanResults scanResults) {
        this.sastResults = scanResults.getSastResults();
        this.osaResults = scanResults.getOsaResults();
        this.scaResults = scanResults.getScaResults();
        this.astResults = scanResults.getAstResults();
    }

    public ScanResults(SASTResults sastResults) {
        this.sastResults = sastResults;
    }
    public ScanResults(OSAResults osaResults) {
        this.osaResults = osaResults;
    }

    public ScanResults(SCAResults scaResults) {
        this.scaResults = scaResults;
    }
    public ScanResults(ASTResults astResults) {
        this.astResults = astResults;
    }
    
    public ScanResults build(ScannerType type){
        if(ScannerType.SAST.equals(type)){
            sastResults = new SASTResults();
        }
        if(ScannerType.OSA.equals(type)){
            osaResults = new OSAResults();
        }
        if(ScannerType.SCA.equals(type)){
            scaResults = new SCAResults();
        }
        if(ScannerType.AST.equals(type)){
            astResults = new ASTResults();
        }
        if(ScannerType.ALL.equals(type)){
            sastResults = new SASTResults();
            osaResults = new OSAResults();
            scaResults = new SCAResults();
            astResults = new ASTResults();
        }
        return this;
    }
    
    public OSAResults getOsaResults() {
        return osaResults;
    }

    public ASTResults getAstResults() {
        return astResults;
    }

    public void setAstResults(ASTResults astResults) {
        this.astResults = astResults;
    }

    public void setOsaResults(OSAResults osaResults) {
        this.osaResults = osaResults;
    }

    public SCAResults getScaResults() {
        return scaResults;
    }

    public void setScaResults(SCAResults scaResults) {
        this.scaResults = scaResults;
    }

    public SASTResults getSastResults() {
        return sastResults;
    }

    public void setSastResults(SASTResults sastResults) {
        this.sastResults = sastResults;
    }

    public Exception getSastCreateException() {
        return sastCreateException;
    }

    public void setSastCreateException(Exception sastCreateException) {
        this.sastCreateException = sastCreateException;
    }

    public Exception getSastWaitException() {
        return sastWaitException;
    }

    public void setSastWaitException(Exception sastWaitException) {
        this.sastWaitException = sastWaitException;
    }

    public Exception getOsaCreateException() {
        return osaCreateException;
    }

    public void setOsaCreateException(Exception osaCreateException) {
        this.osaCreateException = osaCreateException;
    }

    public Exception getOsaWaitException() {
        return osaWaitException;
    }

    public void setOsaWaitException(Exception osaWaitException) {
        this.osaWaitException = osaWaitException;
    }

    public Exception getGeneralException() {
        return generalException;
    }

    public void setGeneralException(Exception generalException) {
        this.generalException = generalException;
    }


}
