package com.cx.restclient.ast.dto.sca;

import com.cx.restclient.ast.dto.sca.report.Package;
import com.cx.restclient.ast.dto.sca.report.*;
import com.cx.restclient.dto.Results;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AstScaResults implements Serializable, Results {
    private String scanId;
    private AstScaSummaryResults summary;
    private String webReportLink;
    private List<Finding> findings;
    private List<Package> packages;
    private boolean scaResultReady;
}
