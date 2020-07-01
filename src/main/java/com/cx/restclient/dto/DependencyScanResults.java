package com.cx.restclient.dto;

import com.cx.restclient.osa.dto.OSAResults;
import com.cx.restclient.sca.dto.SCAResults;

import java.io.Serializable;

public class DependencyScanResults implements Serializable, IResults {
    private OSAResults osaResults;
    private SCAResults scaResults;

    public void setOsaResults(OSAResults osaResults) {
        this.osaResults = osaResults;
    }

    public OSAResults getOsaResults() {
        return osaResults;
    }

    public void setScaResults(SCAResults scaResults) {
        this.scaResults = scaResults;
    }

    public SCAResults getScaResults() {
        return scaResults;
    }
}
