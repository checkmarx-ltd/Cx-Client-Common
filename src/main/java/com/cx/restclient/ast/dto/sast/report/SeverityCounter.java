package com.cx.restclient.ast.dto.sast.report;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SeverityCounter {
    private String severity;
    private Integer counter;
}
