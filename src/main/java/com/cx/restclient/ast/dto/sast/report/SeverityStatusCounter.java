package com.cx.restclient.ast.dto.sast.report;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SeverityStatusCounter {
    private String severity;
    private String status;
    private Integer counter;
}
