package com.cx.restclient.ast.dto.sca.report;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class DependencyPathSegment implements Serializable {
    public String id;
    public String name;
    public String version;
    public boolean isResolved;
    public boolean isDevelopment;
}
