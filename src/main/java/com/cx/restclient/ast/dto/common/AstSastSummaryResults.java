package com.cx.restclient.ast.dto.common;

import com.cx.restclient.ast.dto.sast.report.StatusCounter;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class AstSastSummaryResults extends SummaryResults implements Serializable {
    private List<StatusCounter> statusCounters;
    private int totalCounter;
}
