package com.cx.restclient.ast.dto.common;

import lombok.Builder;
import lombok.Getter;
import java.util.Map;


@Builder
@Getter
public class ProjectToScan {
    private String id;
    private String type;
    private ScanStartHandler handler;
}
