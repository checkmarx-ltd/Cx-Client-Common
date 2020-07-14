package com.cx.restclient.ast.dto.sca.report;

import com.cx.restclient.dto.scansummary.Severity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This entity is called vulnerability in SCA API, but here it is called Finding for consistency.
 * Indicates a specific type of vulnerability detected in a specific package.
 */
@Getter
@Setter
public class Finding implements Serializable {
    public String id;
    public String cveName;
    public double score;
    public Severity severity;
    public String publishDate;
    public List<String> references = new ArrayList<>();
    public String description;
    public String recommendations;
    public String packageId;
    public String similarityId;
    public String fixResolutionText;
    public boolean isIgnored;
}
