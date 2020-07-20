package com.cx.restclient.ast.dto.sast.report;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ScansSummary {
    private String scanId;
    private List<SeverityCounter> severityCounters = new ArrayList<>();
    private List<StatusCounter> statusCounters = new ArrayList<>();
    private Integer totalCounter;
}
