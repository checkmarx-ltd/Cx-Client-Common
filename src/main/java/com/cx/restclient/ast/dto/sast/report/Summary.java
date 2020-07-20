package com.cx.restclient.ast.dto.sast.report;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Summary {
    private List<ScansSummary> scansSummaries = new ArrayList<>();
    private Integer totalCount;
}
