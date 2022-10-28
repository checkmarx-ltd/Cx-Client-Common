package com.cxone.restclient.cxOneArm.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Galn on 7/5/2018.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Policy implements Serializable {
    long policyId;
    String policyName;
    String ruleName;
    String firstDetectionDate;
    private List<Violation> violations = new ArrayList<Violation>();

    public Policy() {
    }


    public Policy(long policyId, String policyName, String ruleName, List<Violation> violations, String firstDate) {
        this.policyId = policyId;
        this.policyName = policyName;
        this.ruleName = ruleName;
        this.violations = violations;
        this.firstDetectionDate = firstDate;
    }

    public long getPolicyId() {
        return policyId;
    }

    public void setPolicyId(long policyId) {
        this.policyId = policyId;
    }

    public String getPolicyName() {
        return policyName;
    }

    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }

    public List<Violation> getViolations() {
        return violations;
    }

    public void setViolations(List<Violation> violations) {
        this.violations = violations;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getFirstDetectionDate() {
        return firstDetectionDate;
    }

    public void setFirstDetectionDate(String firstDetectionDate) {
        this.firstDetectionDate = firstDetectionDate;
    }
}
