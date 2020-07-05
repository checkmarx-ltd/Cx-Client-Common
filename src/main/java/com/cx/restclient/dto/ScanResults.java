package com.cx.restclient.dto;


import com.cx.restclient.exception.CxClientException;
import com.cx.restclient.osa.dto.OSAResults;
import com.cx.restclient.sast.dto.SASTResults;
import com.cx.restclient.sca.dto.ASTResults;
import com.cx.restclient.sca.dto.SCAResults;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ScanResults implements Serializable, Results {
    
    Map<ScannerType, Results> resultsMap = new HashMap<>();
    
    private Exception sastCreateException = null;
    private Exception sastWaitException = null;
    private Exception osaCreateException = null;
    private Exception osaWaitException = null;
    private Exception generalException = null;

 
    public ScanResults() {
    }
    
    public Map<ScannerType, Results> getResults(){
        return resultsMap;
    }
    
    public void put(ScannerType type, Results results) {
        if(resultsMap.containsKey(type)){
            throw new CxClientException("Results already contain type " + type);
        }
        resultsMap.put(type, results);
    }

    public Map<ScannerType, Results> getResultsMap() {
        return resultsMap;
    }

    public Results get(ScannerType type) {
        return resultsMap.get(type);
    }


    public OSAResults getOsaResults() {
        return resultsMap.containsKey(ScannerType.OSA) ? (OSAResults)resultsMap.get(ScannerType.OSA) : null;
    }

    public ASTResults getAstResults() {
        return resultsMap.containsKey(ScannerType.AST) ? (ASTResults)resultsMap.get(ScannerType.AST) : null;
    }

    public SCAResults getScaResults() {
        return resultsMap.containsKey(ScannerType.SCA) ? (SCAResults)resultsMap.get(ScannerType.SCA) : null;
    }


    public SASTResults getSastResults() {
        return resultsMap.containsKey(ScannerType.SAST) ? (SASTResults)resultsMap.get(ScannerType.SCA) : null;

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
