package com.cx.restclient.ast.dto.common;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SummaryResults {
    private int criticalVulnerabilityCount = 0;
    private int highVulnerabilityCount = 0;
    private int mediumVulnerabilityCount = 0;
    private int lowVulnerabilityCount = 0;
}
