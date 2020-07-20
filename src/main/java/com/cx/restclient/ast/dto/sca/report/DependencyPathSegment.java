package com.cx.restclient.ast.dto.sca.report;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class DependencyPathSegment implements Serializable {
    String id;
    String name;
    String version;
    boolean isResolved;
    boolean isDevelopment;
}
