package com.cx.restclient.ast.dto.sast;

import com.cx.restclient.ast.dto.sast.report.AstSastSummaryResults;
import com.cx.restclient.ast.dto.sast.report.Finding;
import com.cx.restclient.dto.Results;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class AstSastResults implements Serializable, Results {
    private String scanId;
    private AstSastSummaryResults summary;
    private List<Finding> findings;
}
