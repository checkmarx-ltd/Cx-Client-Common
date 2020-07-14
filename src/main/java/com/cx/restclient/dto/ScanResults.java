package com.cx.restclient.dto;


import com.cx.restclient.exception.CxClientException;
import com.cx.restclient.osa.dto.OSAResults;
import com.cx.restclient.sast.dto.SASTResults;
import com.cx.restclient.ast.dto.common.ASTResults;
import com.cx.restclient.ast.dto.sca.AstScaResults;

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
        return (OSAResults)resultsMap.get(ScannerType.OSA);
    }

    public ASTResults getAstResults() {
        return (ASTResults)resultsMap.get(ScannerType.AST_SAST);
    }

    public AstScaResults getScaResults() {
        return (AstScaResults)resultsMap.get(ScannerType.AST_SCA);
    }


    public SASTResults getSastResults() {
        return (SASTResults)resultsMap.get(ScannerType.SAST);

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
