package com.cx.restclient.ast.dto.sast.report;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatusCounter {
    private String status;
    private Integer counter;
}
